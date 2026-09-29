import os
import sys
import glob
import shutil
import subprocess
import zipfile
import struct
import re

repo_dir = os.path.dirname(os.path.abspath(__file__))
java_src = os.path.join(repo_dir, 'src', 'app', 'morphe', 'extension', 'jhc', 'JhcUpdateCheckPatch.java')
mpp_file = os.path.join(repo_dir, 'bin', 'update-check.mpp')

# 1. Substitute build code if set (e.g. in GitHub Actions)
next_ver = os.environ.get('NEXT_VER_CODE', '').strip()

if next_ver and next_ver.isdigit():
    print(f'[0/4] Injecting build number: BUILD={next_ver}')
    with open(java_src, 'r', encoding='utf-8') as f:
        src_content = f.read()
    src_content = re.sub(r'EMBEDDED_BUILD_CODE = [0-9]+;', f'EMBEDDED_BUILD_CODE = {next_ver};', src_content)
    with open(java_src, 'w', encoding='utf-8') as f:
        f.write(src_content)

def get_java_home():
    jh = os.environ.get('JAVA_HOME')
    if jh and os.path.exists(jh):
        return jh
    if sys.platform == 'win32':
        import winreg
        for root in (winreg.HKEY_CURRENT_USER, winreg.HKEY_LOCAL_MACHINE):
            for subkey in (r'Environment', r'SYSTEM\CurrentControlSet\Control\Session Manager\Environment'):
                try:
                    with winreg.OpenKey(root, subkey) as key:
                        val, _ = winreg.QueryValueEx(key, 'JAVA_HOME')
                        if val and os.path.exists(val):
                            return val
                except OSError:
                    pass
    return None

def find_jdk_tool(tool_name):
    found = shutil.which(tool_name)
    if found:
        return found
    jh = get_java_home()
    if jh:
        ext = '.exe' if sys.platform == 'win32' else ''
        p = os.path.join(jh, 'bin', f'{tool_name}{ext}')
        if os.path.exists(p):
            return p
    return None

javac_bin = find_jdk_tool('javac')
java_bin = find_jdk_tool('java')

android_jar = None
jar_candidates = [
    os.path.join(repo_dir, 'libs', 'android.jar'),
    os.path.join(os.environ.get('ANDROID_SDK_ROOT', ''), 'platforms', 'android-*', 'android.jar'),
    os.path.join(os.environ.get('ANDROID_HOME', ''), 'platforms', 'android-*', 'android.jar'),
    '/usr/local/lib/android/sdk/platforms/android-*/android.jar',
    '/opt/android-sdk/platforms/android-*/android.jar',
    '/opt/android/sdk/platforms/android-*/android.jar',
]
for c in jar_candidates:
    matches = sorted(glob.glob(c))
    if matches:
        android_jar = matches[-1]
        break

d8_bin = shutil.which('d8')
if not d8_bin:
    d8_candidates = [
        os.path.join(os.environ.get('ANDROID_SDK_ROOT', ''), 'build-tools', '*', 'd8'),
        os.path.join(os.environ.get('ANDROID_HOME', ''), 'build-tools', '*', 'd8'),
        '/usr/local/lib/android/sdk/build-tools/*/d8',
        '/opt/android-sdk/build-tools/*/d8',
        '/opt/android/sdk/build-tools/*/d8',
    ]
    for c in d8_candidates:
        matches = sorted(glob.glob(c))
        if matches:
            d8_bin = matches[-1]
            break

r8_jar = os.path.join(repo_dir, 'libs', 'r8.jar')

assert javac_bin and os.path.exists(javac_bin), f'javac not found: {javac_bin}'
assert android_jar and os.path.exists(android_jar), f'android.jar not found: {android_jar}'
assert (d8_bin and os.path.exists(d8_bin)) or os.path.exists(r8_jar), 'Neither d8 nor r8.jar found'

build_dir = os.path.join(repo_dir, '.build_tmp')
if os.path.exists(build_dir):
    shutil.rmtree(build_dir)
os.makedirs(build_dir)

classes_dir = os.path.join(build_dir, 'classes')
os.makedirs(classes_dir)

print('[1/4] Compiling Java with javac (encoding UTF-8)...')
cmd_javac = [
    javac_bin,
    '-encoding', 'UTF-8',
    '-cp', android_jar,
    '-d', classes_dir,
    java_src
]
res = subprocess.run(cmd_javac, capture_output=True, text=True)
if res.stdout: print(res.stdout)
if res.stderr: print(res.stderr)
assert res.returncode == 0, f'javac failed with code {res.returncode}'

class_files = []
for root, dirs, files in os.walk(classes_dir):
    for f in files:
        if f.endswith('.class'):
            class_files.append(os.path.join(root, f))
print(f'Found {len(class_files)} compiled class files.')

print('[2/4] Running D8 to produce classes.dex...')
dex_dir = os.path.join(build_dir, 'dex')
os.makedirs(dex_dir)

if d8_bin and os.path.exists(d8_bin):
    cmd_d8 = [d8_bin, '--lib', android_jar, '--output', dex_dir] + class_files
else:
    cmd_d8 = [java_bin, '-cp', r8_jar, 'com.android.tools.r8.D8', '--lib', android_jar, '--output', dex_dir] + class_files

res = subprocess.run(cmd_d8, capture_output=True, text=True)
if res.stdout: print(res.stdout)
if res.stderr: print(res.stderr)
assert res.returncode == 0, f'd8 failed with code {res.returncode}'

dex_path = os.path.join(dex_dir, 'classes.dex')
assert os.path.exists(dex_path), 'classes.dex was not generated'
print(f'classes.dex size: {os.path.getsize(dex_path)} bytes')

print('[3/4] Verifying strings in DEX...')
with open(dex_path, 'rb') as f:
    data = f.read()

string_ids_size, string_ids_off = struct.unpack('<II', data[56:64])
dex_strings = []
for i in range(string_ids_size):
    off = struct.unpack('<I', data[string_ids_off + i*4 : string_ids_off + (i+1)*4])[0]
    idx = off
    while data[idx] & 0x80: idx += 1
    idx += 1
    null_idx = data.find(b'\x00', idx)
    raw = data[idx:null_idx]
    try:
        s = raw.decode('utf-8', errors='surrogatepass')
    except Exception:
        s = raw.decode('utf-8', errors='replace')
    dex_strings.append(s)

corrupted = any('\ufffd' in s for s in dex_strings)
assert not corrupted, 'DEX contains corrupted characters!'
print('DEX string verification: CLEAN (no corrupted chars)')

print(f'[4/4] Updating extensions/jhc.mpe inside {os.path.basename(mpp_file)}...')
mpp_temp = mpp_file + '.tmp'
with zipfile.ZipFile(mpp_file, 'r') as zin, zipfile.ZipFile(mpp_temp, 'w', compression=zipfile.ZIP_DEFLATED) as zout:
    for item in zin.infolist():
        if item.filename == 'extensions/jhc.mpe':
            zout.write(dex_path, 'extensions/jhc.mpe')
            print('  Replaced extensions/jhc.mpe')
        else:
            zout.writestr(item, zin.read(item.filename))

os.replace(mpp_temp, mpp_file)
shutil.rmtree(build_dir, ignore_errors=True)
print(f'Done! Successfully updated {mpp_file}, new size: {os.path.getsize(mpp_file)} bytes')
