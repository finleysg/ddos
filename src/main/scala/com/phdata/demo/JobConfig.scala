package com.phdata.demo

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._

/**
  * Represents environment-specific configuration. The case class
  * should mirror what you have in application.conf
  */
case class JobConfig(spark: Map[String, String], kafka: Map[String, String], logTopic: String, logByIpTopic: String)

object JobConfig {

  def apply(): JobConfig = {
    val env = "demo"
    apply(ConfigFactory.load, env.toLowerCase())
  }

  def apply(envOverride: String): JobConfig = {
    apply(ConfigFactory.load, envOverride)
  }

  def apply(applicationConfig: Config, env: String): JobConfig = {
    val config = applicationConfig.getConfig(env)

    new JobConfig(
      config.as[Map[String, String]]("spark"),
      config.as[Map[String, String]]("kafka"),
      config.as[String]("logTopic"),
      config.as[String]("logByIpTopic")
    )
  }
}
