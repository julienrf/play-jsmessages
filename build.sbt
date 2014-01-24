parallelExecution in Global := false

val commonSettings = Seq(
  organization := "com.github.julienrf",
  version := "1.5.2",
  scalaVersion := "2.10.0"
)

lazy val jsmessages = project
  .settings(commonSettings: _*)
  .settings(
    publishTo := Some(Resolver.file("Github pages", Path.userHome / "sites" / "julienrf.github.com" / (if (version.value.trim.endsWith("SNAPSHOT")) "repo-snapshots" else "repo") asFile)),
    javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
    libraryDependencies += "com.typesafe.play" %% "play" % "2.2.0",
    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
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
