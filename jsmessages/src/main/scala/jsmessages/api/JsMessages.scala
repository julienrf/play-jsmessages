package jsmessages.api

import play.api.Application
import play.api.i18n.Lang
import play.api.libs.json.{Writes, JsValue, Json}
import play.api.templates.JavaScript

/**
 * Generate a JavaScript function computing localized messages of a Play! application.
 *
 * Typical usage:
 *
 * {{{
 *   import play.api.mvc._
 *   import play.api.Play.current
 *   import jsmessages.api.JsMessages
 *
 *   object Application extends Controller {
 *     val jsMessages = new JsMessages
 *
 *     val messages = Action { implicit request =>
 *       Ok(jsMessages(Some("window.Messages")))
 *     }
 *   }
 * }}}
 *
 * Then on client-side:
 *
 * {{{
 *   console.log(Messages("greeting", "Julien")); // prints "Hello, Julien!"
 * }}}
 *
 * @param app Play! application to get messages from
 */
class JsMessages(implicit app: Application) {

  /**
   * All the messages of the application, as a map of (lang -> map(key -> message)).
   *
   * The default implementation uses the Play! application’s message files. Override this lazy val to supply
   * additional or other messages. As it is the case in Play!, JsMessages assumes that “default” messages are
   * indexed by the `"default"` and `"default.play"` language codes.
   */
  lazy val allMessagesData: Map[String, Map[String, String]] = play.api.i18n.Messages.messages

  // Message patterns have to escape quotes using double quotes, here we unescape them because we don’t support using quotes to escape format elements
  // TODO Also remove subformats
  private val allMessagesUnescaped: Map[String, Map[String, String]] =
    allMessagesData.mapValues(_.mapValues(_.replace("''", "'")))

  /**
   * Messages for each available lang of the application.
   *
   * The message corresponding to a given key is found by searching in the
   * following locations, in order: the language (e.g. in the `conf/messages.fr-FR` file), the language
   * country (e.g. `conf/messages.fr`), the application default messages (`conf/messages`) and the
   * Play! default messages.
   */
  lazy val allMessages: Map[String, Map[String, String]] = for ((lang, msgs) <- allMessagesUnescaped) yield {
    val maybeCountry = if (lang.contains("-")) Some(lang.split("-")(0)) else None
    lang -> (
      allMessagesUnescaped.get("default.play").getOrElse(Map.empty) ++
      allMessagesUnescaped.get("default").getOrElse(Map.empty) ++
      maybeCountry.flatMap(country => allMessagesUnescaped.get(country)).getOrElse(Map.empty) ++
      msgs
    )
  }

  /**
   * Same as `allMessages`, but as a JSON value.
   */
  final val allMessagesJson: JsValue = Json.toJson(allMessages)

  // Cache of all the messages map as a JSON object
  private val allMessagesCache: String = allMessagesJson.toString()

  // Per lang cache of the messages
  private val messagesCache: Map[String, String] = allMessages.mapValues(map => formatMap(map))

  /**
   * @param lang Language to retrieve messages for
   * @return The messages defined for the given language `lang`, as a map
   *         of (key -> message). The message corresponding to a given key is found by searching in the
   *         following locations, in order: the language (e.g. in the `conf/messages.fr-FR` file), the language
   *         country (e.g. `conf/messages.fr`), the application default messages (`conf/messages`) and the
   *         Play! default messages.
   */
  def messages(implicit lang: Lang): Map[String, String] = lookupLang(allMessages, lang)

  /**
   * @param lang Language to retrieve messages for
   * @return The JSON formatted string of the for the given language `lang`
   */
  private def messagesString(implicit lang: Lang): String = lookupLang(messagesCache, lang)

