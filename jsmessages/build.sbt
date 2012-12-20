name := "play-jsmessages"

version := "1.4"

organization := "com.github.julienrf"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

scalaVersion := "2.10.0-RC1"

libraryDependencies += "play" %% "play" % "2.1-RC1"

publishTo := Some(Resolver.file("Github Pages", Path.userHome / "backup" / "julienrf.github.com" / "repo" asFile))

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")
