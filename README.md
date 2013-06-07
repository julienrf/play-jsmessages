# Play JsMessages library

This library allows to compute localized messages on client-side, in Play 2 projects.

Take a look at the [Scala](/julienrf/play-jsmessages/tree/1.5.x/sample-scala) and
[Java](/julienrf/play-jsmessages/tree/1.5.x/sample-java) samples to see it in action.

## Installation (using sbt)

You will need to add the following resolver:

```scala
resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo-snapshots/"
```

Add a dependency on the following artifact:

```scala
libraryDependencies += "com.github.julienrf" %% "play-jsmessages" % "1.5.0-SNAPSHOT"
```

Replace the version of the library with the one that targets your version of Play:

* `1.2.1` for play-2.0 ;
* `1.3` for play-2.0.4 ;
* `1.4.2` for play-2.1.x ;
* >= `1.5` for play-2.1.x.

## Usage (Scala)

Create an instance of the `jsmessages.api.JsMessages` class to export your application localized messages on client-side:

```scala
  val jsMessages = {
    import play.api.Play.current

    val messages = new jsmessages.api.JsMessages

    Action { implicit request =>
      Ok(messages(Some("window.Messages"))).as(JAVASCRIPT)
    }
  }
```

The above code creates a Play action that returns a JavaScript resource defining a function `window.Messages` that computes
the localized messages of the current application.

The `JsMessage#apply` method takes an optional namespace as parameter (`Some("window.Messages")` in the example above).
You can use any valid JavaScript namespace, the generated function will then be assigned to this namespace. Use `None`
if you only want to generate the function, without assigning it to a namespace.

Alternatively, you can inline the JavaScript code in a HTML template:

```html
@(messages: jsmessages.api.JsMessages)(implicit lang: Lang)
<html>
  <head>
    @messages.html(Some("window.Messages"))
  </head>
  <body>
  ...
  </body>
</html>
```

Last but not least, you can export only a subset of your i18n keys:

```scala
  messages.subset(Some("window.Messages"))(
    "error.required",
    "error.number"
  )
```

See below for usage instructions in JavaScript.

## Usage (Java)

Create an instance of the `jsmessages.JsMessages` class to export your application localized messages on client-side:

```java
    import jsmessages.JsMessages;
    ...
    final static JsMessages messages = new JsMessages(play.Play.application());

    public static Result jsMessages() {
        return ok(messages.generate("window.Messages")).as("application/javascript");
    }
```

The above code creates a Play action that returns a JavaScript resource defining a function `window.Messages` that computes
the localized messages of the current application.

The `JsMessage#generate` method takes an optional namespace as parameter (`"window.Messages"` in the example above).
You can use any valid JavaScript namespace, the generated function will then be assigned to this namespace. Use `null`
if you only want to generate the function, without assigning it to a namespace.

You can also inline the definition of the messages in a HTML template:

```html
@(messages: jsmessages.JsMessages)
<html>
  <head>
    @messages.html("window.Messages")(lang)
  </head>
  <body>
  ...
  </body>
</html>
```

Finally, you can only export a subset of your i18n keys:

```java
    public static Result jsMessages() {
        return ok(messages.subset("window.Messages",
            "error.required",
            "error.number"
        )).as("application/javascript");
    }
```

## Usage (JavaScript)

After having generated a JavaScript function as explained above, you can compute messages on client-side:

```javascript
  alert(Messages('greeting', 'World'));
```

You can also provide alternative keys that will be used if the main key is not defined. Keys will be tried in order until
a defined key is found.

```javascript
  alert(Messages(['greeting', 'opening'], 'World'));
```

## Changelog

* v1.5.0
  - Fix the `subset` method of the Java API (thanks to Seppel Hardt) ;
  - Refactor the whole API in order to make it more extensible (thanks to Jacques Bachellerie).