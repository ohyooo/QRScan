#!/usr/bin/env bash
set -euo pipefail

module=${1:?Android application module required}
app=${2:?Application name required}
output=ci-output/abi-apks
work=$(mktemp -d "${RUNNER_TEMP:-/tmp}/abi-apks.XXXXXX")
trap 'rm -rf "$work"' EXIT

if [[ -z "${APK_KEYSTORE:-}" ]]; then
    APK_KEYSTORE="$HOME/.android/debug.keystore"
    APK_KEY_ALIAS=androiddebugkey
    APK_STORE_PASSWORD=android
    APK_KEY_PASSWORD=android
    if [[ ! -f "$APK_KEYSTORE" ]]; then
        mkdir -p "$(dirname "$APK_KEYSTORE")"
        keytool -genkeypair -keystore "$APK_KEYSTORE" -storepass android \
            -keypass android -alias androiddebugkey -keyalg RSA -keysize 2048 \
            -validity 10000 -dname 'CN=Android Debug,O=Android,C=US'
    fi
fi
: "${APK_KEY_ALIAS:?Signing key alias required}"
: "${APK_STORE_PASSWORD:?Keystore password required}"
: "${APK_KEY_PASSWORD:?Signing key password required}"
export APK_STORE_PASSWORD APK_KEY_PASSWORD

bundletool_version=1.18.3
bundletool="${RUNNER_TEMP:-/tmp}/bundletool-all-$bundletool_version.jar"
if [[ ! -f "$bundletool" ]]; then
    curl --fail --location --retry 3 \
        "https://github.com/google/bundletool/releases/download/$bundletool_version/bundletool-all-$bundletool_version.jar" \
        --output "$bundletool"
fi
bundles=("$module"/build/outputs/bundle/release/*.aab)
[[ ${#bundles[@]} -eq 1 && -f "${bundles[0]}" ]]
java -jar "$bundletool" build-apks --bundle="${bundles[0]}" \
    --output="$work/universal.apks" --mode=universal \
    --ks="$APK_KEYSTORE" --ks-key-alias="$APK_KEY_ALIAS" \
    --ks-pass="pass:$APK_STORE_PASSWORD" --key-pass="pass:$APK_KEY_PASSWORD"
unzip -p "$work/universal.apks" universal.apk > "$work/universal.apk"

# Keep every resource and language; only native libraries vary between APKs
python3 - "$work/universal.apk" "$work" <<'PY'
from copy import copy
from pathlib import Path
import re
import sys
import zipfile

abis = ("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
with zipfile.ZipFile(sys.argv[1]) as source:
    native_abis = {p.split("/")[1] for p in source.namelist() if p.startswith("lib/") and p.endswith(".so")}
    for abi in abis:
        if native_abis and abi not in native_abis:
            raise RuntimeError(f"AAB is missing native libraries for {abi}")
        with zipfile.ZipFile(Path(sys.argv[2]) / f"{abi}-unsigned.apk", "w") as target:
            for entry in source.infolist():
                if entry.filename.startswith("lib/") and not entry.filename.startswith(f"lib/{abi}/"):
                    continue
                if re.fullmatch(r"META-INF/([^/]+\.(SF|RSA|DSA|EC)|MANIFEST\.MF)", entry.filename, re.IGNORECASE):
                    continue
                target.writestr(copy(entry), source.read(entry))
PY

build_tools=$(printf '%s\n' "${ANDROID_HOME:?Android SDK required}"/build-tools/* | sort -V | tail -n 1)
mkdir -p "$output"
for abi in arm64-v8a armeabi-v7a x86 x86_64; do
    "$build_tools/zipalign" -P 16 -f 4 "$work/$abi-unsigned.apk" "$work/$abi-aligned.apk"
    "$build_tools/apksigner" sign --ks "$APK_KEYSTORE" --ks-key-alias "$APK_KEY_ALIAS" \
        --ks-pass env:APK_STORE_PASSWORD --key-pass env:APK_KEY_PASSWORD \
        --v4-signing-enabled false --out "$output/$app-$abi.apk" "$work/$abi-aligned.apk"
    "$build_tools/apksigner" verify "$output/$app-$abi.apk"
    "$build_tools/zipalign" -c -P 16 4 "$output/$app-$abi.apk"
done

mapping="$module/build/outputs/mapping/release/mapping.txt"
if [[ -f "$mapping" ]]; then
    cp "$mapping" "$output/$app-mapping.txt"
fi
