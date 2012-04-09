name := "play-jsmessages"

version := "1.0"

organization := "com.github.julienrf"

libraryDependencies += "play" %% "play" % "2.0"

publishTo := Some(Resolver.file("Github Pages", Path.userHome / "Workspace" / "julienrf.github.com" / "repo" asFile))