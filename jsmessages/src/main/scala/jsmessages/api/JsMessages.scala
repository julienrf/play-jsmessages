package jsmessages.api

import play.api.i18n._
import play.api.Application
import play.api.templates.Html
import org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript

/**
 * Can generate a JavaScript function computing localized messages of a Play application
 * @param app Play application to use
 */
class JsMessages(implicit app: Application) {

  /**
   * The application’s messages to use by default, as a map of (key -> message)
   */
  val defaultMessages: Map[String, String] = escapeMap(Messages.messages.get("default").getOrElse(Map.empty))

  /**
   * Messages used by Play Framework
   */
  val defaultPlayMessages: Map[String, String] =  escapeMap(Messages.messages.get("default.play").getOrElse(Map.empty))

  /**
   * The messages defined in the given Play application `app`, for all languages, as a map of (lang -> map(key -> message))
   * with addition of default messages and Play Framework messages
   */
  val allMessages: Map[String, Map[String, String]] =
    Messages.messages.mapValues(v => escapeMap(v)) ++
    Map("default" -> defaultMessages, "default.play" -> defaultPlayMessages)

  /**
   *  Nearly JSON formated string of allMessages.
   */
  val allMessagesString: String = formatAllMap(allMessages)

  /**
   * Cache to memorize computed messages for each available lang
   * Computation consists of merging default messages, default Play messages,
   * messages for the language of the lang and messages of the lang itself.
   */
  val messagesCache: Map[String, Map[String, String]] = allMessages.map { kv =>
    val language = if (kv._1.contains("-")) { Some(kv._1.split("-")(0)) } else { None }

    kv._1 -> (defaultMessages ++
    defaultPlayMessages ++
    language.flatMap(lang => allMessages.get(lang)).getOrElse(Map.empty) ++
    kv._2)
  }

  /**
   *  Cache to memorize the nearly JSON formated string of each lang messages.
   */
  val messagesStringCache: Map[String, String] = messagesCache.mapValues(v => formatMap(v))

  /**
   * @param lang Language to retrieve messages for
   * @return The messages defined in the given Play application `app`, for the given language `lang`, as a map of (key -> message)
   */
  def messages(implicit lang: Lang): Map[String, String] = messagesCache.get(lang.code).getOrElse(Map.empty)

  /**
   * @param lang Language to retrieve messages for
   * @return The nearly JSON formated string of messages defined in the given Play application `app`,
   * for the given language `lang`, as a map of (key -> message)
   */
  def messagesString(implicit lang: Lang): String = messagesStringCache.get(lang.code).getOrElse("")

  /**
   * Generates a JavaScript function computing localized messages.
   *
   * For example:
   *
   * {{{
   *   def jsMessages = Action { implicit request =>
   *     Ok(jsMessages(Some("window.MyMessages"))).as(JAVASCRIPT)
   *   }
   * }}}
   *
   * Then use it in your JavaScript code as follows:
   *
   * {{{
   *   alert(MyMessages('greeting', 'World'));
   * }}}
   *
   * Provided you have the following message in your conf/messages file:
   *
   * {{{
   * greeting=Hello {0}!
   * }}}
   *
   * Note: This implementation does not handle quotes escaping in patterns (see http://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html)
   *
   * @param namespace Optional JavaScript namespace to use to put the function definition. If not set this function will
   * just generate a function. Otherwise it will generate a function and assign it to the given namespace. Note: you can
   * set something like `Some("var Messages")` to use a fresh variable.
   */
  def apply(namespace: Option[String] = None)(implicit lang: Lang): String = apply(namespace, messagesString)

  /**
   * Generates a JavaScript function computing all messages.
   *
   * For example:
   *
   * {{{
   *   def jsMessages = Action { implicit request =>
   *     Ok(jsMessages.all(Some("window.MyMessages"))).as(JAVASCRIPT)
   *   }
   * }}}
   *
   * Then use it in your JavaScript code as follows:
   *
   * {{{
   *   alert(MyMessages('en', 'greeting', 'World'));
   * }}}
   *
   * Provided you have the following message in your conf/messages file:
   *
   * {{{
   * greeting=Hello {0}!
   * }}}
   *
   * Note: This implementation does not handle quotes escaping in patterns (see http://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html)
   *
   * @param namespace Optional JavaScript namespace to use to put the function definition. If not set this function will
   * just generate a function. Otherwise it will generate a function and assign it to the given namespace. Note: you can
   * set something like `Some("var Messages")` to use a fresh variable.
   */
  def all(namespace: Option[String] = None): String = all(namespace, allMessagesString)

  /**
   * Generates a JavaScript function computing localized messages for a given keys subset.
   *
   * Example:
   *
   * {{{
   *   jsMessages.subset(Some("window.MyMessages"))(
   *     "error.required",
   *     "error.number"
   *   )
   * }}}
   */
  def subset(namespace: Option[String] = None)(keys: String*)(implicit lang: Lang): String =
     apply(namespace, subsetMap(messages, keys: _*))

  /**
   * Generates a JavaScript function computing all messages for a given keys subset.
   *
   * Example:
   *
   * {{{
   *   jsMessages.allSubset(Some("window.MyMessages"))(
   *     "error.required",
   *     "error.number"
   *   )
   * }}}
   */
  def allSubset(namespace: Option[String] = None)(keys: String*): String =
    all(namespace, allMessages.mapValues(m => subsetMap(m, keys: _*)))

