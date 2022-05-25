#!/bin/bash

set -euo pipefail

sbt clean assembly

rm -rf bin
mkdir bin
cp avro-tools/target/scala-*/avro-tools-*.jar parquet-cli/target/scala-*/parquet-cli-*.jar proto-tools/target/scala-*/proto-tools-*.jar magnolify-tools/target/scala-*/magnolify-tools-*.jar bin
cp scripts/blob-tools bin

cd bin
ln -s blob-tools avro-tools
ln -s blob-tools parquet-cli
ln -s blob-tools proto-tools
ln -s blob-tools magnolify-tools
