#!/system/bin/sh

MODDIR=${0%/*}
. "$MODDIR/config"

rm -f "/data/adb/rvhc/${MODDIR##*/}.apk"
rm -f "/data/adb/post-fs-data.d/$PKG_NAME-uninstall.sh"

if ! ls /data/adb/rvhc/*.apk >/dev/null 2>&1; then
	rm -rf "/data/adb/rvhc"
fi
