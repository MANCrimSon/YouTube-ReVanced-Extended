#!/usr/bin/env python3
import sys
import time
import zipfile
import struct
import hashlib
import zlib
import shutil
import os

def patch_dex_bytes(dex: bytearray) -> bool:
    """
    Patches getDefaultIconStyle() in DEX to return ORIGINAL instead of BLACK.
    Automatically discovers field IDs for BLACK and ORIGINAL dynamically.
    """
    if len(dex) < 0x70:
        return False

    # 1. Parse string pool
    string_ids_size, string_ids_off = struct.unpack_from('<II', dex, 0x38)
    strings = []
    for i in range(string_ids_size):
        str_off, = struct.unpack_from('<I', dex, string_ids_off + i*4)
        p = str_off; length = 0; shift = 0
        while True:
            b = dex[p]; p += 1
            length |= (b & 0x7f) << shift
            if not (b & 0x80): break
            shift += 7
        end = dex.find(b'\x00', p)
        strings.append(dex[p:end].decode('utf-8', 'ignore'))

    type_ids_size, type_ids_off = struct.unpack_from('<II', dex, 0x40)
    types = [strings[struct.unpack_from('<I', dex, type_ids_off + i*4)[0]] for i in range(type_ids_size)]

    # 2. Find field IDs for BLACK and ORIGINAL in BrandingTheme
    field_ids_size, field_ids_off = struct.unpack_from('<II', dex, 0x50)
    black_fid = None
    orig_fid = None
    for i in range(field_ids_size):
        c_idx, t_idx, n_idx = struct.unpack_from('<HHI', dex, field_ids_off + i*8)
        cname, fname = types[c_idx], strings[n_idx]
        if 'BrandingTheme' in cname:
            if fname == 'BLACK':
                black_fid = i
            elif fname == 'ORIGINAL':
                orig_fid = i

    if black_fid is None or orig_fid is None:
        return False

    target = bytes([0x62, 0x00]) + struct.pack('<H', black_fid) + bytes([0x11, 0x00])
    replacement = bytes([0x62, 0x00]) + struct.pack('<H', orig_fid) + bytes([0x11, 0x00])

    pos = dex.find(target)
    if pos == -1:
        # Check if already patched
        if dex.find(replacement) != -1:
            return True
        return False

    dex[pos:pos+len(target)] = replacement

    # 3. Update DEX header checksums
    sha1 = hashlib.sha1(dex[32:]).digest()
    dex[12:32] = sha1
    checksum = zlib.adler32(dex[12:]) & 0xffffffff
    dex[8:12] = struct.pack('<I', checksum)
    return True

def patch_class_bytes(bc: bytearray) -> bool:
    """
    Patches BaseCustomBrandingPatchKt.class:
    - original alias: enabled = iload 23 (matches default name)
    - morphe_black alias: enabled = false
    """
    modified = False

    # 1. original alias: change iconst_0 (0x03) to dup (0x59)
    p1 = bytes([0x15, 22, 0x15, 23, 0x03, 0x19, 19])
    r1 = bytes([0x15, 22, 0x15, 23, 0x59, 0x19, 19])
    idx1 = bc.find(p1)
    if idx1 != -1:
        bc[idx1:idx1+len(p1)] = r1
        modified = True
    elif bc.find(r1) != -1:
        modified = True

    # 2. morphe_black alias: change iconst_1 (0x04) to iconst_0 (0x03)
    p2 = bytes([0x04, 0xa7, 0x00, 0x04, 0x03, 0x19, 19])
    r2 = bytes([0x03, 0xa7, 0x00, 0x04, 0x03, 0x19, 19])
    idx2 = bc.find(p2)
    if idx2 != -1:
        bc[idx2:idx2+len(p2)] = r2
        modified = True
    elif bc.find(r2) != -1:
        modified = True

    return modified

def patch_mpp_file(mpp_path: str) -> bool:
    """
    Applies the patch directly to an .mpp / .jar zip archive.
    """
    if not os.path.isfile(mpp_path):
        print(f"[patch_branding] File not found: {mpp_path}")
        return False

    with zipfile.ZipFile(mpp_path, 'r') as zin:
        entries = zin.namelist()
        class_name = 'app/morphe/patches/shared/layout/branding/BaseCustomBrandingPatchKt.class'
        if class_name not in entries:
            # Not a Morphe-style bundle (e.g. anddea or other sources)
            print(f"[patch_branding] No BaseCustomBrandingPatchKt found in {os.path.basename(mpp_path)} (skipping)")
            return True

        class_data = bytearray(zin.read(class_name))
        class_ok = patch_class_bytes(class_data)

        dex_name = 'extensions/shared-youtube.mpe'
        dex_ok = True
        dex_data = None
        if dex_name in entries:
            dex_data = bytearray(zin.read(dex_name))
            dex_ok = patch_dex_bytes(dex_data)

        if not (class_ok and dex_ok):
            print(f"[patch_branding] Could not patch byte patterns in {os.path.basename(mpp_path)}")
            return False

        tmp_out = mpp_path + '.tmp'
        with zipfile.ZipFile(tmp_out, 'w', compression=zipfile.ZIP_DEFLATED) as zout:
            for item in zin.infolist():
                if item.filename == class_name:
                    zout.writestr(item, bytes(class_data))
                elif item.filename == dex_name and dex_data is not None:
                    zout.writestr(item, bytes(dex_data))
                else:
                    zout.writestr(item, zin.read(item.filename))

    shutil.move(tmp_out, mpp_path)
    return True

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('Usage: patch_branding.py <path_to_bundle.mpp>')
        sys.exit(1)

    target_file = sys.argv[1]
    t0 = time.perf_counter()
    if patch_mpp_file(target_file):
        ms = (time.perf_counter() - t0) * 1000
        print(f"[patch_branding] Custom branding default set to Original in {os.path.basename(target_file)} ({ms:.1f}ms)")
        sys.exit(0)
    else:
        sys.exit(1)