  /**
   * Generates a JavaScript function computing localized messages in the given implicit `Lang`.
   *
   * For example:
   *
   * {{{
   *   val messages = Action { implicit request =>
   *     Ok(jsMessages(Some("window.Messages")))
   *   }
   * }}}
   *
   * Then use it in your JavaScript code as follows:
   *
   * {{{
   *   alert(Messages('greeting', 'World'));
   * }}}
   *
   * Provided you have the following message in your `conf/messages` file:
   *
   * {{{
   * greeting=Hello {0}!
   * }}}
   *
   * Note: This implementation does not handle quotes escaping in patterns and subformats (see
   * http://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html)
   *
   * @param namespace Optional JavaScript namespace to use to put the function definition. If not set, this
   *                  function will just generate a function. Otherwise it will generate a function and assign
   *                  it to the given namespace. Note: you can set something like `Some("var Messages")` to use
   *                  a fresh variable.
   * @param lang Language to use. The message corresponding to a given key is found by searching in the
   *         following locations, in order: the language (e.g. in the `conf/messages.fr-FR` file), the language
   *         country (e.g. `conf/messages.fr`), the application default messages (`conf/messages`) and the
   *         Play! default messages.
   */
  def apply(namespace: Option[String] = None)(implicit lang: Lang): JavaScript = apply(namespace, messagesString)

  /**
   * Generates a JavaScript function computing localized messages in all the languages of the application.
   *
   * For example:
   *
   * {{{
   *   val messages = Action {
   *     Ok(jsMessages.all(Some("window.Messages")))
   *   }
   * }}}
   *
   * Then use it in your JavaScript code as follows:
   *
   * {{{
   *   alert(Messages('en', 'greeting', 'World'));
   * }}}
   *
   * Provided you have the following message in your `conf/messages` file:
   *
   * {{{
   * greeting=Hello {0}!
   * }}}
   *
   * Note that, given a message key, the JavaScript function will search the corresponding message in the
   * following locations, in order: the language (e.g. in the `conf/messages.fr-FR` file), the language
   * country (e.g. `conf/messages.fr`), the application default messages (`conf/messages`) and the
   * Play! default messages.
   *
   * Note: This implementation does not handle quotes escaping in patterns and subformats (see
   * http://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html)
   *
   * @param namespace Optional JavaScript namespace to use to put the function definition. If not set, this
   *                  function will just generate a function. Otherwise it will generate a function and
   *                  assign it to the given namespace. Note: you can set something like
   *                  `Some("var Messages")` to use a fresh variable.
   */
  def all(namespace: Option[String] = None): JavaScript = all(namespace, allMessagesCache)

  /**
   * Generates a JavaScript function computing localized messages for a given keys subset and language.
   *
   * Example:
   *
   * {{{
   *   jsMessages.subset(
   *     "error.required",
   *     "error.number"
   *   )(Some("window.Messages"))
   * }}}
   *
   * See documentation of the `apply` method for client-side instructions.
   */
  def subset(keys: String*): `Option[String] => JavaScript` = filter(keys.contains)

  /**
   * Generates a JavaScript function computing all messages for a given keys subset, for all languages.
   *
   * Example:
   *
   * {{{
   *   jsMessages.subsetAll(
   *     "error.required",
   *     "error.number"
   *   )(Some("window.MyMessages"))
   * }}}
   *
   * See documentation of the `all` method for client-side instructions.
   */
  def subsetAll(keys: String*): Option[String] => JavaScript = filterAll(keys.contains)

  /**
   * Generates a JavaScript function computing localized messages filtering keys based on a predicated.
   *
   * Example:
   *
   * {{{
   *   jsMessages.filter(_.startsWith("error.")(Some("window.Messages"))
   * }}}
   *
   * See documentation of the `apply` method for client-side instructions.
   */
  def filter(filter: String => Boolean): `Option[String] => JavaScript` = {
    val filteredMessages = allMessages.mapValues(_.filterKeys(filter))
    new `Option[String] => JavaScript` {
      def apply(namespace: Option[String])(implicit lang: Lang) =
        JsMessages.this.apply(namespace, formatMap(lookupLang(filteredMessages, lang)))
    }
  }

