# Play JsMessages library

This library allows to compute localized messages on client side, in Play 2.0 projects.

## Usage

Add the following dependency to your Play project:

```scala
  val appDependenies = Seq(
    "com.github.julienrf" %% "play-jsmessages" % "1.0"
  )
  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo/"
  )
```

Use the `JsMessages` object to export your application localized messages on client side:

```scala
  def jsMessages = Action { implicit request =>
    Ok(JsMessages("Messages")).as(JAVASCRIPT)
  }
```

Then you can compute messages on client side:

```javascript
  alert(Messages('greeting', 'World'));
```