import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "sample-java"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "com.github.julienrf" %% "play-jsmessages" % "1.2.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo/"
    )

}
