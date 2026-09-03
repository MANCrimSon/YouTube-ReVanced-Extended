# YouTube & YT Music Mod Builder

Automated builds of modified **YouTube** and **YouTube Music** — on two patch sets:
[**anddea/revanced-patches**](https://github.com/anddea/revanced-patches) (ReVanced Extended) and
[**MorpheApp/morphe-patches**](https://github.com/MorpheApp/morphe-patches) (Morphe), with voice-over translation via
[**Yandex VoT**](https://github.com/sashade8-ship-it/morphe-patches-yavot).

Every build ships in two flavors — **NonRoot** (a plain APK) and **Root** (a Magisk/KernelSU module) — and gets
rebuilt automatically as soon as new patches are released.

<!--release--><a href="../../releases/latest"><img src="https://img.shields.io/badge/release-v401-blue" alt="Latest release"></a><!--/release--> <!--downloads--><a href="../../releases"><img src="https://img.shields.io/badge/downloads-456.2k-success" alt="Downloads"></a><!--/downloads-->

## Download

The links below always point straight at the file, kept up to date automatically after every build (see full history
in [**Releases**](../../releases)). YouTube Music ships two architectures — pick the one matching your device.

| Build | NonRoot (APK) | Root (module) |
|---|---|---|
| YouTube — ReVanced Extended (anddea) | <!--yt-rvx-apk-->[Download](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/400/youtube-revanced-extended-v20.51.39-all.apk)<!--/yt-rvx-apk--> | <!--yt-rvx-zip-->[Download](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/400/youtube-revanced-extended-module-v20.51.39-all.zip)<!--/yt-rvx-zip--> |
| YouTube Music — ReVanced Extended (anddea) | <!--ytm-rvx-apk-arm64-->[arm64-v8a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/400/youtube-music-revanced-extended-v9.15.51-arm64-v8a.apk)<!--/ytm-rvx-apk-arm64--><br><br><!--ytm-rvx-apk-armv7-->[arm-v7a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/400/youtube-music-revanced-extended-v9.15.51-arm-v7a.apk)<!--/ytm-rvx-apk-armv7--> | <!--ytm-rvx-zip-arm64-->[arm64-v8a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/400/youtube-music-revanced-extended-module-v9.15.51-arm64-v8a.zip)<!--/ytm-rvx-zip-arm64--><br><br><!--ytm-rvx-zip-armv7-->[arm-v7a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/400/youtube-music-revanced-extended-module-v9.15.51-arm-v7a.zip)<!--/ytm-rvx-zip-armv7--> |
| YouTube — Morphe (+ Yandex VoT) | <!--yt-morphe-apk-->[Download](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/400/youtube-morphe-v21.07.247-all.apk)<!--/yt-morphe-apk--> | <!--yt-morphe-zip-->[Download](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/400/youtube-morphe-module-v21.07.247-all.zip)<!--/yt-morphe-zip--> |
| YouTube Music — Morphe | <!--ytm-morphe-apk-arm64-->[arm64-v8a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/401/youtube-music-morphe-v9.15.51-arm64-v8a.apk)<!--/ytm-morphe-apk-arm64--><br><br><!--ytm-morphe-apk-armv7-->[arm-v7a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/401/youtube-music-morphe-v9.15.51-arm-v7a.apk)<!--/ytm-morphe-apk-armv7--> | <!--ytm-morphe-zip-arm64-->[arm64-v8a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/401/youtube-music-morphe-module-v9.15.51-arm64-v8a.zip)<!--/ytm-morphe-zip-arm64--><br><br><!--ytm-morphe-zip-armv7-->[arm-v7a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/401/youtube-music-morphe-module-v9.15.51-arm-v7a.zip)<!--/ytm-morphe-zip-armv7--> |

## NonRoot — installation and auto-updates via Obtainium

The APK installs like a regular app and needs no root, but YouTube/YouTube Music also need
[**MicroG RE**](https://github.com/MorpheApp/MicroG-RE/releases) — it spoofs the Google services signature; without it
the apps won't launch.

You don't have to check for updates by hand — [**Obtainium**](https://github.com/ImranR98/Obtainium) can watch
releases in this repository and prompt you when a new build is out. Install Obtainium, then tap each link below — it
opens directly in the app with the source and APK filter already configured, just hit "Add":

| What to import | Link |
|---|---|
| MicroG RE *(needed once, shared by every build)* | [Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.revanced.android.gms%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMorpheApp%2FMicroG-RE%22%2C%22author%22%3A%22MorpheApp%22%2C%22name%22%3A%22MicroG%20RE%22%2C%22additionalSettings%22%3A%22%7B%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Emicrog%28%3F%21.%2Anoicon%29%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%7D%22%7D) |
| YouTube — ReVanced Extended (anddea) | [Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22anddea.youtube%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YouTube%20RVX%20%28anddea%29%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-revanced-extended%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Afalse%2C%5C%22appName%5C%22%3A%5C%22YouTube%20RVX%20%28anddea%29%5C%22%7D%22%7D) |
| YouTube Music — ReVanced Extended (anddea) | [Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22anddea.youtube.music%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YT%20Music%20RVX%20%28anddea%29%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-music-revanced-extended%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%2C%5C%22appName%5C%22%3A%5C%22YT%20Music%20RVX%20%28anddea%29%5C%22%7D%22%7D) |
| YouTube — Morphe (+ Yandex VoT) | [Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.morphe.android.youtube%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YouTube%20Morphe%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-morphe%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Afalse%7D%22%7D) |
| YouTube Music — Morphe | [Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.morphe.android.apps.youtube.music%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YT%20Music%20Morphe%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-music-morphe%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%7D%22%7D) |

These links only work on a device with Obtainium installed (they're `obtainium://` deep links, they won't do
anything in a desktop browser).

<details>
<summary><b>Troubleshooting playback issues (infinite buffering)</b></summary>

<details>
<summary><b>YouTube & YouTube Music — ReVanced Extended (anddea)</b></summary>

If videos or music tracks stop playing after a few seconds or buffer infinitely:

1. **YouTube:** Go to `Settings` ➔ `RVX` ➔ `Miscellaneous` ➔ `Spoof video streams` ➔ toggle `Spoof video streams` (**On**) and set **Default client** to **`TV Simply`** (priority client with built-in PoToken generation). Alternatively, try switching to another client or toggling the feature **Off**.
2. **YouTube Music:** Go to `Settings` ➔ `RVX` ➔ `Miscellaneous` ➔ toggle `Spoof video streams` (**On**) and set **Default client** to **`TV Simply`** (priority client with built-in PoToken generation). Alternatively, try switching to another client or toggling the feature **Off**.
3. Restart the app.

> *Note: Due to regional restrictions or network DPI blocks, playback may still be unstable — check your network bypass tools if needed.*
</details>

<details>
<summary><b>YouTube & YouTube Music — Morphe</b></summary>

Morphe provides two methods to resolve playback issues:

#### 1. Primary solution — PoToken (Recommended)
1. Install [**PotHelper**](https://github.com/MorpheApp/PotHelper/releases/latest) ([Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.morphe.pot.helper%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMorpheApp%2FPotHelper%22%2C%22author%22%3A%22MorpheApp%22%2C%22name%22%3A%22PotHelper%22%2C%22additionalSettings%22%3A%22%7B%5C%22fallbackToOlderReleases%5C%22%3Atrue%7D%22%7D)).
2. Turn **OFF** stream spoofing: `Settings` ➔ `Morphe` ➔ `Miscellaneous` ➔ `Spoof video streams` ➔ toggle `Spoof video streams` (**Off**).
3. Turn **ON** PoToken: `Settings` ➔ `Morphe` ➔ `Miscellaneous` ➔ `PoToken provider` ➔ toggle `External PoToken provider` (**On**).
4. Restart the app.

#### 2. Fallback solution — Spoof video streams
1. Turn **OFF** PoToken: `Settings` ➔ `Morphe` ➔ `Miscellaneous` ➔ `PoToken provider` ➔ toggle `External PoToken provider` (**Off**).
2. Turn **ON** stream spoofing: `Settings` ➔ `Morphe` ➔ `Miscellaneous` ➔ `Spoof video streams` ➔ toggle `Spoof video streams` (**On**), then select a suitable **Default client** (or try toggling the feature **Off**).
3. Restart the app.

> *Note: Due to regional restrictions or network DPI blocks, playback may still be unstable — check your network bypass tools if needed.*
</details>

</details>

## Root — installation

<details>
<summary>Instructions</summary>

1. Install the module archive via Magisk/KernelSU ➔ reboot.
2. Detach the app from Google Play updates, otherwise Play Store will overwrite the patch:
   - Magisk: enable Zygisk.
   - KernelSU: install [**ZygiskNext**](https://github.com/Dr-TSNG/ZygiskNext/releases).
   - Install [**zygisk-detach**](https://github.com/j-hc/zygisk-detach/releases) +
     [**zygisk-detach-app**](https://github.com/j-hc/zygisk-detach-app/releases), grant root ➔ reboot.
   - In zygisk-detach-app, pick the app ➔ Detach.
3. KernelSU only: KSU App ➔ Superuser ➔ app ➔ Unmount modules: Off ➔ reboot the device.
4. To update: hit \"Update\" in Magisk/KernelSU, or just flash the new archive on top.

If you get a **\"Reflash needed\"** error after rebooting, or root detectors complain about a
**\"Suspicious mount detected\"** — try [**rvmm-zygisk-mount**](https://github.com/j-hc/rvmm-zygisk-mount).
</details>

## Building your own config

Want to build a different set of apps/patches? Use this repository as a base and edit
[`config.toml`](./config.toml) — the key format is documented in [`CONFIG.md`](./CONFIG.md). Manual builds run from
[Actions → Build Modules](../../actions/workflows/build.yml) (workflow_dispatch; pass `only_apps` to build a single
app instead of everything).

## Credits

This build system started as a fork of [**j-hc/revanced-magisk-module**](https://github.com/j-hc/revanced-magisk-module) —
huge thanks to [**j-hc**](https://github.com/j-hc) for the original builder, the module template, and the tooling
(`zygisk-detach`, `rvmm-zygisk-mount`) this repo still relies on.

Patches and translation add-on come from [**anddea**](https://github.com/anddea/revanced-patches),
[**MorpheApp**](https://github.com/MorpheApp/morphe-patches), and [**sashade8-ship-it**](https://github.com/sashade8-ship-it/morphe-patches-yavot).
