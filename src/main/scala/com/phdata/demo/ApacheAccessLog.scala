package com.phdata.demo

case class ApacheAccessLog (clientIp: String,
                            clientIdentity: String,
                            remoteUser: String,
                            timestamp: String,
                            request: String,
                            httpStatusCode: String,
                            bytesSent: String,
                            referer: String,
                            userAgent: String)
