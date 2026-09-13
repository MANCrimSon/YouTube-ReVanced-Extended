# Project Status: YouTube-ReVanced-Extended

## 1. Current State and Date
- **Date**: 2026-09-14
- **Summary**: Confirmed 100% operational in-app update checks on Samsung SM-S938B (Android 16) with Release 426! Added author GitHub repository link alongside changelog link in the update dialog with full multilingual support.
- **Commit**: Adding author GitHub link to info card.

## 2. What Was Done and Verified
- **Files Modified**:
  - `src/app/morphe/extension/jhc/JhcUpdateCheckPatch.java`:
    - Updated info card with a horizontal row: `[ 📋 Список изменений ↗ ] • [ 🧑‍💻 GitHub автора ↗ ]`.
    - Maintained non-dismissing behavior on click so that browsing links leaves the update sheet active.
    - Added full multilingual translations for Ukrainian, Russian, Belarusian, Kazakh, Spanish, German, and English.
  - `bin/update-check.mpp`:
    - Recompiled with clean DEX bytecode using `python build_patch.py`.
- **Quick Verification Commands**:
  - Compile patch: `python build_patch.py`
  - Run update matrix: `python tests/test_update_matrix.py`

## 3. Key Decisions and Rationale
- **Non-dismissing Links**: Clicking either link opens the browser via system Intent without calling `dialog.dismiss()`, preserving the update interface when returning to the app.
- **Unicode Resilience**: Used `emoji(0x1F4CB)` for clipboard and `emoji(0x1F9D1) + "\u200D" + emoji(0x1F4BB)` for developer emoji to prevent codepage corruption on Windows CP1251 environments.

## 4. Limitations and Gotchas
- Keep links layout constrained to 11.5sp to avoid wrapping on narrow 360dp screens.

## 5. Next Steps
- Commit and push changes to `origin/main`.
- Trigger GitHub Actions build when requested.

