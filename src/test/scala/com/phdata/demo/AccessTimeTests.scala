package com.phdata.demo

import org.scalatest._
import Matchers._
import org.joda.time.DateTime

class AccessTimeTests extends FunSuite {

  test("Should convert an Apache Common Log time to a joda DateTime ") {
    val logTime = "[25/May/2015:23:11:15 +0000]"
    val logRecord = ApacheAccessLog("ip", "-", "-", logTime, "GET", "200", "10", "foo", "bar")
    logRecord.accessTimestamp.isInstanceOf[DateTime] shouldBe true
  }

}
