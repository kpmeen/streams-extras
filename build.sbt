name := "streams-extras"
organization := "net.symbiotic"

licenses := Seq(
  "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")
)

pomExtra := (
  <url>https://github.com/kpmeen/streams-extras</url>
  <scm>
    <url>git@github.com:kpmeen/streams-extras.git</url>
    <connection>scm:git:git@github.com:kpmeen/streams-extras.git</connection>
  </scm>
  <developers>
    <developer>
      <id>kpmeen</id>
      <name>Knut Petter Meen</name>
      <url>http://scalytica.net</url>
    </developer>
  </developers>
)

scalaVersion := "2.12.1"
crossScalaVersions := Seq("2.12.1", "2.11.8")

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-nullary-override",
  "-Ywarn-numeric-widen",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

scalacOptions in Test ++= Seq("-Yrangepos")

publishMavenStyle := true
publishArtifact in Test := false

bintrayReleaseOnPublish in ThisBuild := false
bintrayPackageLabels := Seq("scala", "akka", "akka-streams")

lazy val akkaVersion = "2.5.0"
lazy val testVersion = "3.0.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"          % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j"          % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"         % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit"        % akkaVersion,
  "org.scalatest"     %% "scalatest"           % testVersion % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
