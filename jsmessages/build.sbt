name := "play-jsmessages"

version := "1.5.1"

organization := "com.github.julienrf"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

scalaVersion := "2.10.2"

libraryDependencies += "com.typesafe.play" %% "play" % "[2.2.0,2.3["

publishTo <<= version {
  case v if v.trim.endsWith("SNAPSHOT") => Some(Resolver.file("Github Pages", Path.userHome / "sites" / "julienrf.github.com" / "repo-snapshots" asFile))
  case _ => Some(Resolver.file("Github Pages", Path.userHome / "sites" / "julienrf.github.com" / "repo" asFile))
}

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")
