name := "play-jsmessages"

version := "1.5.0-SNAPSHOT"

organization := "com.github.julienrf"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

scalaVersion := "2.10.0"

libraryDependencies += "play" %% "play" % "2.1.0"

publishTo <<= version {
  case v if v.trim.endsWith("SNAPSHOT") => Some(Resolver.file("Github Pages", Path.userHome / "sites" / "julienrf.github.com" / "repo-snapshots" asFile))
  case _ => Some(Resolver.file("Github Pages", Path.userHome / "sites" / "julienrf.github.com" / "repo" asFile))
}

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")
