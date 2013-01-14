import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "sample-scala"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "com.github.julienrf" %% "play-jsmessages" % "1.4-SNAPSHOT"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
        resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo-snapshots/"
    )

}
