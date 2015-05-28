package jsmessages

import play.api.i18n.{Messages, Lang}
import play.api.libs.json.{JsValue, Json, Writes}
import play.twirl.api.JavaScript

/**
 * Generate a JavaScript function computing localized messages of a Play application.
 *
 * Typical usage (from within a Play controller):
 *
 * {{{
 *   val jsMessages: JsMessages = ???
 *
 *   val messages = Action { implicit request =>
 *     Ok(jsMessages(Some("window.Messages")))
 *   }
 * }}}
 *
 * Then on client-side:
 *
 * {{{
 *   console.log(Messages("greeting", "Julien")); // prints "Hello, Julien!"
 * }}}
 *
 * See [[JsMessagesFactory]] to know how to get a `JsMessages` instance.
 *
 * @param allMessagesData All the messages of the application, as a map of (lang -> map(key -> message pattern)). As it
 *                        is the case in Play, JsMessages assumes that “default” messages are indexed by the `"default"`
 *                        and `"default.play"` language codes.
 */
class JsMessages(allMessagesData: Map[String, Map[String, String]]) {

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
   * Play default messages.
   */
  lazy val allMessages: Map[String, Map[String, String]] = for ((lang, msgs) <- allMessagesUnescaped) yield {
    lang match {
      // Do not merge with "default" if its "default.play"
      case "default.play" => lang -> allMessagesUnescaped.getOrElse("default.play", Map.empty)
      case _ => lang -> (
        allMessagesUnescaped.getOrElse("default.play", Map.empty) ++
        allMessagesUnescaped.getOrElse("default", Map.empty) ++
        extractCountry(lang).flatMap(country => allMessagesUnescaped.get(country)).getOrElse(Map.empty) ++
        msgs
      )
    }
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
   * @param messages Messages instance containing the lang to retrieve messages for
   * @return The messages defined for the given language `lang`, as a map
   *         of (key -> message). The message corresponding to a given key is found by searching in the
   *         following locations, in order: the language (e.g. in the `conf/messages.fr-FR` file), the language
   *         country (e.g. `conf/messages.fr`), the application default messages (`conf/messages`) and the
   *         Play default messages.
   */
  def messages(implicit messages: Messages): Map[String, String] = lookupLang(allMessages, messages)

  /**
   * @param messages Messages instance containing the lang to retrieve messages for
   * @return The JSON formatted string of the for the given language `lang`. This is strictly equivalent to
   *         `Json.toJson(jsMessages.messages).toString`, but may be faster due to the use of caching.
   */
  def messagesString(implicit messages: Messages): String = lookupLang(messagesCache, messages)

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
   * @param messages Messages instance defining the language to use. The message corresponding to a given key is found by searching in the
   *         following locations, in order: the language (e.g. in the `conf/messages.fr-FR` file), the language
   *         country (e.g. `conf/messages.fr`), the application default messages (`conf/messages`) and the
   *         Play default messages.
   */
  def apply(namespace: Option[String] = None)(implicit messages: Messages): JavaScript = apply(namespace, messagesString)

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
   * Play default messages.
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
          #function g(kg){
            #var r=f.messages[l] && f.messages[l][kg];
            #if (r===u&&l&&l.indexOf('-')>-1) {var lg=l.split('-')[0];r=f.messages[lg] && f.messages[lg][kg];}
            #if (r===u) {r=f.messages['default'] && f.messages['default'][kg];}
            #if (r===u) {r=f.messages['default.play'] && f.messages['default.play'][kg];}
            #return r;
          #}
          #function h(kh){
            #var m;
            #if(typeof kh==='object'){
              #for(var i=0,le=kh.length;i<le&&g(kh[i])===u;++i);
              #m=g(kh[i])||kh[0];
            #}else{
              #m=g(kh);
              #m=((m!==u)?m:kh);
            #}
            #for(i=1,le=arguments.length;i<le;++i){
              #m=m.replace('{'+(i-1)+'}',arguments[i])
            #}
            #return m;
          #}
          #if(k===undefined){
            #return h;
          #}else{
            #return h.apply({}, Array.prototype.slice.call(arguments, 1));
          #}
        #}
        #f.messages=$messages;
        #return f})()""".stripMargin('#'))
  }

  private def formatMap[A : Writes](map: Map[String, A]): String = Json.toJson(map).toString()

  private def extractCountry(lang: String): Option[String] = if (lang.contains("-")) Some(lang.split("-")(0)) else None

  private def lookupLang[A](data: Map[String, A], messages: Messages): A = {
    val lang = messages.lang
    // Try to get the messages for the lang
    data.get(lang.code)
      // If none, try to get it from its country
      .orElse(extractCountry(lang.code).flatMap(country => data.get(country)))
      // If none, fallback to default
      .orElse(data.get("default"))
      // If none, screw that, crash the system! It's your fault for no having a default.
      .getOrElse(sys.error(s"Lang $lang is not supported by the application. Consider adding it to your 'application.langs' key in your 'conf/application.conf' file or at least provide a default messages file."))
  }

}
