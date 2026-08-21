# YouTube ReVanced Extended

Automated builds of modified **YouTube** and **YouTube Music** — on two patch sets:
[**anddea/revanced-patches**](https://github.com/anddea/revanced-patches) (ReVanced Extended) and
[**MorpheApp/morphe-patches**](https://github.com/MorpheApp/morphe-patches) (Morphe), with voice-over translation via
[**Yandex VoT**](https://github.com/sashade8-ship-it/morphe-patches-yavot).

Every build ships in two flavors — **Root** (a Magisk/KernelSU module) and **NonRoot** (a plain APK) — and gets
rebuilt automatically as soon as new patches are released.

[![Latest release](https://img.shields.io/github/v/release/MANCrimSon/YouTube-ReVanced-Extended?label=release&color=blue)](../../releases/latest)
[![Build Modules](../../actions/workflows/build.yml/badge.svg)](../../actions/workflows/build.yml)

## Download

All builds are in [**Releases**](../../releases/latest) — grab the file by name:

| App | Patches | Non-Root (APK) | Root (module) |
|---|---|---|---|
| YouTube | ReVanced Extended (anddea) | `youtube-revanced-extended-*.apk` | `youtube-revanced-extended-module-*.zip` |
| YouTube Music | ReVanced Extended (anddea) | `youtube-music-revanced-extended-*.apk` | `youtube-music-revanced-extended-module-*.zip` |
| YouTube | Morphe (+ Yandex VoT) | `youtube-morphe-*.apk` | `youtube-morphe-module-*.zip` |
| YouTube Music | Morphe | `youtube-music-morphe-*.apk` | `youtube-music-morphe-module-*.zip` |

## Root — installation

<details>
<summary>Instructions</summary>

1. Install the module archive via Magisk/KernelSU → reboot.
2. Detach the app from Google Play updates, otherwise Play Store will overwrite the patch:
   - Magisk: enable Zygisk.
   - KernelSU: install [**ZygiskNext**](https://github.com/Dr-TSNG/ZygiskNext/releases).
   - Install [**zygisk-detach**](https://github.com/j-hc/zygisk-detach/releases) +
     [**zygisk-detach-app**](https://github.com/j-hc/zygisk-detach-app/releases), grant root → reboot.
   - In zygisk-detach-app, pick the app → Detach.
3. KernelSU only: KSU App → Superuser → app → Unmount modules: Off → reboot the device.
4. To update: hit "Update" in Magisk/KernelSU, or just flash the new archive on top.

If you get a **"Reflash needed"** error after rebooting, or root detectors complain about a
**"Suspicious mount detected"** — try [**rvmm-zygisk-mount**](https://github.com/j-hc/rvmm-zygisk-mount).
</details>

## NonRoot — installation and auto-updates via Obtainium

The APK installs like a regular app and needs no root, but YouTube/YouTube Music also need
[**MicroG RE**](https://github.com/MorpheApp/MicroG-RE/releases) — it spoofs the Google services signature; without it
the apps won't launch.

You don't have to check for updates by hand — [**Obtainium**](https://github.com/ImranR98/Obtainium) can watch
releases in this repository and prompt you when a new build is out. Install Obtainium, then tap each link below — it
opens directly in the app with the source and APK filter already configured, just hit "Add":

| What to import | Link |
|---|---|
| MicroG RE *(needed once, shared by every build)* | [Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.revanced.android.gms%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMorpheApp%2FMicroG-RE%22%2C%22author%22%3A%22MorpheApp%22%2C%22name%22%3A%22MicroG%20RE%22%2C%22additionalSettings%22%3A%22%7B%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%7D%22%7D) |
| YouTube — ReVanced Extended | [Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22anddea.youtube%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YouTube%20RVX%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-revanced-extended%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Afalse%7D%22%7D) |
| YouTube Music — ReVanced Extended | [Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22anddea.youtube.music%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YT%20Music%20RVX%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-music-revanced-extended%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%7D%22%7D) |
| YouTube — Morphe | [Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.morphe.android.youtube%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YouTube%20Morphe%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-morphe%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Afalse%7D%22%7D) |
| YouTube Music — Morphe | [Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.morphe.android.apps.youtube.music%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YT%20Music%20Morphe%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-music-morphe%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%7D%22%7D) |

These links only work on a device with Obtainium installed (they're `obtainium://` deep links, they won't do
anything in a desktop browser).

## Build details

- Rebuilt daily via GitHub Actions as soon as [anddea](https://github.com/anddea/revanced-patches) or
  [Morphe](https://github.com/MorpheApp/morphe-patches) publish new dev patches — only the apps that actually have
  something new get rebuilt.
- The Morphe YouTube build is additionally patched with
  [**Yandex VoT**](https://github.com/sashade8-ship-it/morphe-patches-yavot) — voice-over translation via Yandex, on
  top of Morphe's built-in translation.
- Every release includes a changelog: which patches version was used and what changed in it.

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
