name := "play-jsmessages"

version := "1.3"

organization := "com.github.julienrf"

scalaVersion := "2.9.1"

libraryDependencies += "play" %% "play" % "2.0.4"

publishTo := Some(Resolver.file("Github Pages", Path.userHome / "backup" / "julienrf.github.com" / "repo" asFile))

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")
