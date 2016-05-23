import AssemblyKeys._

name := "cdraggregation"

version := "1.0"


scalaVersion := "2.11.6"


libraryDependencies += "org.apache.spark" %% "spark-core" % "1.2.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.2.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.2.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "1.2.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-hive" % "1.2.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-streaming-twitter" % "1.2.0"

libraryDependencies += "com.google.code.gson" % "gson" % "2.3"

libraryDependencies += "org.twitter4j" % "twitter4j-core" % "3.0.3"

libraryDependencies += "commons-cli" % "commons-cli" % "1.2"

libraryDependencies += "net.sf.opencsv" % "opencsv" % "2.3"

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

assemblySettings

mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}
