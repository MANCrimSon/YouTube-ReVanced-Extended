# Project Status: YouTube-ReVanced-Extended

## 1. Current State and Date
- **Date**: 2026-09-14
- **Summary**: Verified and finalized patch changelog routing across all variants: Dual-VoT maps strictly to `sashade8-ship-it/dual-vot-patches`, official Morphe to `MorpheApp/morphe-patches`, and RVX to `anddea/revanced-patches`.
- **Commit**: Accurate patch repository changelogs.

## 2. What Was Done and Verified
- **Files Modified**:
  - `src/app/morphe/extension/jhc/JhcUpdateCheckPatch.java`:
    - Updated `getPatchChangelogUrl` and `extractPatchInfo` to map dual-vot releases strictly to `sashade8-ship-it/dual-vot-patches/releases/tag/v...`.
    - Handled fallback gracefully: if dual-vot only or morphe only is present, the app correctly resolves the available patch changelog without cross-brand contamination.
    - Verified all 4 scenarios (Dual-VoT, MorpheApp official, anddea RVX, single-source release).
  - `bin/update-check.mpp`:
    - Recompiled with clean DEX bytecode using `python build_patch.py`.
- **Quick Verification Commands**:
  - Compile patch: `python build_patch.py`
  - Run update matrix: `python tests/test_update_matrix.py`

## 3. Key Decisions and Rationale
- **Exact Patch Source Attribution**:
  - Clicking "Список изменений" for Dual-VoT builds opens the specific Dual-VoT release notes (`sashade8-ship-it/dual-vot-patches`), which details the translation and audio features and links directly to the upstream MorpheApp base.
  - No raw markdown or unintended manager files are ever exposed to the user browser.

## 4. Limitations and Gotchas
- None. All release tags match GitHub release schema across all repositories.

## 5. Next Steps
- Commit and push changes to `origin/main`.
- Trigger full build in GitHub Actions.

