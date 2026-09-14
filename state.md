# Project Status: YouTube-ReVanced-Extended

## 1. Current State and Date
- **Date**: 2026-09-14
- **Summary**: Fixed changelog link in in-app updater: eliminated unintended fallback to raw markdown files and ensured direct links to official GitHub patch releases (anddea/revanced-patches and MorpheApp/morphe-patches).
- **Commit**: Direct patch release changelogs.

## 2. What Was Done and Verified
- **Files Modified**:
  - `src/app/morphe/extension/jhc/JhcUpdateCheckPatch.java`:
    - Added helper `getPatchChangelogUrl(context, patchVersion)` returning the exact GitHub release URL for the patch brand.
    - Fixed Channel 1 where Magisk `update/*.json`'s raw markdown file URL was unintentionally overriding the release link.
    - Verified that RVX links to `anddea/revanced-patches/releases/tag/v...` and Morphe links to `MorpheApp/morphe-patches/releases/tag/v...`.
  - `bin/update-check.mpp`:
    - Recompiled with clean DEX bytecode using `python build_patch.py`.
- **Quick Verification Commands**:
  - Compile patch: `python build_patch.py`
  - Run update matrix: `python tests/test_update_matrix.py`

## 3. Key Decisions and Rationale
- **Direct Patch Release Links vs Raw Markdown**:
  - The `update/*.json` file contains a `changelog` field pointing to raw markdown specifically for the Magisk Manager app. In the Android in-app updater, opening raw markdown in a web browser shows unformatted raw text. The in-app updater now strictly resolves to the official patch release on GitHub.

## 4. Limitations and Gotchas
- For Dual-VoT patches, link maps to upstream base MorpheApp releases (`v1.43.0-dev.4`).

## 5. Next Steps
- Commit and push changes to `origin/main`.
- Trigger build in GitHub Actions.

