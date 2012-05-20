name := "play-jsmessages"

version := "1.2.1"

organization := "com.github.julienrf"

libraryDependencies += "play" %% "play" % "2.0"

publishTo := Some(Resolver.file("Github Pages", Path.userHome / "Workspace" / "julienrf.github.com" / "repo" asFile))

javacOptions ++= Seq("-source", "1.6", "-target", "1.6", "-bootclasspath", "/usr/lib/jvm/java-1.6.0-openjdk-amd64/jre/lib/rt.jar")
