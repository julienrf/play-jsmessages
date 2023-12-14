parallelExecution in Global := false

val commonSettings = Seq(
  organization := "org.julienrf",
  scalaVersion := "2.13.12"
)

lazy val jsmessages = project
  .settings(commonSettings: _*)
  .settings(
    name := "play-jsmessages",
    crossScalaVersions := Seq("2.13.12", "3.3.1"),
    libraryDependencies ++= Seq(
      component("play"),
      "org.scala-lang.modules" %% "scala-collection-compat" % "2.8.1"
    ),
    pomExtra := (
      <url>http://github.com/julienrf/play-jsmessages</url>
      <licenses>
        <license>
          <name>MIT License</name>
          <url>http://opensource.org/licenses/mit-license.php</url>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:julienrf/play-jsmessages.git</url>
        <connection>scm:git:git@github.com:julienrf/play-jsmessages.git</connection>
      </scm>
      <developers>
        <developer>
          <id>julienrf</id>
          <name>Julien Richard-Foy</name>
          <url>http://julien.richard-foy.fr</url>
        </developer>
      </developers>
    )
  )


// Settings for sample play-apps, containing the tests.
val sampleSettings = commonSettings ++ Seq(
  libraryDependencies ++= Seq(
    guice,
    "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.2.5" % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0" % Test
  ),
  publish / skip := true,
)

lazy val sampleScala = Project("sample-scala", file("sample-scala"))
  .settings(sampleSettings: _*)
  .enablePlugins(PlayScala)
  .dependsOn(jsmessages)

lazy val sampleJava = Project("sample-java", file("sample-java"))
  .settings(sampleSettings: _*)
  .enablePlugins(PlayJava)
  .dependsOn(jsmessages)

lazy val playJsmessages = project.in(file("."))
  .settings(commonSettings: _*)
  .settings(publish / skip := true)
  .aggregate(jsmessages, sampleScala, sampleJava)
