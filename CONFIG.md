# Configuration Guide

This document describes all available options in `config.toml` for configuring automated builds of modified YouTube, YouTube Music, or any other supported Android applications.

---

## Quick Start

Adding another app is as simple as adding a new table with its download source:

```toml
[Twitter]
uptodown-dlurl = "https://twitter.en.uptodown.com/android"
```

> [!WARNING]
> When a patch name contains a single quote, double it inside TOML strings (e.g. `'Hide ''Get Music Premium'''`).

---

## 1. Global Options (Top Level)

These options apply across all apps unless overridden inside a specific app section:

| Option | Type | Default | Description |
|---|---|---|---|
| `cli-source` | string | `"MorpheApp/morphe-desktop"` | GitHub repository (`owner/repo`) to fetch the CLI/Desktop patcher from. |
| `cli-version` | string | `"latest"` | CLI version to use (`"latest"`, `"dev"`, or a specific tag like `"v1.15.0"`). |
| `patches-source` | string | `"MorpheApp/morphe-patches"` | Default repository for patches bundles. |
| `patches-version` | string | `"latest"` | Default patches version (`"latest"`, `"dev"`, or a specific tag). |
| `rv-brand` | string | `"Morphe"` | Custom brand name for in-app settings menu and patcher prefix (e.g. `"Morphe"`, `"ReVanced Extended"`). |
| `compression-level` | integer | `9` | Zip compression level (0–9) for Magisk/KernelSU root modules. Level `9` produces the smallest size with zero installation delay. |
| `enable-module-update` | boolean | `true` | Enables in-app update checks for Magisk/KernelSU modules. |
| `parallel-jobs` | integer | `1` | Number of parallel patching jobs (kept at `1` for maximum CLI stability). |
| `remove-rv-integrations-checks` | boolean | `true` | Removes internal integration checks from the patches bundle. |

---

## 2. Per-App Options (`[App-Name]`)

Each section `[App-Name]` configures a specific application target:

### General & Targeting
* **`enabled`** *(boolean, default: `true`)* — Set to `false` to temporarily skip building this app.
* **`app-name`** *(string, default: table name)* — Name used in release files and changelog.
* **`build-mode`** *(string, default: `"apk"`)*:
  * `"apk"` — Builds NonRoot APK only.
  * `"module"` — Builds Root (Magisk/KernelSU) module only.
  * `"both"` — Builds both NonRoot APK and Root module.
* **`arch`** *(string, default: `"all"`)*:
  * `"all"` — Universal build containing all architectures.
  * `"arm64-v8a"` / `"arm-v7a"` — Specific target architecture.
  * `"both"` — Automatically builds **two separate versions** (`arm64-v8a` and `arm-v7a`) in parallel.

### Version Selection
* **`version`** *(string, default: `"auto"`)*:
  * `"auto"` — Automatically resolves the highest stock version officially supported by all selected patches.
  * `"latest"` — Picks the newest version found in the download source without patch compatibility checking.
  * `"20.51.39"` — Forces a specific version number.

### Patches Selection
* **`patches-source`** *(string)* — Overrides the global patches repository (e.g. `"anddea/revanced-patches"`).
* **`patches-version`** *(string)* — Overrides the patches version (`"dev"`, `"latest"`, etc.).
* **`included-patches`** *(string)* — Quoted list of optional/non-default patches to include:
  ```toml
  included-patches = "'Disable music video in album' 'Theme'"
  ```
* **`excluded-patches`** *(string)* — Quoted list of default patches to exclude:
  ```toml
  excluded-patches = "'Custom branding icon for YouTube' 'Custom header'"
  ```
* **`exclusive-patches`** *(boolean, default: `false`)* — When `true`, excludes all patches by default and enables *only* those in `included-patches`.
* **`patcher-args`** *(multiline string)* — Custom options passed directly to the patcher CLI:
  ```toml
  patcher-args = """\
    -OdarkThemeBackgroundColor=#FF0F0F0F \
    """
  ```
* **`addon-patches-source`** *(string)* — Extra `.mpp` patch bundles to apply on top (e.g. `"sashade8-ship-it/morphe-patches-yavot"` for Yandex translation).

---

## 3. Stock APK Download Sources

At least one download source is required per app.

### 🌟 Archive.org (`archive-dlurl`) — Recommended for CI
The most reliable and stable source for GitHub Actions CI builds (no Cloudflare IP blocks, fast downloads, and full control over stock APKs).

#### Setting up your own Archive.org storage:
1. Create a free account on [Archive.org](https://archive.org/) and create an Item/Collection (e.g. `my-apks`).
2. Inside that item, organize APK files named strictly in the following format:
   ```text
   <package_name>-<version>-<arch>.apk
   ```
   **Examples:**
   * `com.google.android.youtube-20.51.39-all.apk`
   * `com.google.android.apps.youtube.music-9.15.51-arm64-v8a.apk`
   * `com.google.android.apps.youtube.music-9.15.51-arm-v7a.apk`
3. Point `archive-dlurl` to the URL ending with the package name:
   ```toml
   archive-dlurl = "https://archive.org/download/my-apks/apks/com.google.android.youtube"
   ```

### Other Sources:
* **`uptodown-dlurl`** — Public Uptodown app page:
  ```toml
  uptodown-dlurl = "https://youtube.en.uptodown.com/android"
  ```
* **`direct-dlurl`** — Direct link to an APK file hosted on any web server:
  ```toml
  direct-dlurl = "https://my-server.com/com.google.android.youtube-20.51.39-all.apk"
  ```
* **`apkmirror-dlurl`** — APKMirror app URL.  
  > *Note: APKMirror is Cloudflare-protected and blocks GitHub Actions runner IPs (returns 403 Forbidden). Recommended for local builds only.*

---

## Full Example (`config.toml`)

```toml
enable-magisk-update = true

cli-source = "MorpheApp/morphe-desktop"
cli-version = "dev"

[YouTube-Extended]
enabled = true
app-name = "YouTube"
rv-brand = "ReVanced Extended"
build-mode = "both"
patches-source = "anddea/revanced-patches"
patches-version = "dev"
archive-dlurl = "https://archive.org/download/jhc-apks/apks/com.google.android.youtube"
uptodown-dlurl = "https://youtube.en.uptodown.com/android"

[YouTube-Music-Extended]
enabled = true
app-name = "YouTube Music"
rv-brand = "ReVanced Extended"
build-mode = "both"
arch = "both"
patches-source = "anddea/revanced-patches"
patches-version = "dev"
archive-dlurl = "https://archive.org/download/jhc-apks/apks/com.google.android.apps.youtube.music"
included-patches = "'Disable music video in album'"

[YouTube-Morphe]
enabled = true
app-name = "YouTube"
rv-brand = "Morphe"
build-mode = "both"
patches-source = "sashade8-ship-it/dual-vot-patches"
patches-version = "dev"
archive-dlurl = "https://archive.org/download/jhc-apks/apks/com.google.android.youtube"

[YouTube-Music-Morphe]
enabled = true
app-name = "YouTube Music"
rv-brand = "Morphe"
build-mode = "both"
arch = "both"
patches-source = "MorpheApp/morphe-patches"
patches-version = "dev"
archive-dlurl = "https://archive.org/download/jhc-apks/apks/com.google.android.apps.youtube.music"
```
