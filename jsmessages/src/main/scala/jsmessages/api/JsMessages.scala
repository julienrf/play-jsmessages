package jsmessages.api

import play.api.i18n._
import play.api.Application

object JsMessages {
  /**
   * Generates a JavaScript function able to compute localized messages.
   *
   * For example:
   *
   * {{{
   *   def jsMessages = Action { implicit request =>
   *     Ok(JsMessages(Some("window.MyMessages"))).as(JAVASCRIPT)
   *   }
   * }}}
   *
   * And you can use it in your JavaScript code as follows:
   * {{{
   *   alert(MyMessages('greeting', 'World'));
   * }}}
   *
   * Provided you have the following message in your conf/messages file:
   * {{{
   * greeting=Hello {0}!
   * }}}
   *
   * Note: This implementation does not handle quotes escaping in patterns (see http://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html)
   *
   * @param namespace Optional JavaScript namespace to use to put the function definition. If not set this function will
   * just generate a function. Otherwise it will generate a function and assign it to the given namespace. Note: you can
   * set something like `Some("var Messages")` to use a fresh variable.
   * @param external Additional keys, is overriden by the application keys.
   */
  def apply(namespace: Option[String] = None, external: Map[String,Map[String,String]] = Map.empty)(implicit app: Application, lang: Lang): String = apply(namespace, allMessages(external))

  /**
   * Generates a JavaScript function able to compute localized messages for a given keys subset.
   *
   * Example:
   *
   * {{{
   *   JsMessages.subset(Some("window.MyMessages"))(
   *     "error.required",
   *     "error.number"
   *   )
   * }}}
   */
  def subset(namespace: Option[String] = None, external: Map[String,Map[String,String]] = Map.empty)(keys: String*)(implicit app: Application, lang: Lang): String = {
    val messages = (for {
      key <- keys
      message <- allMessages(external).get(key)
    } yield (key, message)).toMap
    apply(namespace, messages)
  }

  def apply(namespace: Option[String], messages: Map[String, String]): String = {
    import org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript
    """%s(function(u){var ms={%s};return function(k){if(typeof k == "object"){for(var i=0;i<k.length&&ms[k[i]]!==u;i++);var m=ms[k[i]]||k[0]}else{m=((ms[k]!==u)?ms[k]:k)}for(i=1;i<arguments.length;i++){m=m.replace('{'+(i-1)+'}',arguments[i])}return m}})()""".format(
           namespace.map{_ + "="}.getOrElse(""),
           (for ((key, msg) <- messages) yield {
             "'%s':'%s'".format(escapeEcmaScript(key), escapeEcmaScript(msg.replace("''", "'")))
           }).mkString(",")
    )
  }

  private def allMessages(external: Map[String, Map[String,String]] = Map.empty)(implicit app: Application, lang: Lang) =
    external.get(lang.code).getOrElse(Map.empty) ++ Messages.messages.get("default").getOrElse(Map.empty) ++ Messages.messages.get(lang.code).getOrElse(Map.empty)
}