  /**
   * @param namespace Optional JavaScript namespace to use to put the function definition. If not set, this function will
   *                  just return a literal function. Otherwise it will
   * @param messages Map of (key -> message) to use
   * @return A JavaScript fragment defining a function computing localized messages
   */
  def apply(namespace: Option[String], messages: Map[String, String]): String =
    apply(namespace, formatMap(messages))


  def apply(namespace: Option[String], messages: String): String = {
    """ #%s(function(u){function f(k){
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
          #f.messages={%s};
          #return f})()""".stripMargin('#').format(
           namespace.map{_ + "="}.getOrElse(""),
           messages
    )
  }

  /**
   * @param namespace Optional JavaScript namespace to use to put the function definition. If not set, this function will
   *                  just return a literal function. Otherwise it will
   * @param messages Map of (lang -> Map of (key -> message)) to use
   * @return A JavaScript fragment defining a function computing all messages
   */
  def all(namespace: Option[String], messages: Map[String, Map[String, String]]): String =
    all(namespace, formatAllMap(messages))

  def all(namespace: Option[String], messages: String): String = {
    // g(lang,key): given a lang, try to find a key among all possible messages,
    //              will try lang, lang.language, default and finally default.play
    // h(lang,key,args...): return the formatted message retrieved from g(lang,key)
    // f(lang,key,args...): if only lang, return anonymous function always calling h by prefixing arguments with lang
    //                      else, just call h with current arguments
    """ #%s(function(u){function f(l,k){
          #function g(l,k){
            #var r=f.messages[l][k];
            #if (r===u&&l.indexOf('-')>-1) { r=f.messages[l.split('-')[0]][k];}
            #if (r===u) {r=f.messages['default'][k];}
            #if (r===u) {r=f.messages['default.play'][k];}
            #return r;
          #}
          #function h(l,k){
            #var m;
            #if(typeof k==='object'){
              #for(var i=0,le=k.length;i<le&&g(l,k[i])===u;++i);
              #m=g(l,k[i])||k[0];
            #}else{
              #m=g(l,k);
              #m=((m!==u)?m:k);
            #}
            #for(i=2,le=arguments.length;i<le;++i){
              #m=m.replace('{'+(i-2)+'}',arguments[i])
            #}
            #return m;
          #}
          #if(k===undefined){
            #return function() {Array.prototype.splice.call(arguments, 0, 0, l); return h.apply(u, arguments);};
          #}else{
            #return h.apply(u, arguments);
          #}
        #}
        #f.messages={%s};
        #return f})()""".stripMargin('#').format(
        namespace.map{_ + "="}.getOrElse(""),
        messages)
  }

  /**
   * Generates a JavaScript function computing localized messages.
   *
   * For example:
   *
   * {{{
   *   @jsMessages.html(Some("window.MyMessages"))
   * }}}
   *
   * Then use it in your JavaScript code as follows:
   *
   * {{{
   *   alert(MyMessages('greeting', 'World'));
   * }}}
   *
   * Provided you have the following message in your conf/messages file:
   *
   * {{{
   * greeting=Hello {0}!
   * }}}
   */
  def html(namespace: Option[String] = None)(implicit lang: Lang) =
    script(apply(namespace))

  /**
   * Generates a JavaScript function computing all messages.
   *
   * For example:
   *
   * {{{
   *   @jsMessages.allHtml(Some("window.MyMessages"))
   * }}}
   *
   * Then use it in your JavaScript code as follows:
   *
   * {{{
   *   alert(MyMessages('en', 'greeting', 'World'));
   * }}}
   *
   * Provided you have the following message in your conf/messages file:
   *
   * {{{
   * greeting=Hello {0}!
   * }}}
   */
  def allHtml(namespace: Option[String] = None) =
    script(all(namespace))

  /**
   * Generates a JavaScript function computing localized messages for a given keys subset.
   *
   * Example:
   *
   * {{{
   *    @jsMessages.subsetHtml(Some("window.MyMessages"))(
   *      "error.required",
   *      "error.number"
   *    )
   * }}}
   */
  def subsetHtml(namespace: Option[String] = None)(keys: String*)(implicit lang: Lang) =
    script(subset(namespace)(keys: _*))

  /**
   * Generates a JavaScript function computing all messages for a given keys subset.
   *
   * Example:
   *
   * {{{
   *    @jsMessages.allSubsetHtml(Some("window.MyMessages"))(
   *      "error.required",
   *      "error.number"
   *    )
   * }}}
   */
  def allSubsetHtml(namespace: Option[String] = None)(keys: String*) =
    script(allSubset(namespace)(keys: _*))

  private def script(js: String) = Html(s"""<script type="text/javascript">$js</script>""")

  private def subsetMap(values: Map[String, String], keys: String*): Map[String, String] =
    (for {
      key <- keys
      message <- values.get(key)
    } yield (key, message)).toMap

  private def escapeMap(values: Map[String, String]): Map[String, String] = values.map {
    kv => escapeEcmaScript(kv._1) -> escapeEcmaScript(kv._2.replace("''", "'"))
  }

  private def formatMap(values: Map[String, String]): String =
    (for ((key, msg) <- values) yield {
      "'%s':'%s'".format(key, msg)
    }).mkString(",")

  private def formatAllMap(values: Map[String, Map[String, String]]): String =
    (for ((lang, messages) <- values) yield {
      "'%s':{%s}".format(lang, formatMap(messages))
    }).mkString(",")
}
