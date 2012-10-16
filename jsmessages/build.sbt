name := "play-jsmessages"

version := "1.2.2-SNAPSHOT"

organization := "com.github.julienrf"

libraryDependencies += "play" %% "play" % "2.0.4"

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

publishTo := Some("NGT Play modules" at "http://nexus.dev.nextgentel.net/content/repositories/ngt-play-modules")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")