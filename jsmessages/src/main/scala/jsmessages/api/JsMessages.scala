package jsmessages.api

import play.api.i18n._
import play.api.Application
import play.api.templates.Html

/**
 * Can generate a JavaScript function computing localized messages of a Play application
 * @param app Play application to use
 */
class JsMessages(implicit app: Application) {

  /**
   * The application’s messages to use by default, as a map of (key -> message)
   */
  val defaultMessages = Messages.messages.get("default").getOrElse(Map.empty)

  /**
   * @param lang Language to retrieve messages for
   * @return The messages defined in the given Play application `app`, for the given language `lang`, as a map of (key -> message)
   */
  def messages(implicit lang: Lang): Map[String, String] =
    defaultMessages ++ Messages.messages.get(lang.code).getOrElse(Map.empty)

  /**
   * @return The messages defined in the given Play application `app`, for all languages, as a map of (lang -> map(key -> message))
   */
  def allMessages: Map[String, Map[String, String]] = Messages.messages.mapValues(defaultMessages ++ _)

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
  def apply(namespace: Option[String] = None)(implicit lang: Lang): String = apply(namespace, messages)

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
  def all(namespace: Option[String] = None): String = all(namespace, allMessages)

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
  def apply(namespace: Option[String], messages: Map[String, String]): String = {
    import org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript
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
           (for ((key, msg) <- messages) yield {
             "'%s':'%s'".format(escapeEcmaScript(key), escapeEcmaScript(msg.replace("''", "'")))
           }).mkString(",")
    )
  }

  /**
   * @param namespace Optional JavaScript namespace to use to put the function definition. If not set, this function will
   *                  just return a literal function. Otherwise it will
   * @param messages Map of (lang -> Map of (key -> message)) to use
   * @return A JavaScript fragment defining a function computing all messages
   */
  def all(namespace: Option[String], messages: Map[String, Map[String, String]]): String = {
    import org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript
    """ #%s(function(u){function f(l,k){
          #var m;
          #if(typeof k==='object'){
            #for(var i=0,le=k.length;i<le&&f.messages[l][k[i]]===u;++i);
            #m=f.messages[l][k[i]]||k[0]
          #}else{
            #m=((f.messages[l][k]!==u)?f.messages[l][k]:k)
          #}
          #for(i=2,le=arguments.length;i<le;++i){
            #m=m.replace('{'+(i-2)+'}',arguments[i])
          #}
          #return m};
          #f.messages={%s};
          #return f})()""".stripMargin('#').format(
           namespace.map{_ + "="}.getOrElse(""),
           (for ((lang, messages) <- allMessages) yield {
             "'%s':{%s}".format(lang, (for ((key, msg) <- messages) yield {
               "'%s':'%s'".format(escapeEcmaScript(key), escapeEcmaScript(msg.replace("''", "'")))
             }).mkString(","))
           }).mkString(",")
    )
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

  private def subsetMap(map: Map[String, String], keys: String*): Map[String, String] =
    (for {
      key <- keys
      message <- map.get(key)
    } yield (key, message)).toMap
}
