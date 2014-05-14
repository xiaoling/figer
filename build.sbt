name := "figer"

version := "0"

scalaVersion := "2.10.1"

javaOptions += "-Xmx24G"

fork in run := true


val stanfordNlp = "edu.stanford.nlp" % "stanford-corenlp" % "1.3.0" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp"))

libraryDependencies ++= Seq(
  "commons-lang" % "commons-lang" % "2.4" ,
  stanfordNlp,
  "xom" % "xom" % "1.2.5",
  "de.jollyday" % "jollyday" % "0.4.7",
  "joda-time" % "joda-time" % "2.1",
  "net.sf.trove4j" % "trove4j" % "3.0.1",
  "com.google.protobuf" % "protobuf-java" % "2.4.1"
)

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
