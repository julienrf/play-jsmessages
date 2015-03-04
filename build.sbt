parallelExecution in Global := false

val commonSettings = Seq(
  organization := "org.julienrf",
  version := "1.6.2",
  javacOptions in Compile ++= Seq("-source", "1.6", "-target", "1.6"),
  scalaVersion := "2.11.1"
)

lazy val homePage = settingKey[File]("Path to the project home page")

lazy val publishDoc = taskKey[Unit]("Publish the documentation")

lazy val jsmessages = project
  .settings(commonSettings: _*)
  .settings(
    name := "play-jsmessages",
    crossScalaVersions := Seq("2.10.4", "2.11.1"),
    libraryDependencies ++= Seq(
      ws,
      "com.typesafe.play" %% "play" % "2.3.0"
    ),
    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org"
      if (isSnapshot.value) Some("snapshots" at s"$nexus/content/repositories/snapshots")
      else Some("releases" at s"$nexus/service/local/staging/deploy/maven2")
    },
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
    ),
    useGpg := true,
    homePage := Path.userHome / "sites" / "julienrf.github.com",
    publishDoc := {
      IO.copyDirectory((doc in Compile).value, homePage.value / "play-jsmessages" / version.value / "api")
    }
  )

lazy val sampleScala = Project("sample-scala", file("sample-scala/code-sample"))
  .settings(commonSettings: _*)
  .enablePlugins(PlayScala)
  .dependsOn(jsmessages)

lazy val samplePlayingJsmessages = Project("playing-jsmessages", file("sample-scala/playing-jsmessages"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
     "org.webjars" %% "webjars-play" % "2.3.0-2",
     "org.webjars" % "bootstrap" % "3.1.1-2",
     "org.webjars" % "bootswatch-darkly" % "3.3.1+2",
     "org.webjars" % "html5shiv" % "3.7.0",
     "org.webjars" % "respond" % "1.4.2"
    )
  )
  .enablePlugins(PlayScala)
  .dependsOn(jsmessages)


lazy val sampleJava = Project("sample-java", file("sample-java"))
  .settings(commonSettings: _*)
  .enablePlugins(PlayJava)
  .dependsOn(jsmessages)

lazy val playJsmessages = project.in(file("."))
  .settings(commonSettings: _*)
  .aggregate(jsmessages, sampleScala, sampleJava)
