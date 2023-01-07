Blob Tools
==========

[![Build Status](https://img.shields.io/github/actions/workflow/status/nevillelyh/blob-tools/ci.yml?branch=main)](https://github.com/nevillelyh/blob-tools/actions?query=workflow%3ACI)
[![GitHub license](https://img.shields.io/github/license/nevillelyh/blob-tools.svg)](./LICENSE)

## Introduction:

Light weight wrapper that adds [GCS](https://cloud.google.com/storage/) and
[S3](https://aws.amazon.com/s3/) support for CLIs of common file formats,
including
[avro-tools](https://mvnrepository.com/artifact/org.apache.avro/avro-tools),
[orc-tools](https://mvnrepository.com/artifact/org.apache.orc/orc-tools), and
[parquet-cli](https://mvnrepository.com/artifact/org.apache.parquet/parquet-cli).

## Build:

```
./make-binary.sh
./bin/avro-tools tojosn <file>
./bin/orc-tools data <file>
./bin/parquet-cli cat <file>
```
