# Project Status: YouTube-ReVanced-Extended

## 1. Current State and Date
- **Date**: 2026-09-14
- **Summary**: Strict enforcement of changelog routing: for all Morphe builds (including Dual-VoT forks), the changelog points directly to upstream `MorpheApp/morphe-patches/releases/tag/v<baseVer>`. For RVX builds, points to `anddea/revanced-patches/releases/tag/v<version>`.
- **Commit**: Route Dual-VoT changelog strictly to upstream MorpheApp base release.

## 2. What Was Done and Verified
- **Files Modified**:
  - `src/app/morphe/extension/jhc/JhcUpdateCheckPatch.java`:
    - In `getPatchChangelogUrl` and `extractPatchInfo`, when `dualvot` is present, stripped the `-dualvot.*` suffix and routed to `https://github.com/MorpheApp/morphe-patches/releases/tag/v<baseVer>`.
    - Maintained fallback resilience: even if a release contains only dual-vot patches, the base MorpheApp version is extracted and linked to MorpheApp releases.
    - Completely eliminated any link to raw markdown files.
  - `bin/update-check.mpp`:
    - Recompiled with clean DEX bytecode using `python build_patch.py`.
  - `tests/test_update_matrix.py`:
    - Synchronized simulator tests with base MorpheApp resolution for Dual-VoT targets.
- **Quick Verification Commands**:
  - Compile patch: `python build_patch.py`
  - Run update matrix: `python tests/test_update_matrix.py`

## 3. Key Decisions and Rationale
- **Dual-VoT to Upstream MorpheApp**:
  - In agreement with user requirements, Dual-VoT builds always link to the official upstream MorpheApp release notes (`MorpheApp/morphe-patches/releases/tag/v<baseVer>`).

## 4. Limitations and Gotchas
- None.

## 5. Next Steps
- Commit and push changes to `origin/main`.
- Trigger full build in GitHub Actions.

