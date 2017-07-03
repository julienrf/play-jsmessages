parallelExecution in Global := false

val commonSettings = Seq(
  organization := "org.julienrf",
  version := "3.0.0",
  scalaVersion := "2.11.8"
)

lazy val homePage = settingKey[File]("Path to the project home page")

lazy val publishDoc = taskKey[Unit]("Publish the documentation")

lazy val jsmessages = project
  .settings(commonSettings: _*)
  .settings(
    name := "play-jsmessages",
    crossScalaVersions := Seq("2.11.8", "2.12.1"),
    libraryDependencies ++= Seq(
      ws,
      component("play")
    ),
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

lazy val sampleScala = Project("sample-scala", file("sample-scala"))
  .settings(commonSettings: _*).settings(
    routesGenerator := InjectedRoutesGenerator,
    libraryDependencies += specs2 % Test,
    resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
  ).enablePlugins(PlayScala)
  .dependsOn(jsmessages)

lazy val sampleJava = Project("sample-java", file("sample-java"))
  .settings(commonSettings: _*).settings(
    libraryDependencies += specs2 % Test,
    resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
  ).enablePlugins(PlayJava)
  .dependsOn(jsmessages)

lazy val playJsmessages = project.in(file("."))
  .settings(commonSettings: _*)
  .aggregate(jsmessages, sampleScala, sampleJava)
