organization := "me.lyh"
name := "blob-tools"

val awsVersion = "1.12.379"
val gcsVersion = "hadoop3-2.2.10"
val hadoopVersion = "3.3.4"
val avroVersion = "1.11.1"
val orcVersion = "1.8.1"
val parquetVersion = "1.12.3"

val commonSettings = assemblySettings ++ Seq(
  scalaVersion := "2.13.10",
  autoScalaLibrary := false,
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
)

lazy val root = project
  .in(file("."))
  .aggregate(
    avroTools,
    orcTools,
    parquetCli
  )

lazy val shared = project
  .in(file("shared"))
  .settings(commonSettings)

lazy val avroTools = project
  .in(file("avro-tools"))
  .settings(commonSettings)
  .settings(
    assembly / mainClass := Some("org.apache.avro.tool.Main"),
    assembly / assemblyJarName := s"avro-tools.jar",
    libraryDependencies ++= Seq(
      "org.apache.avro" % "avro-tools" % avroVersion,
      "org.apache.hadoop" % "hadoop-aws" % hadoopVersion,
      "org.apache.hadoop" % "hadoop-common" % hadoopVersion,
      "org.apache.hadoop" % "hadoop-client" % hadoopVersion,
      "com.amazonaws" % "aws-java-sdk-s3" % awsVersion,
      "com.google.cloud.bigdataoss" % "gcs-connector" % gcsVersion
    )
  )
  .dependsOn(shared)

lazy val orcTools = project
  .in(file("orc-tools"))
  .settings(commonSettings)
  .settings(
    assembly / mainClass := Some("org.apache.orc.tools.Driver"),
    assembly / assemblyJarName := s"orc-tools.jar",
    libraryDependencies ++= Seq(
      "org.apache.hadoop" % "hadoop-aws" % hadoopVersion,
      "org.apache.hadoop" % "hadoop-common" % hadoopVersion,
      "org.apache.hadoop" % "hadoop-client" % hadoopVersion,
      "org.apache.orc" % "orc-tools" % orcVersion,
      "com.amazonaws" % "aws-java-sdk-s3" % awsVersion,
      "com.google.cloud.bigdataoss" % "gcs-connector" % gcsVersion
    )
  )
  .dependsOn(shared)

lazy val parquetCli = project
  .in(file("parquet-cli"))
  .settings(commonSettings)
  .settings(
    assembly / mainClass := Some("org.apache.parquet.cli.Main"),
    assembly / assemblyJarName := s"parquet-cli.jar",
    libraryDependencies ++= Seq(
      "org.apache.parquet" % "parquet-cli" % parquetVersion,
      "org.apache.hadoop" % "hadoop-aws" % hadoopVersion,
      "org.apache.hadoop" % "hadoop-common" % hadoopVersion,
      "org.apache.hadoop" % "hadoop-client" % hadoopVersion,
      "com.amazonaws" % "aws-java-sdk-s3" % awsVersion,
      "com.google.cloud.bigdataoss" % "gcs-connector" % gcsVersion
    )
  )
  .dependsOn(shared)

lazy val assemblySettings = Seq(
  assembly / assemblyMergeStrategy ~= (old => {
    // avro-tools is a fat jar which includes old Guava & Hadoop classes
    case PathList("com", "google", "common", _*) =>
      jarFilter("guava")(_.organization != "org.apache.avro")
    case PathList("org", "apache", "hadoop", _*) =>
      jarFilter("hadoop")(_.organization != "org.apache.avro")
    // avro-tools reads NOTICE for header
    case PathList("META-INF", "NOTICE") =>
      jarFilter("tools")(c => c.name == "avro-tools" || c.name == "orc-tools" || c.name == "parquet-cli")
    // avoid protobuf-lite
    case PathList("com", "google", "protobuf", _*) =>
      jarFilter("protobuf")(c => c.organization == "com.google.protobuf" && c.name == "protobuf-java")

    case PathList("META-INF", "services", "org.apache.hadoop.fs.FileSystem") =>
      MergeStrategy.filterDistinctLines
    case s if s.endsWith(".properties")                => MergeStrategy.filterDistinctLines
    case s if s.endsWith("pom.xml")                    => MergeStrategy.last
    case s if s.endsWith(".class")                     => MergeStrategy.last
    case s if s.endsWith("libjansi.jnilib")            => MergeStrategy.last
    case s if s.endsWith("jansi.dll")                  => MergeStrategy.rename
    case s if s.endsWith("libjansi.so")                => MergeStrategy.rename
    case s if s.endsWith("libsnappyjava.jnilib")       => MergeStrategy.last
    case s if s.endsWith("libsnappyjava.so")           => MergeStrategy.last
    case s if s.endsWith("snappyjava_snappy.dll")      => MergeStrategy.last
    case s if s.endsWith(".dtd")                       => MergeStrategy.rename
    case s if s.endsWith(".xsd")                       => MergeStrategy.rename
    case PathList("META-INF", "LICENSE")               => MergeStrategy.discard
    case PathList("META-INF", "MANIFEST.MF")           => MergeStrategy.discard
    case PathList("META-INF", "INDEX.LIST")            => MergeStrategy.discard
    case PathList("META-INF", s) if s.endsWith(".DSA") => MergeStrategy.discard
    case PathList("META-INF", s) if s.endsWith(".RSA") => MergeStrategy.discard
    case PathList("META-INF", s) if s.endsWith(".SF")  => MergeStrategy.discard
    case _                                             => MergeStrategy.last
  })
)

import sbtassembly.MergeStrategy

def jarFilter(name: String)(f: Assembly.ModuleCoordinate => Boolean): MergeStrategy =
  CustomMergeStrategy(name) { conflicts =>
    val r = conflicts
      .filter(_.module.exists(f))
      .map(d => JarEntry(d.target, d.stream))
    Right(r)
  }
