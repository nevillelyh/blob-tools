#!/bin/bash

set -euo pipefail

sbt clean assembly

rm -rf bin
mkdir bin
cp avro-tools/target/scala-*/avro-tools-*.jar \
    orc-tools/target/scala-*/orc-tools-*.jar \
    parquet-cli/target/scala-*/parquet-cli-*.jar \
    bin
cp scripts/blob-tools bin

cd bin
ln -s blob-tools avro-tools
ln -s blob-tools orc-tools
ln -s blob-tools parquet-cli
