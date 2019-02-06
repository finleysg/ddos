package com.phdata.demo

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

// T is a return type (Unit for jobs that return nothing)
trait Spark2Application[T] {

  def sparkConfig: Map[String, String]

  def withSparkSession(f: SparkSession => T): T = {
    val conf = new SparkConf()

    sparkConfig.foreach { case (k, v) => conf.setIfMissing(k, v) }

    val spark = SparkSession.builder
      .config(conf)
      .getOrCreate()

    f(spark)
  }
}
