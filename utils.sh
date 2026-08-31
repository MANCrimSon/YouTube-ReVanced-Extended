#!/usr/bin/env bash

MODULE_TEMPLATE_DIR="module"
CWD=$(pwd)
TEMP_DIR="temp"
BIN_DIR="bin"
BUILD_DIR="build"
# Separate from TEMP_DIR (which also holds fast-churning CLI/patches jars and
# in-progress patched apks - none of that should persist) so CI can cache
# just this directory across runs: stock apks are large, slow, and the least
# reliable thing to (re-)download, but change far less often than patches do.
STOCK_CACHE_DIR="stock-apks"
DL_SRCS=("direct" "archive" "apkpure" "apkmirror" "uptodown")

if [ "${GITHUB_TOKEN-}" ]; then GH_HEADER="Authorization: token ${GITHUB_TOKEN}"; else GH_HEADER=; fi
NEXT_VER_CODE=${NEXT_VER_CODE:-$(date +'%Y%m%d')}
OS=$(uname -o)

toml_prep() {
	if [ ! -f "$1" ]; then return 1; fi
	if [ "${1##*.}" == toml ]; then
		__TOML__=$($TOML --output json --file "$1" .)
	elif [ "${1##*.}" == json ]; then
		__TOML__=$(cat "$1")
	else abort "config extension not supported"; fi
}
toml_get_table_names() { jq -r -e 'to_entries[] | select(.value | type == "object") | .key' <<<"$__TOML__"; }
toml_get_table_main() { jq -r -e 'to_entries | map(select(.value | type != "object")) | from_entries' <<<"$__TOML__"; }
toml_get_table() { jq -r -e ".\"${1}\"" <<<"$__TOML__"; }
toml_get() {
	local op quote_placeholder=$'\001'
	op=$(jq -r ".\"${2}\" | values" <<<"$1")
	if [ "$op" ]; then
		op="${op#"${op%%[![:space:]]*}"}"
		op="${op%"${op##*[![:space:]]}"}"
		op=${op//\\\'/$quote_placeholder}
		op=${op//"''"/$quote_placeholder}
		op=${op//"'"/'"'}
		op=${op//$quote_placeholder/$'\''}
		echo "$op"
	else return 1; fi
}

pr() { echo -e "\033[0;32m[+] ${1}\033[0m"; }
epr() {
	echo >&2 -e "\033[0;31m[-] ${1}\033[0m"
	if [ "${GITHUB_REPOSITORY-}" ]; then echo >&2 -e "::error::utils.sh [-] ${1}\n"; fi
}
wpr() {
	echo >&2 -e "\033[0;33m[!] ${1}\033[0m"
	if [ "${GITHUB_REPOSITORY-}" ]; then echo >&2 -e "::warning::utils.sh [!] ${1}\n"; fi
}

_clean_tmp() {
	rm -rf ./${TEMP_DIR}/*tmp.* ./${TEMP_DIR}/*tmp_* ./${TEMP_DIR}/*/*tmp.* ./${TEMP_DIR}/*-temporary-files ./*-temporary-files
}

abort() {
	epr "ABORT: ${1-}"
	_clean_tmp
	trap - SIGTERM SIGINT EXIT
	kill -9 -- -$$ 2>/dev/null
	exit 1
}
java() {
	if [ "${JAVA_HOME_21_X64-}" ]; then
		env -i JAVA_HOME="$JAVA_HOME_21_X64" "$JAVA_HOME_21_X64"/bin/java --enable-native-access=ALL-UNNAMED "$@"
	else
		env -i java --enable-native-access=ALL-UNNAMED "$@"
	fi
}

get_prebuilts() {
	local cli_src=$1 cli_ver=$2 patches_src=$3 patches_ver=$4
	pr "Getting prebuilts (${patches_src%/*})" >&2
	local cl_dir=${patches_src%/*}
	cl_dir=${TEMP_DIR}/${cl_dir,,}-rv
	[ -d "$cl_dir" ] || mkdir "$cl_dir"

	for src_ver in "Patches $patches_src $patches_ver" "CLI $cli_src $cli_ver"; do
		set -- $src_ver
		local tag=$1 src=$2 ver=${3-}

		local dir=${src%/*}
		dir=${TEMP_DIR}/${dir,,}-rv
		[ -d "$dir" ] || mkdir "$dir"

		local rv_rel="https://api.github.com/repos/${src}/releases" name_ver
		if [ "$ver" = "dev" ]; then
			local resp
			resp=$(gh_req "$rv_rel" -) || return 1
			ver=$(jq -e -r '.[] | .tag_name' <<<"$resp" | get_highest_ver) || return 1
		fi
		if [ "$ver" = "latest" ]; then
			rv_rel+="/latest"
			name_ver="*"
		else
			rv_rel+="/tags/${ver}"
			name_ver="$ver"
		fi

		local file
		if [ "$tag" = "CLI" ]; then
			file=$(find "$dir" -maxdepth 1 -name "*cli-${name_ver#v}*.jar" -o -name "*desktop-${name_ver#v}*.jar" -type f 2>/dev/null)
			local grab_cl=false
		elif [ "$tag" = "Patches" ]; then
			file=$(find "$dir" -maxdepth 1 -name "*patches-${name_ver#v}.*" -type f 2>/dev/null)
			local grab_cl=true
		else abort unreachable; fi

		local url tag_name matches
		# Guarded: under set -e/pipefail, this would otherwise abort the whole
		# script whenever $file is empty (the normal case - no local copy yet)
		# and grep finds nothing to match, exactly like the extensions_ext case
		# below. No match here just means "go fetch it".
		if [ "$ver" = "latest" ]; then
			file=$(grep -v '/[^/]*dev[^/]*$' <<<"$file" | head -1) || :
		else
			file=$(grep "/[^/]*${ver#v}[^/]*\$" <<<"$file" | head -1) || :
		fi
		if [ -z "$file" ]; then
			local resp asset name
			resp=$(gh_req "$rv_rel" -) || return 1
			tag_name=$(jq -r '.tag_name' <<<"$resp") || return 1
			matches=$(jq -e '.assets | map(select(.name | (endswith("asc") or endswith("json")) | not))' <<<"$resp") || return 1
			if [ "$(jq 'length' <<<"$matches")" -gt 1 ]; then
				local matches_new
				matches_new=$(jq -e -r 'map(select(.name | contains("-dev") | not))' <<<"$matches")
				if [ "$(jq 'length' <<<"$matches_new")" -eq 1 ]; then
					matches=$matches_new
				fi
			fi
			if [ "$(jq 'length' <<<"$matches")" -eq 0 ]; then
				epr "No asset was found"
				return 1
			elif [ "$(jq 'length' <<<"$matches")" -ne 1 ]; then
				wpr "More than 1 asset was found for this release. Falling back to the first one found..."
			fi
			asset=$(jq -r ".[0]" <<<"$matches")
			url=$(jq -r .url <<<"$asset")
			name=$(jq -r .name <<<"$asset")
			file="${dir}/${name}"
			gh_dl "$file" "$url" >&2 || return 1
			echo "$tag: $(cut -d/ -f1 <<<"$src")/${name}  " >>"${cl_dir}/changelog.md"
		else
			grab_cl=false
			name=$(basename "$file")
			tag_name=$(cut -d'-' -f3- <<<"$name")
			tag_name=v${tag_name%.*}
		fi

		if [ "$tag" = "Patches" ]; then
			if [ "$grab_cl" = true ]; then echo -e "[Changelog](https://github.com/${src}/releases/tag/${tag_name})\n" >>"${cl_dir}/changelog.md"; fi
			if [ "$REMOVE_RV_INTEGRATIONS_CHECKS" = true ]; then
				local extensions_ext
				# Under set -e/pipefail, an unguarded "$(cmd1 | cmd2)" assignment
				# aborts the whole script the instant grep finds no match - which
				# happens whenever a bundle simply has no extensions/shared.* to
				# patch (e.g. some patches sources never had it, or don't anymore).
				# That's a "nothing to do here" case, not a fatal error.
				extensions_ext=$(unzip -l "${file}" "extensions/shared.*" 2>/dev/null | grep -o "shared\..*" || :)
				extensions_ext="${extensions_ext#*.}"
				if [ -z "$extensions_ext" ]; then
					pr "'${file}' has no extensions/shared.* to patch, skipping revanced-integrations check removal"
				elif ! (
					mkdir -p "${file}-zip" || return 1
					unzip -qo "${file}" -d "${file}-zip" || return 1
					java -cp "${BIN_DIR}/paccer.jar:${BIN_DIR}/dexlib2.jar" com.jhc.Main "${file}-zip/extensions/shared.${extensions_ext}" "${file}-zip/extensions/shared-patched.${extensions_ext}" || return 1
					mv -f "${file}-zip/extensions/shared-patched.${extensions_ext}" "${file}-zip/extensions/shared.${extensions_ext}" || return 1
					rm "${file}" || return 1
					cd "${file}-zip" || abort
					zip -0rq "${CWD}/${file}" . || return 1
				) >&2; then
					echo >&2 "Patching revanced-integrations failed"
				fi
				rm -r "${file}-zip" 2>/dev/null || :
			fi
		fi
		echo -n "$file "
	done
	echo
}

set_prebuilts() {
	APKSIGNER="${BIN_DIR}/apksigner.jar"
	local arch
	arch=$(uname -m)
	if [ "$arch" = aarch64 ]; then arch=arm64; elif [ "${arch:0:5}" = "armv7" ]; then arch=arm; fi
	HTMLQ="${BIN_DIR}/htmlq/htmlq-${arch}"
	AAPT2="${BIN_DIR}/aapt2/aapt2-${arch}"
	TOML="${BIN_DIR}/toml/tq-${arch}"
}

# Resolves and downloads a single .mpp add-on bundle from a GitHub repo's
# releases. Always picks the actual highest tag (via get_highest_ver, so
# dev/prerelease tags are included and compared correctly) rather than
# GitHub's "/releases/latest", which only ever returns the newest
# non-prerelease. Caches the downloaded file per resolved version, same as
# get_prebuilts() does for the main patches jar. Echoes the local file path.
get_addon() {
	local src=$1
	pr "Getting addon (${src})" >&2
	local dir="${TEMP_DIR}/addons/${src,,}"
	mkdir -p "$dir"

	local resp best_tag
	resp=$(gh_req "https://api.github.com/repos/${src}/releases" -) || return 1
	best_tag=$(jq -e -r '.[].tag_name' <<<"$resp" | get_highest_ver) || return 1

	local file
	file=$(find "$dir" -maxdepth 1 -name "*-${best_tag#v}.*" -type f 2>/dev/null | head -1)
	if [ -z "$file" ]; then
		local release matches asset name url
		release=$(jq -e -r --arg t "$best_tag" '.[] | select(.tag_name == $t)' <<<"$resp") || return 1
		matches=$(jq -e '[.assets[] | select(.name | endswith(".mpp"))]' <<<"$release") || return 1
		if [ "$(jq 'length' <<<"$matches")" -eq 0 ]; then
			epr "No .mpp asset found for addon '$src' (${best_tag})"
			return 1
		fi
		asset=$(jq -r ".[0]" <<<"$matches")
		url=$(jq -r .url <<<"$asset")
		name=$(jq -r .name <<<"$asset")
		file="${dir}/${name}"
		gh_dl "$file" "$url" >&2 || return 1
		echo "Addon: $(cut -d/ -f1 <<<"$src")/${name}  " >>"${TEMP_DIR}/addons/changelog.md"
		echo -e "[Changelog](https://github.com/${src}/releases/tag/${best_tag})\n" >>"${TEMP_DIR}/addons/changelog.md"
	fi
	echo "$file"
}

config_update() {
	# No prior state (e.g. first run ever, or 'update' branch has no state.md
	# yet) just means every table looks new below - a one-time full rebuild.
	touch state.md
	# Caches the resolved latest-patches-asset-name per source+version, so
	# tables sharing a source only hit the GitHub API once per run. Failure to
	# resolve is cached too (empty string) - matches the pre-existing "skip
	# this table for now" behavior on a transient API error.
	declare -A resolved
	: >"$TEMP_DIR"/skipped
	local upped=()
	local prcfg=false
	for table_name in $(toml_get_table_names); do
		if [ -z "$table_name" ]; then continue; fi
		t=$(toml_get_table "$table_name")
		enabled=$(toml_get "$t" enabled) || enabled=true
		if [ "$enabled" = "false" ]; then continue; fi
		PATCHES_SRC=$(toml_get "$t" patches-source) || PATCHES_SRC=$DEF_PATCHES_SRC
		PATCHES_VER=$(toml_get "$t" patches-version) || PATCHES_VER=$DEF_PATCHES_VER
		local cache_key="$PATCHES_SRC/$PATCHES_VER" last_patches
		if [[ -v resolved["$cache_key"] ]]; then
			last_patches=${resolved["$cache_key"]}
		else
			local rv_rel="https://api.github.com/repos/${PATCHES_SRC}/releases"
			if [ "$PATCHES_VER" = "dev" ]; then
				# Don't trust the API's own ordering (release #0) - pick the
				# release whose tag is actually highest, same as get_prebuilts
				# does, so this agrees with what actually gets built.
				local dev_resp best_tag
				dev_resp=$(gh_req "$rv_rel" -) || continue
				best_tag=$(jq -e -r '.[].tag_name' <<<"$dev_resp" | get_highest_ver) || continue
				last_patches=$(jq -e -r --arg t "$best_tag" '.[] | select(.tag_name == $t)' <<<"$dev_resp") || continue
			elif [ "$PATCHES_VER" = "latest" ]; then
				last_patches=$(gh_req "$rv_rel/latest" -) || continue
			else
				last_patches=$(gh_req "$rv_rel/tags/${PATCHES_VER}" -) || continue
			fi
			if ! last_patches=$(jq -e -r '.assets[] | select(.name | (endswith("asc") or endswith("json")) | not) | .name' <<<"$last_patches"); then
				abort "config_update error: '$last_patches'"
			fi
			resolved["$cache_key"]=$last_patches
		fi
		if [ -z "$last_patches" ]; then continue; fi

		# A table's state.md line only gets (re)written by build_rv() on a
		# fully successful build, and it embeds the exact patches file that
		# build used - so this directly answers "did THIS table last succeed
		# with the current patches?", not "did the download succeed" (that's
		# all a shared per-source check can prove, and a table whose patch
		# step then failed would wrongly look up to date forever - the actual
		# bug this replaced). A never-built or previously-failed table simply
		# won't have a matching line and gets queued for a (re)build.
		local up_to_date=true arch
		arch=$(toml_get "$t" arch) || arch="all"
		if [ "$arch" = both ]; then
			for a in arm64-v8a arm-v7a; do
				grep "^${table_name} (${a}): " state.md | grep -qF "$last_patches" || up_to_date=false
			done
		else
			grep "^${table_name}: " state.md | grep -qF "$last_patches" || up_to_date=false
		fi
		if [ "$up_to_date" = false ]; then
			prcfg=true
			upped+=("$table_name")
		else
			echo "$table_name: already up to date" >>"$TEMP_DIR"/skipped
		fi
	done
	if [ "$prcfg" = true ]; then
		local query=""
		for table in "${upped[@]}"; do
			if [ -n "$query" ]; then query+=" or "; fi
			query+=".key == \"$table\""
		done
		jq "to_entries | map(select(${query} or (.value | type != \"object\"))) | from_entries" <<<"$__TOML__"
	fi
}

_req() {
	local ip="$1" op="$2"
	shift 2
	local dlp="$op"
	if [ "$op" != - ]; then
		if [ -f "$op" ]; then return; fi
		dlp="$(dirname "$op")/tmp.$(basename "$op")"
		if [ -f "$dlp" ]; then
			while [ -f "$dlp" ]; do sleep 1; done
			return
		fi
	fi
	if ! curl -L -c "$TEMP_DIR/cookie.txt" -b "$TEMP_DIR/cookie.txt" --connect-timeout 15 --retry 4 --retry-delay 5 --retry-connrefused --fail -s -S "$@" "$ip" -o "$dlp"; then
		epr "Request failed: $ip"
		if [ "$dlp" != - ]; then rm -f "$dlp"; fi
		return 1
	fi
	if [ "$dlp" != - ]; then
		mv -f "$dlp" "$op"
	fi
}
req() { _req "$1" "$2" -H "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:108.0) Gecko/20100101 Firefox/108.0"; }
gh_req() { _req "$1" "$2" -H "$GH_HEADER"; }
gh_dl() {
	if [ ! -f "$1" ]; then
		pr "Getting '$1' from '$2'"
		_req "$2" "$1" -H "$GH_HEADER" -H "Accept: application/octet-stream"
	fi
}

log() { echo -e "$1  " >>"build.md"; }
mark_failed() { echo "$1" >>"${TEMP_DIR}/failed"; }
# Unlike build.md (truncated and rebuilt fresh every run, so it only ever
# reflects *this* run's output), state.md is never truncated: it's the
# persistent record config_update() reads to know what a table last built
# with, even across runs where that table wasn't touched at all. Replaces
# any existing line with the same prefix, then appends the new one.
state_upsert() {
	local prefix=$1 line=$2
	touch state.md
	awk -v p="$prefix" 'index($0, p) != 1' state.md >"state.md.tmp"
	mv -f "state.md.tmp" state.md
	echo -e "$line  " >>state.md
}
get_highest_ver() {
	local vers m
	vers=$(tee)
	m=$(head -1 <<<"$vers")
	if ! semver_validate "$m"; then
		echo "$m"
		return
	fi
	# Find the highest base version (the part before any "-prerelease"
	# suffix) with a numeric sort on that alone, since GNU `sort -V` ranks a
	# tag *with* a suffix as greater than the same base *without* one (e.g.
	# "v1.40.0-dev.23" > "v1.40.0") - the opposite of semver's own rule that
	# a pre-release has lower precedence than its associated normal release.
	# Left unfixed, this made a "dev" patches-version channel latch onto the
	# last prerelease tag forever and never notice the stable release that
	# actually superseded it.
	local top_base v base stable=""
	top_base=$(while IFS= read -r v; do echo "${v#v}"; done <<<"$vers" | cut -d- -f1 | sort -Vr | head -1)
	while IFS= read -r v; do
		base=${v#v} base=${base%%-*}
		if [ "$base" = "$top_base" ] && [[ $v != *-* ]]; then
			stable=$v
			break
		fi
	done <<<"$vers"
	if [ -n "$stable" ]; then
		echo "$stable"
		return
	fi
	# No stable release at the top base yet - compare its prereleases
	# directly. Sorting the whole string (not just the part before the first
	# "-") here still matters: it's what correctly ranks e.g.
	# "v4.3.0-dev.9"/"-dev.10"/"-dev.11" against each other, since comparing
	# only the base would tie them all at "4.3.0".
	#
	# The filter loop below must use "if ...; then echo; fi", not
	# "[ ... ] && echo": a bare "&&" makes the whole loop body's (and thus the
	# while loop's, and thus this pipeline's) exit status the test's own
	# non-zero result whenever the *last* input line doesn't match top_base -
	# which is the common case. Under pipefail that failure outranks sort/head
	# both succeeding right after it, so the function returned non-zero here
	# despite already having echoed the correct answer, making every "|| return
	# 1"/"|| continue" caller treat a fully successful resolution as a hard
	# failure whenever the winning version had no stable release yet (e.g. a
	# CLI/patches source still on "vX.Y.Z-dev.N"). An "if" with no "else"
	# always returns 0 when its condition is false, sidestepping that.
	{
		while IFS= read -r v; do
			base=${v#v} base=${base%%-*}
			if [ "$base" = "$top_base" ]; then echo "$v"; fi
		done <<<"$vers"
	} | sort -Vr | head -1
}
semver_validate() {
	local a="${1%-*}"
	local a="${a#v}"
	local ac="${a//[.0-9]/}"
	[ ${#ac} = 0 ]
}
get_patch_last_supported_ver() {
	local list_patches=$1 pkg_name=$2 inc_sel=$3 is_experimental=$4
	# _exc_sel=$4 _exclusive=$5
	local op
	if [ "$inc_sel" ]; then
		if ! op=$(awk '{$1=$1}1' <<<"$list_patches"); then
			epr "list-patches: '$op'"
			return 1
		fi
		local ver vers="" NL=$'\n'
		while IFS= read -r line; do
			line="${line:1:${#line}-2}"
			ver=$(sed -n "/^Name: $line\$/,/^\$/p" <<<"$op" | sed -n "/^Compatible versions:\$/,/^\$/p" | tail -n +2)
			vers=${ver}${NL}
		done <<<"$(list_args "$inc_sel")"
		vers=$(awk '{$1=$1}1' <<<"$vers")
		if [ "$vers" ]; then
			get_highest_ver <<<"$vers"
			return
		fi
	fi
	op=$(patches_list_versions "$cli_jar" "$patches_jar" "$pkg_name" "$is_experimental") || return 1
	op=$(sed -n '/Most common compatible versions:/,$p' <<<"$op" | sed '1d' | awk '{$1=$1}1')
	if [ "$op" = "Any" ]; then return; fi
	pcount=$(head -1 <<<"$op") pcount=${pcount#*(} pcount=${pcount% *}
	if [ -z "$pcount" ]; then
		if grep -Fq "$pkg_name" <<<"$list_patches"; then
			return
		else
			abort "No patches found for '$pkg_name' in patches '$patches_jar'"
		fi
	fi
	grep -F "($pcount patch" <<<"$op" | sed 's/ (.* patch.*//' | get_highest_ver || return 1
}

patches_list_versions() {
	local cli_jar=$1 patches_jar=$2 pkg_name=$3 is_experimental=$4
	local cmd_base="java -jar '$cli_jar' list-versions"

	# TODO: remove this later
	local cli_name
	cli_name=$(basename "$cli_jar")
	if [ "${cli_name::8}" = "revanced" ]; then
		cmd_base+=" -b"
	elif [ "$is_experimental" = "true" ]; then
		cmd_base+=" -x"
	fi

	local cmd="${cmd_base} --patches='$patches_jar' -f '$pkg_name'"
	if op=$(eval "$cmd" 2>&1); then
		echo "$op"
		return
	fi

	cmd="${cmd_base} '$patches_jar' -f '$pkg_name'"
	if op=$(eval "$cmd" 2>&1); then
		echo "$op"
		return
	fi

	epr "Could not list versions ($pkg_name) $cli_jar: '$op'"
	return 1
}
patches_list() {
	local cli_jar=$1 patches_jar=$2 pkg_name=$3 is_experimental=$4
	local op
	if ! op=$(java -jar "$cli_jar" list-patches -p "$patches_jar" --filter-package-name "$pkg_name" --versions --packages -b 2>&1); then
		local cmd="java -jar '$cli_jar' list-patches --patches '$patches_jar' -f '$pkg_name' --with-versions --with-packages"
		if [ "$is_experimental" = "true" ]; then cmd+=" -x"; fi
		if ! op=$(eval "$cmd" 2>&1); then
			epr "Could not get patches list ($pkg_name) $cli_jar: '$op'"
			return 1
		fi

	fi
	echo "$op"
}

isoneof() {
	local i=$1 v
	shift
	for v; do [ "$v" = "$i" ] && return 0; done
	return 1
}

merge_splits() {
	local bundle=$1 output=$2
	pr "Merging splits"
	gh_dl "$TEMP_DIR/apkeditor.jar" "https://github.com/REAndroid/APKEditor/releases/download/V1.4.7/APKEditor-1.4.7.jar" >/dev/null || return 1
	if ! OP=$(java -jar "$TEMP_DIR/apkeditor.jar" merge -i "$bundle" -o "${output}-unsigned" -clean-meta -f 2>&1); then
		epr "APKEditor error: $OP"
		return 1
	fi
	# sign the merged stock apk
	if ! OP=$(java -jar "$APKSIGNER" sign --ks ks-p12.keystore --ks-pass pass:123456789 --key-pass pass:123456789 --ks-key-alias jhc \
		--out "${output}" "${output}-unsigned"); then
		epr "apksigner error: $OP"
		return 1
	fi
	rm "${output}.idsig" "${output}-unsigned" 2>/dev/null || :
	return 0
}

# -------------------- apkmirror --------------------
apkmirror_search() {
	local resp="$1" dpi="$2" arch="$3" apk_bundle="$4"
	local dlurl="" node app_table emptyCheck

	local apparch=('universal' 'noarch' 'arm64-v8a + armeabi-v7a')
	if [ "$arch" != "all" ]; then
		apparch+=("$arch")
	fi

	local appdpi=("nodpi" "anydpi")
	if [ "$dpi" ]; then
		appdpi+=($dpi)
	fi

	for ((n = 1; n < 40; n++)); do
		node=$($HTMLQ "div.table-row.headerFont:nth-last-child($n)" -r "span:nth-child(n+3)" <<<"$resp")
		if [ -z "$node" ]; then break; fi
		emptyCheck=$($HTMLQ -t -w "div.table-cell:nth-child(1) > a:nth-child(1)" <<<"$node" | xargs)
		if [ -z "$emptyCheck" ]; then break; fi
		app_table=$($HTMLQ --text --ignore-whitespace <<<"$node")
		if [ "$(sed -n 3p <<<"$app_table")" != "$apk_bundle" ]; then continue; fi
		dlurl=$($HTMLQ --base https://www.apkmirror.com --attribute href "div:nth-child(1) > a:nth-child(1)" <<<"$node")
		if isoneof "$(sed -n 6p <<<"$app_table")" "${appdpi[@]}" &&
			isoneof "$(sed -n 4p <<<"$app_table")" "${apparch[@]}"; then
			echo "$dlurl"
			return 0
		fi
	done
	if [ "$n" -eq 2 ] && [ "$dlurl" ]; then
		# only one apk exists, return it
		echo "$dlurl"
		return 0
	fi
	return 1
}
dl_apkmirror() {
	local url=$1 version=${2// /-} output=$3 arch=$4 dpi=$5 is_bundle=false

	if [ -f "${output}.apkm" ]; then
		merge_splits "${output}.apkm" "${output}"
		return 0
	fi

	if [ "$arch" = "arm-v7a" ]; then arch="armeabi-v7a"; fi
	local resp node app_table apkmname dlurl=""
	apkmname=$($HTMLQ "h1.marginZero" --text <<<"$__APKMIRROR_RESP__")
	apkmname="${apkmname,,}" apkmname="${apkmname// /-}" apkmname="${apkmname//[^a-z0-9-]/}"
	url="${url}/${apkmname}-${version//./-}-release/"
	resp=$(req "$url" -) || return 1
	node=$($HTMLQ "div.table-row.headerFont:nth-last-child(1)" -r "span:nth-child(n+3)" <<<"$resp")
	if [ "$node" ]; then
		for type in APK BUNDLE; do
			if dlurl=$(apkmirror_search "$resp" "$dpi" "$arch" "$type"); then
				if [ "$type" = "BUNDLE" ]; then
					is_bundle=true
				else is_bundle=false; fi
				break 2
			fi
		done
		if [ -z "$dlurl" ]; then return 1; fi
		resp=$(req "$dlurl" -)
	fi
	url=$(echo "$resp" | $HTMLQ --base https://www.apkmirror.com --attribute href "a.btn") || return 1
	url=$(req "$url" - | $HTMLQ --base https://www.apkmirror.com --attribute href "span > a[rel = nofollow]") || return 1

	if [ "$is_bundle" = true ]; then
		req "$url" "${output}.apkm" || return 1
		merge_splits "${output}.apkm" "${output}"
	else
		req "$url" "${output}" || return 1
	fi
}
get_apkmirror_vers() {
	local vers apkm_resp
	apkm_resp=$(req "https://www.apkmirror.com/uploads/?appcategory=${__APKMIRROR_CAT__}" -)
	vers=$(sed -n 's;.*Version:</span><span class="infoSlide-value">\(.*\) </span>.*;\1;p' <<<"$apkm_resp" | awk '{$1=$1}1')

	vers=$(grep -iv "\(beta\|alpha\)" <<<"$vers")
	local v r_vers=()
	local IFS=$'\n'
	for v in $vers; do
		grep -iq "${v} \(beta\|alpha\)" <<<"$apkm_resp" || r_vers+=("$v")
	done
	echo "${r_vers[*]}"
}
get_apkmirror_pkg_name() { sed -n 's;.*id=\(.*\)" class="accent_color.*;\1;p' <<<"$__APKMIRROR_RESP__"; }
get_apkmirror_resp() {
	__APKMIRROR_RESP__=$(req "${1}" -) || return 1
	__APKMIRROR_CAT__="${1##*/}"
}

# -------------------- uptodown --------------------
get_uptodown_resp() {
	__UPTODOWN_RESP__=$(req "${1}/versions" -) || return 1
	__UPTODOWN_RESP_PKG__=$(req "${1}/download" -) || return 1
}
get_uptodown_vers() { $HTMLQ --text ".version" <<<"$__UPTODOWN_RESP__"; }
dl_uptodown() {
	local uptodown_dlurl=$1 version=$2 output=$3 arch=$4 _dpi=$5
	if [ "$arch" = "arm-v7a" ]; then arch="armeabi-v7a"; fi

	local apparch=('arm64-v8a, armeabi-v7a, x86_64' 'arm64-v8a, armeabi-v7a, x86, x86_64' 'arm64-v8a, armeabi-v7a')
	if [ "$arch" != "all" ]; then
		apparch+=("$arch")
	fi

	local op resp data_code
	data_code=$($HTMLQ "#detail-app-name" --attribute data-code <<<"$__UPTODOWN_RESP__")
	local versionURL=""
	local is_bundle=false
	for i in {1..20}; do
		resp=$(req "${uptodown_dlurl}/apps/${data_code}/versions/${i}" -)
		if ! op=$(jq -e -r ".data | map(select(.version == \"${version}\")) | .[0]" <<<"$resp"); then
			continue
		fi
		if [ "$(jq -e -r ".kindFile" <<<"$op")" = "xapk" ]; then is_bundle=true; fi
		if versionURL=$(jq -e -r '.versionURL' <<<"$op"); then break; else return 1; fi
	done
	if [ -z "$versionURL" ]; then return 1; fi
	versionURL=$(jq -e -r '.url + "/" + .extraURL + "/" + (.versionID | tostring)' <<<"$versionURL")
	resp=$(req "$versionURL" -) || return 1

	local data_version files node_arch="" data_file_id node_class
	data_version=$($HTMLQ '.button.variants' --attribute data-version <<<"$resp") || return 1
	if [ "$data_version" ]; then
		files=$(req "${uptodown_dlurl%/*}/app/${data_code}/version/${data_version}/files" - | jq -e -r .content) || return 1
		for ((n = 1; n < 12; n += 1)); do
			node_class=$($HTMLQ -w -t ".content > :nth-child($n)" --attribute class <<<"$files") || return 1
			if [ "$node_class" != "variant" ]; then
				node_arch=$($HTMLQ -w -t ".content > :nth-child($n)" <<<"$files" | xargs) || return 1
				continue
			fi
			if [ -z "$node_arch" ]; then return 1; fi
			if ! isoneof "$node_arch" "${apparch[@]}"; then continue; fi

			file_type=$($HTMLQ -w -t ".content > :nth-child($n) > .v-file > span" <<<"$files") || return 1
			if [ "$file_type" = "xapk" ]; then is_bundle=true; else is_bundle=false; fi
			data_file_id=$($HTMLQ ".content > :nth-child($n) > .v-report" --attribute data-file-id <<<"$files") || return 1
			resp=$(req "${uptodown_dlurl}/download/${data_file_id}-x" -)
			break
		done
		if [ $n -eq 12 ]; then return 1; fi
	fi
	local data_url
	data_url=$($HTMLQ "#detail-download-button" --attribute data-url <<<"$resp") || return 1
	if [ $is_bundle = true ]; then
		req "https://dw.uptodown.com/dwn/${data_url}" "$output.apkm" || return 1
		merge_splits "${output}.apkm" "${output}"
	else
		req "https://dw.uptodown.com/dwn/${data_url}" "$output"
	fi
}
get_uptodown_pkg_name() { $HTMLQ --text "tr.full:nth-child(1) > td:nth-child(3)" <<<"$__UPTODOWN_RESP_PKG__"; }

# -------------------- archive --------------------
dl_archive() {
	local url=$1 version=$2 output=$3 arch=$4
	local path version=${version// /}

	if [ -f "${output}.apkm" ]; then
		merge_splits "${output}.apkm" "$output"
		return 0
	fi

	# Fall back to a universal "-all" build if the archive dump has no
	# arch-specific entry for this version (e.g. it only ever got a merged
	# upload) - matches upstream j-hc/revanced-magisk-module@65f40c9.
	if ! path=$(grep -m1 "${version_f#v}-${arch// /}" <<<"$__ARCHIVE_RESP__"); then
		path=$(grep -m1 "${version_f#v}-all" <<<"$__ARCHIVE_RESP__") || return 1
	fi
	if [ "${path##*.}" = "apkm" ]; then
		req "${url}/${path}" "${output}.apkm" || return 1
		merge_splits "${output}.apkm" "$output"
	else
		req "${url}/${path}" "${output}" || return 1
	fi
}
get_archive_resp() {
	local r
	r=$(req "$1" -)
	if [ -z "$r" ]; then return 1; else __ARCHIVE_RESP__=$(sed -n 's;^<a href="\(.*\)"[^"]*;\1;p' <<<"$r"); fi
	__ARCHIVE_PKG_NAME__=$(awk -F/ '{print $NF}' <<<"$1")
}
get_archive_vers() { sed 's/^[^-]*-//;s/-\(all\|arm64-v8a\|arm-v7a\)\.apk//g' <<<"$__ARCHIVE_RESP__"; }
get_archive_pkg_name() { echo "$__ARCHIVE_PKG_NAME__"; }

# -------------------- apkpure (via apkeep) --------------------
get_apkeep() {
	if command -v apkeep >/dev/null 2>&1; then
		APKEEP="apkeep"
		return 0
	fi
	local bin_dir="${TEMP_DIR}/bin"
	mkdir -p "$bin_dir"
	APKEEP="${bin_dir}/apkeep"
	if [ ! -f "$APKEEP" ]; then
		pr "Getting 'apkeep'" >&2
		local arch os uname_s uname_m apkeep_url
		uname_s=$(uname -s 2>/dev/null | tr '[:upper:]' '[:lower:]' || echo linux)
		uname_m=$(uname -m 2>/dev/null || echo x86_64)
		apkeep_url="https://github.com/EFForg/apkeep/releases/download/1.0.0/apkeep-x86_64-unknown-linux-gnu"
		if [[ "$uname_s" =~ mingw|msys|cygwin ]]; then
			apkeep_url="https://github.com/EFForg/apkeep/releases/download/1.0.0/apkeep-x86_64-pc-windows-msvc.exe"
			APKEEP="${bin_dir}/apkeep.exe"
		elif [ "$uname_m" = "aarch64" ] || [ "$uname_m" = "arm64" ]; then
			apkeep_url="https://github.com/EFForg/apkeep/releases/download/1.0.0/apkeep-aarch64-unknown-linux-gnu"
		fi
		_req "$apkeep_url" "$APKEEP" || return 1
		chmod +x "$APKEEP" 2>/dev/null || :
	fi
}
get_apkpure_resp() {
	get_apkeep || return 1
	local pkg=$1
	pkg="${pkg%/}"
	pkg="${pkg##*/}"
	__APKPURE_PKG__="$pkg"
	__APKPURE_RESP__=$("$APKEEP" -a "$__APKPURE_PKG__" -l -d apk-pure .) || return 1
}
get_apkpure_vers() {
	sed -n 's/^[| ]*//; s/, /\n/g; p' <<<"$__APKPURE_RESP__" | grep -v "Versions available" | grep -iv "\(beta\|alpha\)"
}
get_apkpure_pkg_name() { echo "$__APKPURE_PKG__"; }
dl_apkpure() {
	get_apkeep || return 1
	local url=$1 version=$2 output=$3 arch=$4 _dpi=$5
	local pkg="$__APKPURE_PKG__"
	[ -z "$pkg" ] && pkg="${url%/}" && pkg="${pkg##*/}"
	local dl_dir="${TEMP_DIR}/apkeep_${pkg}_${version// /}"
	mkdir -p "$dl_dir"
	"$APKEEP" -a "${pkg}@${version}" -d apk-pure "$dl_dir" || { rm -rf "$dl_dir"; return 1; }
	local downloaded
	downloaded=$(find "$dl_dir" -maxdepth 1 -name "${pkg}@${version}*" -type f 2>/dev/null | head -1)
	if [ -z "$downloaded" ]; then
		downloaded=$(find "$dl_dir" -maxdepth 1 -name "${pkg}*" -type f 2>/dev/null | head -1)
	fi
	if [ -z "$downloaded" ]; then
		rm -rf "$dl_dir"
		return 1
	fi

	if [[ "$downloaded" =~ \.xapk$ ]] || [[ "$downloaded" =~ \.apkm$ ]]; then
		merge_splits "$downloaded" "$output"
	else
		mv -f "$downloaded" "$output"
	fi
	rm -rf "$dl_dir"
}

# -------------------- direct --------------------
dl_direct() {
	local url=$1 version=${2// /-} output=$3 arch=$4 _dpi=$5
	if ! grep -q "${version_f#v}-${arch// /}" <<<"$url"; then
		epr "Given direct-dlurl for $output is not compatible. Set proper 'arch' and 'version' options."
		return 1
	fi
	if [ "${url##*.}" = "apkm" ]; then
		req "$url" "${output}.apkm" || return 1
		merge_splits "${output}.apkm" "$output"
	else
		req "$url" "${output}" || return 1
	fi
}
get_direct_vers() { cut -d- -f2 <<<"$__DIRECT_APKNAME__"; }
get_direct_pkg_name() { cut -d- -f1 <<<"$__DIRECT_APKNAME__"; }
get_direct_resp() { __DIRECT_APKNAME__=$(awk -F/ '{print $NF}' <<<"$1"); }
# --------------------------------------------------

patch_apk() {
	local stock_input=$1 patched_apk=$2 patcher_args=$3 cli_jar=$4 patches_jar=$5 addon_patches=${6-}
	local tmp_files
	tmp_files="$(pwd)/$(mktemp -d -p "$TEMP_DIR")"

	# -e/-d selectors apply to whichever "-p <bundle>" they immediately follow
	# on the command line (per-bundle patch selection, not global), so
	# $patcher_args (which may -d the GmsCore/microg patch for module builds)
	# MUST sit directly after the main "-p '$patches_jar'" and before any
	# addon bundle - otherwise it silently binds to the wrong bundle (or none)
	# and e.g. the microg patch never actually gets excluded from root builds.
	local addon_args="" addon
	for addon in $addon_patches; do
		if [ ! -f "$addon" ]; then
			epr "Addon patches bundle not found, skipping: $addon"
			continue
		fi
		addon_args+=" -p '$addon'"
	done

	local cmd="java -jar '$cli_jar' patch '$stock_input' -o '$patched_apk' -p '$patches_jar' $patcher_args${addon_args} --keystore=ks.keystore \
--keystore-entry-password=123456789 --keystore-password=123456789 --signer=jhc --keystore-entry-alias=jhc -t '$tmp_files'"

	# TODO: remove this later
	local cli_name
	cli_name=$(basename "$cli_jar")
	if [ "${cli_name::8}" = revanced ]; then cmd+=" -b"; fi

	if [ "$OS" = Android ]; then cmd+=" --custom-aapt2-binary='${AAPT2}'"; fi
	pr "$cmd"
	if eval "$cmd"; then [ -f "$patched_apk" ]; else
		rm "$patched_apk" 2>/dev/null || :
		return 1
	fi
}

check_sig() {
	local file=$1 pkg_name=$2
	local sig
	if grep -q "$pkg_name" sig.txt; then
		sig=$(java -jar "$APKSIGNER" verify --print-certs "$file" | grep ^Signer | grep SHA-256 | tail -1 | awk '{print $NF}')
		echo "$pkg_name signature: ${sig}"
		grep -qFx "$sig $pkg_name" sig.txt
	fi
}

build_rv() {
	eval "declare -A args=${1#*=}"
	local version="" pkg_name=""
	local mode_arg=${args[build_mode]} version_mode=${args[version]}
	local app_name=${args[app_name]}
	local app_name_l=${app_name,,}
	app_name_l=${app_name_l// /-}
	local table=${args[table]}
	local dl_from=${args[dl_from]}
	local arch=${args[arch]}
	local arch_f="${arch// /}"

	local p_patcher_args=()
	if [ "${args[excluded_patches]}" ]; then p_patcher_args+=("$(join_args "${args[excluded_patches]}" -d)"); fi
	if [ "${args[included_patches]}" ]; then p_patcher_args+=("$(join_args "${args[included_patches]}" -e)"); fi
	[ "${args[exclusive_patches]}" = true ] && p_patcher_args+=("--exclusive")

	local tried_dl=()
	if [ "${args[pkg_name]}" ]; then
		pkg_name="${args[pkg_name]}"
	else
		for dl_p in "${DL_SRCS[@]}"; do
			if [ -z "${args[${dl_p}_dlurl]}" ]; then continue; fi
			if ! get_${dl_p}_resp "${args[${dl_p}_dlurl]}" || ! pkg_name=$(get_"${dl_p}"_pkg_name); then
				args[${dl_p}_dlurl]=""
				epr "ERROR: Could not find ${table} in ${dl_p}"
				continue
			fi
			tried_dl+=("$dl_p")
			dl_from=$dl_p
			break
		done
	fi

	if [ -z "$pkg_name" ]; then
		epr "empty pkg name, not building ${table}."
		mark_failed "$table"
		return 0
	fi
	pr "Package name of '${table}' is '$pkg_name'"
	local list_patches

	local is_experimental="false"
	if [ "$version_mode" = "experimental" ]; then is_experimental="true"; fi
	list_patches=$(patches_list "$cli_jar" "$patches_jar" "$pkg_name" "$is_experimental") || { mark_failed "$table"; return 1; }
	local get_latest_ver=false
	if isoneof "$version_mode" "auto" "experimental"; then
		if ! version=$(get_patch_last_supported_ver "$list_patches" "$pkg_name" "${args[included_patches]}" "$is_experimental"); then
			epr "get_patch_last_supported_ver failed '$list_patches'"
			mark_failed "$table"
			return
		elif [ -z "$version" ]; then get_latest_ver="true"; fi
	elif [ "$version_mode" = "latest" ]; then
		get_latest_ver="true"
		p_patcher_args+=("-f")
	else
		version=$version_mode
		p_patcher_args+=("-f")
	fi
	if [ $get_latest_ver = "true" ]; then
		pkgvers=$(get_"${dl_from}"_vers)
		version=$(get_highest_ver <<<"$pkgvers") || version=$(head -1 <<<"$pkgvers")
	fi
	if [ -z "$version" ]; then
		epr "empty version, not building ${table}."
		mark_failed "$table"
		return 0
	fi

	if [ "$mode_arg" = module ]; then
		build_mode_arr=(module)
	elif [ "$mode_arg" = apk ]; then
		build_mode_arr=(apk)
	elif [ "$mode_arg" = both ]; then
		build_mode_arr=(apk module)
	fi

	pr "Choosing version '${version}' for ${table}"
	local version_f=${version// /}
	version_f=${version_f#v}
	local stock_apk="${STOCK_CACHE_DIR}/${pkg_name}-${version_f}-${arch_f}.apk"
	if [ ! -f "$stock_apk" ]; then
		for dl_p in "${DL_SRCS[@]}"; do
			if [ -z "${args[${dl_p}_dlurl]}" ]; then continue; fi
			pr "Downloading '${table}' from '${dl_p}'"
			if ! isoneof $dl_p "${tried_dl[@]}"; then
				if ! get_${dl_p}_resp "${args[${dl_p}_dlurl]}"; then
					epr "ERROR: Could not get '${table}' from '${dl_p}'"
					continue
				fi
			fi
			if ! dl_${dl_p} "${args[${dl_p}_dlurl]}" "$version" "$stock_apk" "$arch" "${args[dpi]}" "$get_latest_ver"; then
				epr "ERROR: Could not download '${table}' from '${dl_p}' with version '${version}', arch '${arch}', dpi '${args[dpi]}'"
				continue
			fi
			break
		done
		if [ ! -f "$stock_apk" ]; then
			epr "Stock apk not found ($stock_apk)"
			mark_failed "$table"
			return 0
		fi
	else
		pr "Using cached stock apk for '${table}': '${stock_apk}'"
	fi
	# Upsert (by table), not append: the manifest lives inside STOCK_CACHE_DIR
	# itself and round-trips through the same CI cache as the apks, so it's
	# never reset between runs (see build.sh) - only *this* table's own line
	# gets replaced. A table config_update() decided didn't need rebuilding
	# is never touched here, so its previous entry (still the correct file)
	# survives the prune step below; only a table's own superseded old
	# filename gets dropped, once that table is actually rebuilt with a new
	# version. Blindly appending on every run, or truncating the manifest at
	# the start of every run, both get this wrong: appending never forgets a
	# superseded version, and truncating makes any table config_update()
	# skipped that run look "unwanted" and prunes its still-current apk.
	awk -v p="${table}: " 'index($0, p) != 1' "${STOCK_CACHE_DIR}/.manifest" 2>/dev/null >"${STOCK_CACHE_DIR}/.manifest.tmp" || :
	mv -f "${STOCK_CACHE_DIR}/.manifest.tmp" "${STOCK_CACHE_DIR}/.manifest"
	echo "${table}: ${stock_apk}" >>"${STOCK_CACHE_DIR}/.manifest"

	local sig_op
	if [ -f "${stock_apk}.apkm" ]; then
		rm -rf "${stock_apk}-zip" || :
		unzip -j "${stock_apk}.apkm" -d "${stock_apk}-zip" >/dev/null
		for a in "${stock_apk}"-zip/*.apk; do
			if ! sig_op=$(check_sig "$a" "$pkg_name" 2>&1); then
				epr "Not building $table, apk signature mismatch '$a': $sig_op"
				mark_failed "$table"
				return 0
			fi
		done
		rm -rf "${stock_apk}-zip" || :
	else
		if ! sig_op=$(check_sig "$stock_apk" "$pkg_name" 2>&1); then
			epr "Not building $table, apk signature mismatch '$stock_apk': $sig_op"
			mark_failed "$table"
			return 0
		fi
	fi

	local microg_patch
	microg_patch=$(grep "^Name: " <<<"$list_patches" | grep -i "gmscore\|microg" || :) microg_patch=${microg_patch#*: }
	if [ -n "$microg_patch" ] && [[ ${p_patcher_args[*]} =~ $microg_patch ]]; then
		wpr "You cant include/exclude microg patch as that's done by rvmm builder automatically."
		p_patcher_args=("${p_patcher_args[@]//-[ed] ${microg_patch}/}")
	fi

	local patcher_args patched_apk build_mode
	local rv_brand_f=${args[rv_brand],,}
	rv_brand_f=${rv_brand_f// /-}
	if [ "${args[patcher_args]}" ]; then p_patcher_args+=("${args[patcher_args]}"); fi
	for build_mode in "${build_mode_arr[@]}"; do
		patcher_args=("${p_patcher_args[@]}")
		pr "Building '${table}' in '$build_mode' mode"
		if [ -n "$microg_patch" ]; then
			patched_apk="${TEMP_DIR}/${app_name_l}-${rv_brand_f}-${version_f}-${arch_f}-${build_mode}.apk"
		else
			patched_apk="${TEMP_DIR}/${app_name_l}-${rv_brand_f}-${version_f}-${arch_f}.apk"
		fi
		if [ -n "$microg_patch" ]; then
			if [ "$build_mode" = apk ]; then
				patcher_args+=("-e \"${microg_patch}\"")
			elif [ "$build_mode" = module ]; then
				patcher_args+=("-d \"${microg_patch}\"")
			fi
		fi

		local stock_apk_to_patch="${stock_apk}.stripped.apk"
		cp -f "$stock_apk" "$stock_apk_to_patch"
		if [ "$build_mode" = module ]; then
			zip -d "$stock_apk_to_patch" "lib/*" >/dev/null 2>&1 || :
		else
			if [ "$arch" = "arm64-v8a" ]; then
				zip -d "$stock_apk_to_patch" "lib/armeabi-v7a/*" "lib/x86_64/*" "lib/x86/*" >/dev/null 2>&1 || :
			elif [ "$arch" = "arm-v7a" ]; then
				zip -d "$stock_apk_to_patch" "lib/arm64-v8a/*" "lib/x86_64/*" "lib/x86/*" >/dev/null 2>&1 || :
			elif [ "$arch" = "x86" ]; then
				zip -d "$stock_apk_to_patch" "lib/arm64-v8a/*" "lib/x86_64/*" "lib/armeabi-v7a/*" >/dev/null 2>&1 || :
			elif [ "$arch" = "x86_64" ]; then
				zip -d "$stock_apk_to_patch" "lib/arm64-v8a/*" "lib/armeabi-v7a/*" "lib/x86/*" >/dev/null 2>&1 || :
			else
				zip -d "$stock_apk_to_patch" "lib/x86_64/*" "lib/x86/*" >/dev/null 2>&1 || :
			fi
		fi

		local apk_output="${BUILD_DIR}/${app_name_l}-${rv_brand_f}-v${version_f}-${arch_f}.apk"
		if [ "${NORB:-}" != true ] || { [ ! -f "$patched_apk" ] && [ ! -f "$apk_output" ]; }; then
			if ! patch_apk "$stock_apk_to_patch" "$patched_apk" "${patcher_args[*]}" "${args[cli]}" "${args[ptjar]}" "${args[addon_patches]}"; then
				epr "Building '${table}' failed!"
				mark_failed "$table"
				return 0
			fi
		fi
		rm "$stock_apk_to_patch"
		if [ "$build_mode" = apk ]; then
			if [ "${NORB:-}" != true ] || { [ ! -f "$patched_apk" ] && [ ! -f "$apk_output" ]; }; then
				mv -f "$patched_apk" "$apk_output"
			else
				cp -f "$patched_apk" "$apk_output"
			fi
			pr "Built ${table} (non-root): '${apk_output}'"
			continue
		fi
		local base_template
		base_template=$(mktemp -d -p "$TEMP_DIR")
		cp -a $MODULE_TEMPLATE_DIR/. "$base_template"
		local upj="${table,,}-update.json"

		module_config "$base_template" "$pkg_name" "$version" "$arch"

		local patches_ver="${patches_jar##*-}"
		module_prop \
			"${args[module_prop_name]}" \
			"${app_name} ${args[rv_brand]}" \
			"${version} (patches ${patches_ver})" \
			"${app_name} ${args[rv_brand]} module" \
			"https://raw.githubusercontent.com/${GITHUB_REPOSITORY-}/update/${upj}" \
			"$base_template"

		local module_output="${app_name_l}-${rv_brand_f}-module-v${version_f}-${arch_f}.zip"
		pr "Packing module ${table}"
		cp -f "$patched_apk" "${base_template}/base.apk"

		if [ "${args[include_stock]}" != "disable" ]; then
			mkdir -p "${base_template}/stock/"
			if [ "${args[include_stock]}" = "merged" ]; then
				cp -f "$stock_apk" "${base_template}/stock/base.apk"
			elif [ "${args[include_stock]}" = "split" ]; then
				if [ ! -f "${stock_apk}.apkm" ]; then
					epr "Cannot include as 'split' because stock apk of $table_name is not a bundle"
					mark_failed "$table"
					return 0
				fi
				if [ "$arch" = "arm64-v8a" ]; then
					unzip -j "${stock_apk}.apkm" '*.apk' -x '*x86_64.apk' -x '*x86.apk' -x '*armeabi_v7a.apk' -d "${base_template}/stock/" >/dev/null 2>&1
				elif [ "$arch" = "arm-v7a" ]; then
					unzip -j "${stock_apk}.apkm" '*.apk' -x '*x86_64.apk' -x '*x86.apk' -x '*arm64_v8a.apk' -d "${base_template}/stock/" >/dev/null 2>&1
				elif [ "$arch" = "x86" ]; then
					unzip -j "${stock_apk}.apkm" '*.apk' -x '*x86_64.apk' -x '*arm64_v8a.apk' -x '*armeabi_v7a.apk' -d "${base_template}/stock/" >/dev/null 2>&1
				elif [ "$arch" = "x86_64" ]; then
					unzip -j "${stock_apk}.apkm" '*.apk' -x '*x86.apk' -x '*arm64_v8a.apk' -x '*armeabi_v7a.apk' -d "${base_template}/stock/" >/dev/null 2>&1
				else
					unzip -j "${stock_apk}.apkm" '*.apk' -x '*x86_64.apk' -x '*x86.apk' -d "${base_template}/stock/" >/dev/null 2>&1
				fi
			fi
		fi

		pushd >/dev/null "$base_template" || abort "Module template dir not found"
		zip -"$COMPRESSION_LEVEL" -FSqr "${CWD}/${BUILD_DIR}/${module_output}" .
		popd >/dev/null || :
		pr "Built ${table} (root): '${BUILD_DIR}/${module_output}'"
	done
	log "${table}: ${version}"
	# Embeds the exact patches file this table just succeeded with (not just
	# the app version), so config_update() can tell "this table already
	# built with the current patches" apart from "some other table sharing
	# this source already downloaded the current patches" - see config_update().
	state_upsert "${table}: " "${table}: ${version} [$(basename "${args[ptjar]}")]"
}

list_args() { tr -d '\t\r' <<<"$1" | tr -s ' ' | sed 's/" "/"\n"/g' | sed 's/\([^"]\)"\([^"]\)/\1'\''\2/g' | grep -v '^$' || :; }
join_args() { list_args "$1" | sed "s/^/${2} /" | paste -sd " " - || :; }

module_config() {
	local ma=""
	if [ "$4" = "arm64-v8a" ]; then
		ma="arm64"
	elif [ "$4" = "arm-v7a" ]; then
		ma="arm"
	fi
	echo "PKG_NAME=$2
PKG_VER=$3
MODULE_ARCH=$ma" >"$1/config"
}
module_prop() {
	echo "id=${1}
name=${2}
version=v${3}
versionCode=${NEXT_VER_CODE}
author=j-hc
description=${4}" >"${6}/module.prop"

	if [ "$ENABLE_MODULE_UPDATE" = true ]; then echo "updateJson=${5}" >>"${6}/module.prop"; fi
}
