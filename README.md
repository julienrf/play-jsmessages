# Play JsMessages library

This library allows to compute localized messages on client side, in Play 2.0 projects.

## Usage (Scala)

Add the following dependency to your Play project:

```scala
  val appDependencies = Seq(
    "com.github.julienrf" %% "play-jsmessages" % "1.2"
  )
  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo/"
  )
```

Use the `JsMessages` object to export your application localized messages on client side:

```scala
  def jsMessages = Action { implicit request =>
    Ok(JsMessages(Some("window.Messages"))).as(JAVASCRIPT)
  }
```

It takes an optional namespace as parameter. You can use any valid JavaScript namespace, the generated function will then be assigned to this namespace. Use `None` if you only want to generate the function, without assigning it to a namespace.

Then you can compute messages on client side:

```javascript
  alert(Messages('greeting', 'World'));
```

A template tag is also defined:

```html
  @jsMessages(Some("window.Messages"))
```

Last but not least, you can export only a subset of your i18n keys:

```scala
  JsMessages.subset(Some("window.Messages"))(
    "error.required",
    "error.number"
  )
```

## Usage (Java)

Add the following dependency to your Play project:

```scala
  val appDependencies = Seq(
    "com.github.julienrf" %% "play-jsmessages" % "1.2"
  )
  val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
    resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo/"
  )
```

Use the `JsMessages` object to export your application localized messages on client side:

```java
    public static Result jsMessages() {
        return ok(jsmessages.JsMessages.generate("window.Messages")).as("application/javascript");
    }
```

It takes an optional namespace as parameter. You can use any valid JavaScript namespace, the generated function will then be assigned to this namespace. Use `null` if you only want to generate the function, without assigning it to a namespace.

Then you can compute messages on client side:

```javascript
  alert(Messages('greeting', 'World'));
```

A template tag is also defined:

```html
  @jsmessages.JsMessages("window.Messages")(play.api.Play.current, lang)
```

Finally, you can only export a subset of your i18n keys:

```java
    public static Result jsMessages() {
        return ok(jsmessages.JsMessages.subset("window.Messages",
            "error.required",
            "error.number"
        )).as("application/javascript");
    }
```