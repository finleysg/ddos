package com.phdata.demo

import java.util

import org.apache.kafka.common.serialization.Deserializer
import org.joda.time.format.DateTimeFormat

import scala.util.matching.Regex

object ApacheAssessLogDeserializerWrapper {
  val deserializer = new ApacheAssessLogDeserializer
}

class ApacheAssessLogDeserializer extends Deserializer[ApacheAccessLog] {

  // regex courtesy of https://gist.github.com/earthquakesan/e35653927e4c67d88ad6d9dc32e3aa29
  val regex: Regex = "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+)\\s?(\\S+)?\\s?(\\S+)?\" (\\d{3}|-) (\\d+|-)\\s?\"?([^\"]*)\"?\\s?\"?([^\"]*)?\"?$".r

  override def close(): Unit = {
    // no-op
  }

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {
    // no-op
  }

  override def deserialize(topic: String, data: Array[Byte]): ApacheAccessLog = {
    data.toString match {
      case regex(
        ip,
        id,
        user,
        date,
        request,
        status,
        bytes,
        referer,
        agent
      ) => ApacheAccessLog(ip, id, user, accessTimestamp(date), request, status, bytes, referer, agent)
    }
  }

  // [25/May/2015:23:11:15 +0000]
  val pattern = "[dd/MMM/yyyy:HH:mm:ss Z]"

  def accessTimestamp(dateString: String): String = {
    val format = DateTimeFormat.forPattern(pattern)
    format.parseDateTime(dateString).toString()
  }
}
