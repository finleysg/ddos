package com.phdata.demo

import org.apache.spark.sql.functions._

class RawLogDriver(config: JobConfig)
  extends Spark2Application[Unit] {

  override def sparkConfig: Map[String, String] = config.spark

  def Run(): Unit = {
    withSparkSession { spark =>

      import spark.implicits._

      spark.udf.register("deserialize", (topic: String, bytes: Array[Byte]) =>
        ApacheAssessLogDeserializerWrapper.deserializer.deserialize(topic, bytes)
      )

      val logs = spark.readStream
        .format("org.apache.spark.sql.kafka010.KafkaSourceProvider")
        .option("kafka.bootstrap.servers", config.kafka.getOrElse("bootstrap.servers", "error"))
        .option("startingOffsets", "earliest")
        .option("subscribe", config.logTopic)
        .load()
        .selectExpr(s"""deserialize("${config.logTopic}", value) AS record""")

      val ipCounts = logs
        .groupBy(
          window($"record.timestamp", "15 seconds", "10 seconds"),
          $"record.clientIp"
        ).count()

      val query = ipCounts.writeStream
        .outputMode("complete")
        .format("org.apache.spark.sql.kafka010.KafkaSourceProvider")
        .option("kafka.bootstrap.servers", config.kafka.getOrElse("bootstrap.servers", "error"))
        .option("topic", config.logByIpTopic)
        .start()

      query.awaitTermination()
    }
  }
}

object RawLogDriver {
  def main(args: Array[String]): Unit = {
    try {
      val instance = new RawLogDriver(JobConfig())
      instance.Run()
    } catch {
      case e: Exception =>
        // TODO: log fatal
        println(e)
    }
  }
}
