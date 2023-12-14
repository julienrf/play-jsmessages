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
    ),
    homepage := Some(url("https://github.com/julienrf/play-jsmessages")),
    licenses := List("MIT License" -> url("https://opensource.org/licenses/mit-license.php")),
    developers := List(
      Developer(
        "julienrf",
        "Julien Richard-Foy",
        "julien@richard-foy.fr",
        url("https://github.com/julienrf")
      )
    )
  )


// Settings for sample play-apps, containing the tests.
val sampleSettings = commonSettings ++ Seq(
  libraryDependencies ++= Seq(
    guice,
    "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
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
