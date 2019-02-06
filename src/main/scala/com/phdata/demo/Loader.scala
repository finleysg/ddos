package com.phdata.demo

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.collection.JavaConversions._
import scala.io.Source

class Loader(config: JobConfig) {

  def LoadKafka(filepath: String): Unit = {
    val producer = new KafkaProducer[String, String](config.kafka)
    try {
      val metadata = Source.fromFile(filepath).getLines.map { line =>
        producer.send(new ProducerRecord[String, String](config.logTopic, line))
      }.toList

      metadata.foreach { metadata => metadata.get() }
    }
    finally {
      producer.close()
    }
  }
}

object Loader {
  def main(args: Array[String]): Unit = {
    try {
      if (args.length != 1) {
        throw new IllegalArgumentException("you must pass a full file path into the loader driver")
      }
      val instance = new Loader(JobConfig())
      instance.LoadKafka(args.head)
    } catch {
      case e: Exception =>
        // TODO: log fatal
        println(e)
    }
  }
}