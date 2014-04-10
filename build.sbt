parallelExecution in Global := false

val commonSettings = Seq(
  organization := "org.julienrf",
  version := "1.6.1-SNAPSHOT",
  scalaVersion := "2.10.0"
)

lazy val homePage = settingKey[File]("Path to the project home page")

lazy val publishDoc = taskKey[Unit]("Publish the documentation")

lazy val jsmessages = project
  .settings(commonSettings: _*)
  .settings(
    name := "play-jsmessages",
    javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
    libraryDependencies += "com.typesafe.play" %% "play" % "2.2.0",
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

lazy val sampleScala = Project("sample-scala", file("sample-scala"))
  .settings(commonSettings: _*)
  .settings(play.Project.playScalaSettings: _*)
  .dependsOn(jsmessages)

lazy val sampleJava = Project("sample-java", file("sample-java"))
  .settings(commonSettings: _*)
  .settings(play.Project.playJavaSettings: _*)
  .dependsOn(jsmessages)

lazy val playJsmessages = project.in(file("."))
  .settings(commonSettings: _*)
  .aggregate(jsmessages, sampleScala, sampleJava)
