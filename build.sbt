lazy val root = (project in file(".")).
  settings(
    name := "ddos",
    version := "1.0",
    mainClass in Compile := Some("com.phdata.demo.RawLogDriver")
  )

resolvers += "Cloudera Repo" at "https://repository.cloudera.com/artifactory/cloudera-repos/"
resolvers += "iHeartRadio Repo" at "https://dl.bintray.com/iheartradio/maven/"

scalaVersion := "2.11.8"
val sparkVersion = "2.3.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-hive" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion
)

libraryDependencies ++= Seq(
  "com.iheart" %% "ficus" % "1.4.1",
  "org.apache.kafka" %% "kafka" % "1.1.1",
  "org.scalactic" %% "scalactic" % "2.2.6" % "test",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
javacOptions ++= Seq(
  "-source", "1.8", "-target", "1.8",
  "-Xlint")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

assemblyJarName in assembly := "ddos.jar"
test in assembly := {}
assemblyMergeStrategy in assembly :=
  {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case PathList("org", "apache", xs @ _*) => MergeStrategy.last
    case "application.conf" => MergeStrategy.concat
    case x =>
      val baseStrategy = (assemblyMergeStrategy in assembly).value
      baseStrategy(x)
  }
assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheUnzip = false)
