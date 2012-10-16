import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "sample-scala"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "com.github.julienrf" %% "play-jsmessages" % "1.2.2-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
        resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo/"
    )

}
