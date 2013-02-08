# Play JsMessages library

This library allows to compute localized messages on client side, in Play 2 projects.

## Installation (using sbt)

You will need to add the following resolver:

```scala
resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo/"
```

Add a dependency on the following artifact:

```scala
libraryDependencies += "com.github.julienrf" %% "play-jsmessages" % "1.4"
```

Replace the version of the library with the one that targets your version of Play:

* `1.2.1` for play-2.0 ;
* `1.3` for play-2.0.4 ;
* `1.4` for play-2.1.0.

## Usage (Scala)

Add the following dependency to your Play project:

Use the `jsmessages.api.JsMessages` object to export your application localized messages on client side:

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

Use the `jsmessages.JsMessages` class to export your application localized messages on client side:

```java
    public static Result jsMessages() {
        return ok(JsMessages.generate("window.Messages")).as("application/javascript");
    }
```

It takes an optional namespace as parameter. You can use any valid JavaScript namespace, the generated function will then be assigned to this namespace. Use `null` if you only want to generate the function, without assigning it to a namespace.

Then you can compute messages on client side:

```javascript
  alert(Messages('greeting', 'World'));
```

You can also inline the definition of the messages in a HTML template:

```html
  @jsmessages.JsMessages("window.Messages")(play.api.Play.current, lang)
```

Finally, you can only export a subset of your i18n keys:

```java
    public static Result jsMessages() {
        return ok(JsMessages.subset("window.Messages",
            "error.required",
            "error.number"
        )).as("application/javascript");
    }
```
