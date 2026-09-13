# YouTube & YT Music Mod Builder

Automated builds of modified **YouTube** and **YouTube Music** – on two patch sets:
[**anddea/revanced-patches**](https://github.com/anddea/revanced-patches) (ReVanced Extended) and
[**MorpheApp/morphe-patches**](https://github.com/MorpheApp/morphe-patches) (Morphe), with voice-over translation via
[**Yandex VoT**](https://github.com/sashade8-ship-it/morphe-patches-yavot).

Every build ships in two flavors – **NonRoot** (a plain APK) and **Root** (a Magisk/KernelSU module) – and gets
rebuilt automatically as soon as new patches are released.

<!--release--><a href="../../releases/latest"><img src="https://img.shields.io/badge/release-v422-blue" alt="Latest release"></a><!--/release--> <!--downloads--><a href="../../releases"><img src="https://img.shields.io/badge/downloads-481.0k-success" alt="Downloads"></a><!--/downloads-->

## Download

The links below always point straight at the file, kept up to date automatically after every build (see full history
in [**Releases**](../../releases)). YouTube Music ships two architectures – pick the one matching your device.

| Build | NonRoot (APK) | Root (module) |
|---|---|---|
| YouTube – ReVanced Extended (anddea) | <!--yt-rvx-apk-->[Download](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/422/youtube-revanced-extended-v21.07.247-all.apk)<!--/yt-rvx-apk--> | <!--yt-rvx-zip-->[Download](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/422/youtube-revanced-extended-module-v21.07.247-all.zip)<!--/yt-rvx-zip--> |
| YouTube Music – ReVanced Extended (anddea) | <!--ytm-rvx-apk-arm64-->[arm64-v8a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/422/youtube-music-revanced-extended-v9.15.51-arm64-v8a.apk)<!--/ytm-rvx-apk-arm64--><br><br><!--ytm-rvx-apk-armv7-->[arm-v7a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/422/youtube-music-revanced-extended-v9.15.51-arm-v7a.apk)<!--/ytm-rvx-apk-armv7--> | <!--ytm-rvx-zip-arm64-->[arm64-v8a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/422/youtube-music-revanced-extended-module-v9.15.51-arm64-v8a.zip)<!--/ytm-rvx-zip-arm64--><br><br><!--ytm-rvx-zip-armv7-->[arm-v7a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/422/youtube-music-revanced-extended-module-v9.15.51-arm-v7a.zip)<!--/ytm-rvx-zip-armv7--> |
| YouTube – Morphe (+ Yandex VoT) | <!--yt-morphe-apk-->[Download](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/422/youtube-morphe-v21.13.164-all.apk)<!--/yt-morphe-apk--> | <!--yt-morphe-zip-->[Download](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/422/youtube-morphe-module-v21.13.164-all.zip)<!--/yt-morphe-zip--> |
| YouTube Music – Morphe | <!--ytm-morphe-apk-arm64-->[arm64-v8a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/422/youtube-music-morphe-v9.15.51-arm64-v8a.apk)<!--/ytm-morphe-apk-arm64--><br><br><!--ytm-morphe-apk-armv7-->[arm-v7a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/422/youtube-music-morphe-v9.15.51-arm-v7a.apk)<!--/ytm-morphe-apk-armv7--> | <!--ytm-morphe-zip-arm64-->[arm64-v8a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/422/youtube-music-morphe-module-v9.15.51-arm64-v8a.zip)<!--/ytm-morphe-zip-arm64--><br><br><!--ytm-morphe-zip-armv7-->[arm-v7a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/422/youtube-music-morphe-module-v9.15.51-arm-v7a.zip)<!--/ytm-morphe-zip-armv7--> |

## NonRoot (APK)

<details>
<summary><b>Installation, Updating & Troubleshooting Instructions</b></summary>

#### Installation
1. Install [**MicroG RE**](https://github.com/MorpheApp/MicroG-RE/releases) *(required to run without Root and sign into your Google account)*.
2. Download and install the matching APK from the download table above.

#### Updating
* **In-App Updater:** Check for updates directly inside the app:
  * **YouTube & YouTube Music (RVX / anddea):** `Settings` → `RVX` → `Patch updates`
  * **YouTube & YouTube Music (Morphe):** `Settings` → `Morphe` → `Patch updates`
  * *Features: version comparison, changelog preview, direct APK download, and snooze options.*
* **Via Obtainium (Auto-updates):**  
  Install [**Obtainium**](https://github.com/ImranR98/Obtainium) and tap the links below directly on your Android device to import preconfigured update profiles:

  | App & Patch Set | Obtainium Quick Import |
  |---|---|
  | **MicroG RE** *(shared across all builds)* | [Import MicroG RE](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.revanced.android.gms%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMorpheApp%2FMicroG-RE%22%2C%22author%22%3A%22MorpheApp%22%2C%22name%22%3A%22MicroG%20RE%22%2C%22additionalSettings%22%3A%22%7B%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Emicrog%28%3F%21.%2Anoicon%29%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%7D%22%7D) |
  | **YouTube** – ReVanced Extended (anddea) | [Import YouTube RVX](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22anddea.youtube%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YouTube%20RVX%20%28anddea%29%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-revanced-extended%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Afalse%2C%5C%22appName%5C%22%3A%5C%22YouTube%20RVX%20%28anddea%29%5C%22%7D%22%7D) |
  | **YouTube Music** – ReVanced Extended (anddea) | [Import YT Music RVX](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22anddea.youtube.music%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YT%20Music%20RVX%20%28anddea%29%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-music-revanced-extended%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%2C%5C%22appName%5C%22%3A%5C%22YT%20Music%20RVX%20%28anddea%29%5C%22%7D%22%7D) |
  | **YouTube** – Morphe (+ Yandex VoT) | [Import YouTube Morphe](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.morphe.android.youtube%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YouTube%20Morphe%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-morphe%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Afalse%7D%22%7D) |
  | **YouTube Music** – Morphe | [Import YT Music Morphe](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.morphe.android.apps.youtube.music%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YT%20Music%20Morphe%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-music-morphe%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%7D%22%7D) |

#### Troubleshooting playback issues (infinite buffering)

<details>
<summary><b>ReVanced Extended (anddea) – YouTube & YouTube Music</b></summary>

##### 1. Primary solution – Built-in PoToken (Recommended)
* **YouTube:**
  1. Turn **OFF** stream spoofing: `Settings` → `RVX` → `Miscellaneous` → `Spoof video streams` → `Spoof video streams` (**Off**).
  2. Turn **ON** PoToken: `Settings` → `RVX` → `Miscellaneous` → `Spoof video streams` → `PoToken provider` (**On**).
* **YouTube Music:**
  1. Turn **OFF** stream spoofing: `Settings` → `RVX` → `Miscellaneous` → `Spoof video streams` (**Off**).
  2. Turn **ON** PoToken: `Settings` → `RVX` → `Miscellaneous` → `PoToken provider` (**On**).
* Restart the app (if buffering persists, force stop **MicroG RE** in Android system settings).

##### 2. Fallback solution – Spoof video streams
* **YouTube:** `Settings` → `RVX` → `Miscellaneous` → `Spoof video streams`:
  1. Turn **OFF** PoToken: `PoToken provider` (**Off**).
  2. Turn **ON** stream spoofing: `Spoof video streams` (**On**) and select a suitable default client.
* **YouTube Music:** `Settings` → `RVX` → `Miscellaneous`:
  1. Turn **OFF** PoToken: `PoToken provider` (**Off**).
  2. Turn **ON** stream spoofing: `Spoof video streams` (**On**) and select a suitable default client.
* Restart the app.

> [!NOTE]
> Due to regional network restrictions or DPI blocking, playback may still be affected by ISP filters – verify your network bypass or VPN configuration if needed.

</details>

<details>
<summary><b>Morphe – YouTube & YouTube Music</b></summary>

##### 1. Primary solution – External PoToken via PotHelper (Recommended)
1. Install [**PotHelper**](https://github.com/MorpheApp/PotHelper/releases/latest) ([Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.morphe.pot.helper%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMorpheApp%2FPotHelper%22%2C%22author%22%3A%22MorpheApp%22%2C%22name%22%3A%22PotHelper%22%2C%22additionalSettings%22%3A%22%7B%5C%22fallbackToOlderReleases%5C%22%3Atrue%7D%22%7D)).
2. Configure settings:
   * **YouTube:** `Settings` → `Morphe` → `Miscellaneous`:
     - Turn **OFF** stream spoofing: `Spoof video streams` → `Spoof video streams` (**Off**).
     - Turn **ON** PoToken: `PoToken provider` → `External PoToken provider` (**On**).
   * **YouTube Music:** `Settings` → `Morphe` → `Miscellaneous`:
     - Turn **OFF** stream spoofing: `Spoof video streams` → `Spoof video streams` (**Off**).
     - Turn **ON** PoToken: `PoToken provider` → `External PoToken provider` (**On**).
3. Restart the app.

##### 2. Fallback solution – Spoof video streams
1. Turn **OFF** PoToken: `PoToken provider` → `External PoToken provider` (**Off**).
2. Turn **ON** stream spoofing: `Spoof video streams` → `Spoof video streams` (**On**) and select a suitable default client.
3. Restart the app.

> [!NOTE]
> Due to regional network restrictions or DPI blocking, playback may still be affected by ISP filters – verify your network bypass or VPN configuration if needed.

</details>

</details>

## Root (Magisk / KernelSU)

<details>
<summary><b>Installation, Updating & Troubleshooting Instructions</b></summary>

#### Installation
1. Flash the module zip archive via **Magisk** or **KernelSU**.
2. Reboot the device.

#### Updating
* **In Root Manager:** Tap the **Update** button in Magisk / KernelSU (modules check for updates automatically).
* **Manually:** Flash the new zip archive on top of the existing one without uninstalling.

#### Detach from Google Play (prevent automatic updates)
1. Enable **Zygisk**:
   * **Magisk:** Enable Zygisk in Magisk settings.
   * **KernelSU / APatch:** Install the [**ZygiskNext**](https://github.com/Dr-TSNG/ZygiskNext/releases) module.
2. Install the [**zygisk-detach**](https://github.com/j-hc/zygisk-detach/releases) module and [**zygisk-detach-app**](https://github.com/j-hc/zygisk-detach-app/releases) APK, grant Root permissions → reboot the device.
3. Open **zygisk-detach-app**, select your target app (**YouTube** / **YouTube Music**) → tap **Detach**.

#### Troubleshooting
* **KernelSU only (blank/stock app or launch issues):**  
  Open `KernelSU App` → `Superuser` → select target app (**YouTube** / **YouTube Music**) → *(Custom, if present)* → set `Unmount modules` to **Off** → reboot the device.
* **Mounting errors (*"Reflash needed"* / *"Suspicious mount detected"*):**  
  Install the [**rvmm-zygisk-mount**](https://github.com/j-hc/rvmm-zygisk-mount) module to resolve overlayfs / mount namespace conflicts.

</details>

## Building your own config

Want to build a different set of apps/patches? Use this repository as a base and edit
[`config.toml`](./config.toml) – the key format is documented in [`CONFIG.md`](./CONFIG.md). Manual builds run from
[Actions → Build Modules](../../actions/workflows/build.yml) (workflow_dispatch; pass `only_apps` to build a single
app instead of everything).

## Credits

This build system started as a fork of [**j-hc/revanced-magisk-module**](https://github.com/j-hc/revanced-magisk-module) –
huge thanks to [**j-hc**](https://github.com/j-hc) for the original builder, the module template, and the tooling
(`zygisk-detach`, `rvmm-zygisk-mount`) this repo still relies on.

Patches and translation add-on come from [**anddea**](https://github.com/anddea/revanced-patches),
[**MorpheApp**](https://github.com/MorpheApp/morphe-patches), and [**sashade8-ship-it**](https://github.com/sashade8-ship-it/morphe-patches-yavot).