  /**
   * Generates a JavaScript function computing all messages filtering keys based on a predicated.
   *
   * Example:
   *
   * {{{
   *   jsMessages.filterAll(_.startsWith("error.")(Some("window.Messages"))
   * }}}
   *
   * See documentation of the `all` method for client-side instructions.
   */
  def filterAll(filter: String => Boolean): Option[String] => JavaScript = {
    val formattedMessages = formatMap(allMessagesUnescaped.mapValues(_.filterKeys(filter)))
    namespace => all(namespace, formattedMessages)
  }

  /**
   * @param namespace Optional namespace that will contain the generated function
   * @param messages Map of (key -> message) to use, as a JSON literal
   * @return a JavaScript function taking a key and eventual arguments and returning a formatted message
   */
  private def apply(namespace: Option[String], messages: String): JavaScript = {
    JavaScript(s""" #${namespace.map{_ + "="}.getOrElse("")}(function(u){function f(k){
          #var m;
          #if(typeof k==='object'){
            #for(var i=0,l=k.length;i<l&&f.messages[k[i]]===u;++i);
            #m=f.messages[k[i]]||k[0]
          #}else{
            #m=((f.messages[k]!==u)?f.messages[k]:k)
          #}
          #for(i=1;i<arguments.length;i++){
            #m=m.replace('{'+(i-1)+'}',arguments[i])
          #}
          #return m};
          #f.messages=$messages;
          #return f})()""".stripMargin('#'))
  }

  /*
   * @param namespace Optional JavaScript namespace to use to put the function definition. If not set, this function will
   *                  just return a literal function. Otherwise it will generate a function and assign it to the given namespace.
   *                  Note: you can set something like `Some("var Messages")` to use a fresh variable.
   * @param messages String correctly formatted as JSON corresponding the to Map of messages.
   * @return A JavaScript fragment defining a function computing all messages
   */
  private def all(namespace: Option[String], messages: String): JavaScript = {
    // g(key): given a lang, try to find a key among all possible messages,
    //              will try lang, lang.language, default and finally default.play
    // h(key,args...): return the formatted message retrieved from g(lang,key)
    // f(lang,key,args...): if only lang, return anonymous function always calling h by prefixing arguments with lang
    //                      else, just call h with current arguments
    JavaScript(s""" #${namespace.map{_ + "="}.getOrElse("")}(function(u){function f(l,k){
          #function g(k){
            #var r=f.messages[l][k];
            #if (r===u&&l.indexOf('-')>-1) {var lg=l.split('-')[0];r=f.messages[lg] && f.messages[lg][k];}
            #if (r===u) {r=f.messages['default'] && f.messages['default'][k];}
            #if (r===u) {r=f.messages['default.play'] && f.messages['default.play'][k];}
            #return r;
          #}
          #function h(k){
            #var m;
            #if(typeof k==='object'){
              #for(var i=0,le=k.length;i<le&&g(k[i])===u;++i);
              #m=g(k[i])||k[0];
            #}else{
              #m=g(k);
              #m=((m!==u)?m:k);
            #}
            #for(i=1,le=arguments.length;i<le;++i){
              #m=m.replace('{'+(i-1)+'}',arguments[i])
            #}
            #return m;
          #}
          #if(k===undefined){
            #return h;
          #}else{
            #return h.apply(u, Array.prototype.slice.call(arguments, 1));
          #}
        #}
        #f.messages=$messages;
        #return f})()""".stripMargin('#'))
  }

  private def formatMap[A : Writes](map: Map[String, A]): String = Json.toJson(map).toString()

  private def lookupLang[A](data: Map[String, A], lang: Lang): A =
    data.get(lang.code)
      .getOrElse(sys.error(s"Lang $lang is not supported by the application. Consider adding it to your 'application.langs' key in your 'conf/application.conf' file."))

}

/**
 * Fake `Option[String] => JavaScript` type that actually takes an additional implicit `Lang` parameter
 */
trait `Option[String] => JavaScript` {
  def apply(namespace: Option[String])(implicit lang: Lang): JavaScript
}