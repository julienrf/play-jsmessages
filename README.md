# Play JsMessages library [![Build Status](https://travis-ci.org/julienrf/play-jsmessages.png?branch=master)](https://travis-ci.org/julienrf/play-jsmessages)

This library allows you to compute localized messages on client-side, in Play projects.

Basically, play-jsmessages takes the i18n messages of your Play application, sends them to the client-side as a JSON object and defines a JavaScript function returning a message value from a given language and the message key and arguments.

Take a look at the [Scala](/sample-scala) and [Java](/sample-java) samples to see it in action.

## Installation (using sbt)

Add a dependency on the following artifact:

```scala
libraryDependencies += "org.julienrf" %% "play-jsmessages" % "4.0.0"
```

The current 4.0.0 version is compatible with Play 2.7 and Scala 2.11, 2.12 and 2.13.

Previous versions are available here:
 * [`3.0.0`](https://github.com/julienrf/play-jsmessages/tree/3.0.0) for play-2.6 ;
 * [`2.1.0`](https://github.com/julienrf/play-jsmessages/tree/2.1.0) for play-2.5 ;
 * [`2.0.0`](https://github.com/julienrf/play-jsmessages/tree/2.0.0) for play-2.4 ;
 * [`1.6.2`](https://github.com/julienrf/play-jsmessages/tree/1.6.2) for play-2.3 ;
 * [`1.6.1`](https://github.com/julienrf/play-jsmessages/tree/1.6.1) for play-2.2 ;
 * [`1.5.2`](https://github.com/julienrf/play-jsmessages/tree/1.5.2) for play-2.2 ;
 * [`1.5.0`](https://github.com/julienrf/play-jsmessages/tree/1.5.0) for play-2.1 ;
 * [`1.3`](https://github.com/julienrf/play-jsmessages/tree/403dc8d7248c965c827b70edeff55016ae274bef) for play-2.0 ;
 * [`1.2.1`](https://github.com/julienrf/play-jsmessages/tree/cc52b23dae9997b77da3cbccf6d22b60a557c2ee) for play-2.0.

## API Documentation

You can browse the online [scaladoc](https://www.javadoc.io/doc/org.julienrf/play-jsmessages_2.12).

## Quick start

### Select which messages you want to support on client-side

#### Get a `JsMessagesFactory`

The `JsMessagesFactory` class is the starting point: it allows you to select which messages of your application
you want to support on client-side. You can use all the messages of the application or just a subset of them.

The simplest way to get a `JsMessagesFactory` is _via_ dependency injection:

```scala
import jsmessages.JsMessagesFactory
import javax.inject.Inject

class Application @Inject() (jsMessagesFactory: JsMessagesFactory) extends Controller {

}
```

The equivalent Java code is the following:

```java
import jsmessages.JsMessagesFactory;
import javax.inject.Inject;

public class Application extends Controller {

  @Inject
  public Application(JsMessagesFactory jsMessagesFactory) {

  }

}
```

The constructor of the `JsMessagesFactory` itself needs to be injected a `play.api.i18n.MessagesApi` parameter. By default,
Play defines a binding that uses a `play.api.i18n.DefaultMessagesApi`, but you can disable this binding if you want to use something else.

For more control, you can also manually instantiate your `JsMessagesFactory`:

```scala
val jsMessagesFactory = new JsMessagesFactory(someMessagesApi)
```

The equivalent Java code is the following:

```java
JsMessagesFactory jsMessagesFactory = new JsMessagesFactory(someMessagesApi);
```

Note that, in Scala, you may want to use the `JsMessagesFactoryComponents` trait that provides a `jsMessagesFactory: JsMessagesFactory`
member using an abstract `messagesApi: MessagesApi` member.

#### Select which messages to use

You can either use all your i18n messages:

```scala
val jsMessages = jsMessagesFactory.all
```

Or, just an extensive subset:

```scala
val jsMessages = jsMessagesFactory.subset("error.required", "error.number")
```

Or, finally, an intensive subset:

```scala
val jsMessages = jsMessagesFactory.filtering(_.startsWith("error."))
```

The equivalent Java code of the above expressions are the following:

```java
import jsmessages.JsMessages;
import play.libs.Scala;
JsMessages jsMessages = jsMessagesFactory.all();
JsMessages jsMessages = jsMessagesFactory.subset(Scala.varargs("error.required", "error.number"));
JsMessages jsMessages = jsMessagesFactory.filtering(new scala.runtime.AbstractFunction1<String, Boolean>() {
  @Override
  public Boolean apply(String key) {
    return key.startsWith("error.");
  }
});
```

### Generate a JavaScript asset …

#### … for the client’s preferred language

Then, you typically want to define an action returning a JavaScript resource containing all the machinery to compute
localized messages from client-side, using the client’s preferred lang:

```scala
val messages = Action { implicit request =>
  Ok(jsMessages(Some("window.Messages")))
}
```

Note that for this to work the `apply` method of `jsMessages` needs to have an implicit `play.api.i18n.Messages` value. You can
get one by mixing the `play.api.i18n.I18nSupport` trait in your controller.

Or in Java:

```java
@Inject
private MessagesApi messagesApi;

public Result messages(Http.Request request) {
    return ok(jsMessages.apply(Scala.Option("window.Messages"), this.messagesApi.preferred(request)));
}
```

The above code creates a Play action that returns a JavaScript program containing the localized messages of the
application for the client language and defining a global function `window.Messages`. This function returns a localized
message given its key and its arguments:

```javascript
console.log(Messages('greeting', 'Julien')); // will print e.g. "Hello, Julien!" or "Bonjour Julien!"
```

The JavaScript function can also be supplied alternative keys that will be used if the main key is not defined. Keys will be tried in order until a defined key is found:

```javascript
alert(Messages(['greeting', 'saluting'], 'Julien'));
```

The JavaScript function stores the messages map in a `messages` property that is publicly accessible so you can update the messages without reloading the page:

```javascript
// Update a single message
Messages.messages['greeting'] = 'Hi there {0}!';
// Update all messages
Messages.messages = {
  'greeting': 'Hello, {0}!'
}
```

#### … for all the languages

Alternatively, you can use the `all` method to generate a JavaScript program containing all the messages of the application
instead of just those of the client’s current language, making it possible to switch the language from client-side
without reloading the page:

```scala
val messages = Action {
  Ok(jsMessages.all(Some("window.Messages")))
}
```

The equivalent Java code is the following:

```java
public Result messages() {
    return ok(jsMessages.all(Scala.Option("window.Messages")));
}
```

In this case, the generated JavaScript function takes an additional parameter corresponding
to the language to use:

```javascript
console.log(Messages('en', 'greeting', 'Julien')); // "Hello, Julien!"
console.log(Messages('fr', 'greeting', 'Julien')); // "Bonjour Julien!"
```

Moreover, the `messages` property of the JavaScript function is a map of languages indexing maps of messages.

The JavaScript function can also be partially applied to fix its first parameter:

```javascript
val messagesFr = Messages('fr'); // Use only the 'fr' messages
console.log(messagesFr('greeting', 'Julien')); // "Bonjour Julien!"
```

Note: if you pass `undefined` as the language parameter, it will use the default messages.

## Changelog
* 4.0.0
  - Play 2.7.x compatibility, cross-compiled for Scala 2.13.

* 3.0.0
  - Play 2.6.x compatibility. All tests moved to scalatest+play and dependency injection.

* 2.0.0
  - Play 2.4.x compatibility.

* 1.6.2
  - Play 2.3.x compatibility.

* 1.6.1
  - Fix crash when `undefined` is passed as the language parameter (thanks to Paul Dijou).

* 1.6.0
  - Big changes in the API ;
  - Make it possible to return messages of all languages (thanks to Paul Dijou) ;
  - Discard methods returning HTML, keep just JavaScript.

* 1.5.2
  - Export the `messages` property on client-side (thanks to Paul Dijou).

* 1.5.1
  - Play 2.2.x compatibility.

* 1.5.0
  - Fix the `subset` method of the Java API (thanks to Seppel Hardt) ;
  - Refactor the whole API in order to make it more extensible (thanks to Jacques Bachellerie).

## License

This content is released under the [MIT License](http://opensource.org/licenses/mit-license.php).
