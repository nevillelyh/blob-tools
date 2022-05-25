Blob Tools
==========

[![Build Status](https://img.shields.io/github/workflow/status/nevillelyh/blob-tools/CI)](https://github.com/nevillelyh/blob-tools/actions?query=workflow%3ACI)
[![GitHub license](https://img.shields.io/github/license/nevillelyh/blob-tools.svg)](./LICENSE)

## Introduction:

Light weight wrapper that adds [GCS](https://cloud.google.com/storage/) and [S3](https://aws.amazon.com/s3/) support to common Hadoop tools, including [avro-tools](https://mvnrepository.com/artifact/org.apache.avro/avro-tools), [parquet-cli](https://mvnrepository.com/artifact/org.apache.parquet/parquet-cli), proto-tools for [Scio](https://github.com/spotify/scio)'s Protobuf in Avro file, and magnolify-tools for [Magnolify](https://github.com/spotify/magnolify) code generation, so that they can be used from regular workstations or laptops for local development.

## Build:

```
./make-binary.sh
./bin/avro-tools tojosn <file>
./bin/parquet-cli cat <file>
./bin/proto-cli cat <file>
./bin/magnolify-tools cat <file>
```
