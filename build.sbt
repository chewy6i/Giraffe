name := "Giraffe"
version := "1.0"
scalaVersion := "2.10.6"


libraryDependencies ++= {
    val sparkVersion = "1.6.2"
    val jsonVersion = "3.2.10"

    Seq("org.apache.spark" % "spark-streaming_2.10" % sparkVersion,
      "org.apache.spark" % "spark-core_2.10" % sparkVersion,
      "org.apache.spark" % "spark-sql_2.10" % sparkVersion,
      "org.apache.spark" % "spark-mllib_2.10" % sparkVersion,
      "org.apache.spark" % "spark-streaming-kafka_2.10" % sparkVersion,
      "org.apache.spark" % "spark-hive_2.10" % sparkVersion ,
      "org.apache.spark" % "spark-yarn_2.10" % sparkVersion,

      "org.slf4j" % "slf4j-api" % "1.7.21",

      "com.fasterxml.jackson.core" % "jackson-core" % "2.5.1",
      "org.json4s" % "json4s-jackson_2.10" % jsonVersion,
      "org.json4s" % "json4s-native_2.10" % jsonVersion,
      "org.json4s" % "json4s-ast_2.10" % jsonVersion

    )
}
