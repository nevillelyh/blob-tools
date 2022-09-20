#!/bin/bash

set -euo pipefail

die() {
    echo "[FAIL] $*"
    echo "============================================================"
    cat "$out"
    echo "============================================================"
    cleanup
    exit 1
}

setup() {
    case "$fs" in
        gs)
            gsutil cp test-files/* "$uri"
            ;;
        s3)
            find test-files -type f -exec aws s3 cp {} "$uri/" \;
            ;;
    esac
}

test_cmd() {
    match=$1
    shift
    cmd=$*
    echo "[TEST] $cmd"
    $cmd > "$out" 2>&1 || die "$cmd"
    grep -q "$match" "$out" || die "$cmd"
    echo "[PASS] $cmd"
}

cleanup() {
    echo "[INFO] Cleaning up $out"
    rm "$out"
    echo "[INFO] Cleaning up $uri"
    case "$fs" in
        gs)
            gsutil rm -r "$uri/*"
            ;;
        s3)
            aws s3 rm --recursive "$uri"
            ;;
    esac
}

if [ $# -eq 0 ]; then
    fs=""
    uri=test-files
    ts=$(date "+%s")
    out="/tmp/blob-tools-$ts"
    echo "[INFO] Local temporary file: $out"
elif [ $# -eq 1 ]; then
    fs="$(echo "$1" | cut -d ":" -f 1)"
    if [[ "$fs" != "gs" ]] && [[ "$fs" != "s3" ]]; then
        echo "File system not supported: $fs"
        exit 1
    fi

    prefix=$1
    ts=$(date "+%s")
    uri="$prefix/blob-tools-$ts"
    echo "[INFO] Temporary location: $uri"
    out="/tmp/blob-tools-$ts"
    echo "[INFO] Local temporary file: $out"
    setup
else
    echo "Usage: test.sh <[gs|s3]://temp/location>"
    exit 1
fi

./make-binary.sh

echo "============================================================"

test_cmd 'Account' ./bin/avro-tools getschema "$uri/test.avro"
test_cmd '"name":"user100"' ./bin/avro-tools tojson "$uri/test.avro"

test_cmd 'user99' ./bin/orc-tools meta "$uri/test.orc"
test_cmd '"name":"user100"' ./bin/orc-tools data "$uri/test.orc"

test_cmd 'Account' ./bin/parquet-cli schema "$uri/test.parquet"
test_cmd 'Account' ./bin/parquet-cli meta "$uri/test.parquet"
test_cmd '"name": "user100"' ./bin/parquet-cli cat "$uri/test.parquet"

echo "============================================================"

cleanup
