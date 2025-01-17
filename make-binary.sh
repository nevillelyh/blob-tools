#!/bin/bash

set -euo pipefail

sbt clean assembly

rm -rf bin
mkdir bin
cp avro-tools/target/scala-2.13/avro-tools.jar \
    orc-tools/target/scala-2.13/orc-tools.jar \
    parquet-cli/target/scala-2.13/parquet-cli.jar \
    bin
cp scripts/blob-tools bin

cd bin
ln -s blob-tools avro-tools
ln -s blob-tools orc-tools
ln -s blob-tools parquet-cli
