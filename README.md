# Play JsMessages library

This library allows to compute localized messages on client side, in Play 2.0 projects.

## Usage

Add the following dependency to your Play project:

```scala
  val appDependencies = Seq(
    "com.github.julienrf" %% "play-jsmessages" % "1.1"
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

A template tag is also defined:

```html
  @jsMessages("Messages")
```

Last but not least, you can export only a subset of your i18n keys:

```scala
  JsMessages.subset("Messages")(
    "error.required",
    "error.number"
  )
```