# YouTube & YT Music Mod Builder

Automated builds of modified **YouTube** and **YouTube Music** – on two patch sets:
[**anddea/revanced-patches**](https://github.com/anddea/revanced-patches) (ReVanced Extended) and
[**MorpheApp/morphe-patches**](https://github.com/MorpheApp/morphe-patches) (Morphe), with voice-over translation via
[**Yandex VoT**](https://github.com/sashade8-ship-it/morphe-patches-yavot).

Every build ships in two flavors – **NonRoot** (a plain APK) and **Root** (a Magisk/KernelSU/APatch module) – and gets
rebuilt automatically as soon as new patches are released.

Supports seamless **In-App Updates** and one-tap **Obtainium** auto-update profiles.

<!--release--><a href="../../releases/latest"><img src="https://img.shields.io/badge/release-v439-blue" alt="Latest release"></a><!--/release--> <!--downloads--><a href="../../releases"><img src="https://img.shields.io/badge/downloads-516.8k-success" alt="Downloads"></a><!--/downloads-->

## Download

The links below always point straight at the file, kept up to date automatically after every build (see full history
in [**Releases**](../../releases)). YouTube Music ships two architectures – pick the one matching your device.

> [!TIP]
> **Using [Obtainium](https://github.com/ImranR98/Obtainium)?**  
> Tap **Import** in the table on your Android device to automatically add the app's update profile directly into Obtainium.

| App | NonRoot<br>(APK / Obtainium) | Root<br>(module) |
| :---: | :---: | :---: |
| **YouTube**<br>RVX (anddea) | <!--yt-rvx-apk-->[universal](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/432/youtube-revanced-extended-v21.13.164-all.apk)<!--/yt-rvx-apk--><br><br>[Import](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22anddea.youtube%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YouTube%20RVX%20%28anddea%29%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-revanced-extended%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Afalse%2C%5C%22appName%5C%22%3A%5C%22YouTube%20RVX%20%28anddea%29%5C%22%7D%22%7D) | <!--yt-rvx-zip-->[universal](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/432/youtube-revanced-extended-module-v21.13.164-all.zip)<!--/yt-rvx-zip--> |
| **YouTube Music**<br>RVX (anddea) | <!--ytm-rvx-apk-arm64-->[arm64&#8209;v8a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/432/youtube-music-revanced-extended-v9.15.51-arm64-v8a.apk)<!--/ytm-rvx-apk-arm64--><br><!--ytm-rvx-apk-armv7-->[arm&#8209;v7a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/432/youtube-music-revanced-extended-v9.15.51-arm-v7a.apk)<!--/ytm-rvx-apk-armv7--><br><br>[Import](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22anddea.youtube.music%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YT%20Music%20RVX%20%28anddea%29%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-music-revanced-extended%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%2C%5C%22appName%5C%22%3A%5C%22YT%20Music%20RVX%20%28anddea%29%5C%22%7D%22%7D) | <!--ytm-rvx-zip-arm64-->[arm64&#8209;v8a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/432/youtube-music-revanced-extended-module-v9.15.51-arm64-v8a.zip)<!--/ytm-rvx-zip-arm64--><br><!--ytm-rvx-zip-armv7-->[arm&#8209;v7a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/432/youtube-music-revanced-extended-module-v9.15.51-arm-v7a.zip)<!--/ytm-rvx-zip-armv7--> |
| **YouTube**<br>Morphe (VoT) | <!--yt-morphe-apk-->[universal](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/439/youtube-morphe-v21.16.256-all.apk)<!--/yt-morphe-apk--><br><br>[Import](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.morphe.android.youtube%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YouTube%20Morphe%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-morphe%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Afalse%7D%22%7D) | <!--yt-morphe-zip-->[universal](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/439/youtube-morphe-module-v21.16.256-all.zip)<!--/yt-morphe-zip--> |
| **YouTube Music**<br>Morphe | <!--ytm-morphe-apk-arm64-->[arm64&#8209;v8a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/439/youtube-music-morphe-v9.15.51-arm64-v8a.apk)<!--/ytm-morphe-apk-arm64--><br><!--ytm-morphe-apk-armv7-->[arm&#8209;v7a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/439/youtube-music-morphe-v9.15.51-arm-v7a.apk)<!--/ytm-morphe-apk-armv7--><br><br>[Import](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.morphe.android.apps.youtube.music%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMANCrimSon%2FYouTube-ReVanced-Extended%22%2C%22author%22%3A%22MANCrimSon%22%2C%22name%22%3A%22YT%20Music%20Morphe%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22versionDetection%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Eyoutube-music-morphe%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%7D%22%7D) | <!--ytm-morphe-zip-arm64-->[arm64&#8209;v8a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/439/youtube-music-morphe-module-v9.15.51-arm64-v8a.zip)<!--/ytm-morphe-zip-arm64--><br><!--ytm-morphe-zip-armv7-->[arm&#8209;v7a](https://github.com/MANCrimSon/YouTube-ReVanced-Extended/releases/download/439/youtube-music-morphe-module-v9.15.51-arm-v7a.zip)<!--/ytm-morphe-zip-armv7--> |

## NonRoot

<details>
<summary><b>Installation & Updating Guide</b></summary>

#### Installation
**1.** Install [**MicroG RE**](https://github.com/MorpheApp/MicroG-RE/releases) ([Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.revanced.android.gms%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMorpheApp%2FMicroG-RE%22%2C%22author%22%3A%22MorpheApp%22%2C%22name%22%3A%22MicroG%20RE%22%2C%22additionalSettings%22%3A%22%7B%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5Emicrog%28%3F%21.%2Anoicon%29%5C%22%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%7D%22%7D)) *(required to run without Root and sign into your Google account)*.  
**2.** Install the matching app: download the APK from the table above, or simply tap **[Import]** to install and manage updates directly via Obtainium.

#### Updating
• **In-App Updater:** Check for updates directly inside the app (`Settings` → `RVX` / `Morphe` → `Patch updates`). Features: version comparison, changelog preview, direct APK download, and snooze.  
• **Via Obtainium (Auto-updates):** Install [**Obtainium**](https://github.com/ImranR98/Obtainium) and tap the **[Import]** links in the main Download table above to add automatic update profiles directly on your Android device.

</details>

<details>
<summary><b>Troubleshooting: Infinite Buffering / Playback Fix</b></summary>

### ReVanced Extended (anddea) – YouTube & YouTube Music

**Method 1: Built-in PoToken (Recommended)**  
**1.** Go to `Settings` → `RVX` → `Miscellaneous` → `Spoof video streams`.  
**2.** Turn **OFF** `Spoof video streams`.  
**3.** Turn **ON** `PoToken provider`.  
**4.** Restart the app *(if buffering persists, force stop **MicroG RE** in Android system settings)*.

**Method 2: Spoof video streams (Fallback)**  
**1.** Go to `Settings` → `RVX` → `Miscellaneous` → `Spoof video streams`.  
**2.** Turn **OFF** `PoToken provider`.  
**3.** Turn **ON** `Spoof video streams` and select a suitable default client.  
**4.** Restart the app.

> [!NOTE]
> Due to regional network restrictions or DPI blocking, playback may still be affected by ISP filters – verify your network bypass or VPN configuration if needed.

---

### Morphe – YouTube & YouTube Music

**Method 1: External PoToken via PotHelper (Recommended)**  
**1.** Install [**PotHelper**](https://github.com/MorpheApp/PotHelper/releases/latest) ([Import into Obtainium](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22app.morphe.pot.helper%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMorpheApp%2FPotHelper%22%2C%22author%22%3A%22MorpheApp%22%2C%22name%22%3A%22PotHelper%22%2C%22additionalSettings%22%3A%22%7B%5C%22fallbackToOlderReleases%5C%22%3Atrue%7D%22%7D)).  
**2.** Go to `Settings` → `Morphe` → `Miscellaneous`.  
**3.** Turn **OFF** `Spoof video streams` → `Spoof video streams`.  
**4.** Turn **ON** `PoToken provider` → `External PoToken provider`.  
**5.** Restart the app.

**Method 2: Spoof video streams (Fallback)**  
**1.** Go to `Settings` → `Morphe` → `Miscellaneous`.  
**2.** Turn **OFF** `PoToken provider` → `External PoToken provider`.  
**3.** Turn **ON** `Spoof video streams` → `Spoof video streams` and select a default client.  
**4.** Restart the app.

> [!NOTE]
> Due to regional network restrictions or DPI blocking, playback may still be affected by ISP filters – verify your network bypass or VPN configuration if needed.

</details>

## Root

<details>
<summary><b>Installation & Updating Guide</b></summary>

#### Installation
**1.** Flash the module zip archive via **Magisk**, **KernelSU**, or **APatch**.  
**2.** Reboot the device.

#### Updating
• **In Root Manager:** Tap the **Update** button in Magisk / KernelSU / APatch (modules check for updates automatically).  
• **Manually:** Flash the new zip archive on top of the existing one without uninstalling.

</details>

<details>
<summary><b>Detach from Google Play & Troubleshooting</b></summary>

#### Detach from Google Play (prevent automatic updates)
**1.** Enable **Zygisk** in Magisk settings (or install a standalone module for KernelSU / APatch: open-source [**ReZygisk**](https://github.com/PerformanC/ReZygisk/releases) or **Zygisk Next**).  
**2.** Flash the [**zygisk-detach**](https://github.com/j-hc/zygisk-detach/releases) module and install [**zygisk-detach-app**](https://github.com/j-hc/zygisk-detach-app/releases) APK, grant Root permissions → reboot the device.  
**3.** Open **zygisk-detach-app**, select your target app (**YouTube** / **YouTube Music**) → tap **Detach**.

#### Troubleshooting
• **KernelSU only (blank/stock app or launch issues):**  
Open `KernelSU App` → `Superuser` → select target app (**YouTube** / **YouTube Music**) → *(Custom, if present)* → set `Unmount modules` to **Off** → reboot the device.  
• **Mounting errors (*"Reflash needed"* / *"Suspicious mount detected"*):**  
Install the [**rvmm-zygisk-mount**](https://github.com/j-hc/rvmm-zygisk-mount) module to resolve overlayfs / mount namespace conflicts.  
• **Playback issues & buffering:**  
Go to `Settings` → `RVX` / `Morphe` → `Miscellaneous` → `Spoof video streams`:  
**1.** Try turning **OFF** `Spoof video streams` and restart the app.  
**2.** If playback issues persist, turn **ON** `Spoof video streams`, change the **Default client**, and restart the app.

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
