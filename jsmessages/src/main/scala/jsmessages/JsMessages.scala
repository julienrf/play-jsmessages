package jsmessages

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
   *     Ok(JsMessages("MyMessages")).as(JAVASCRIPT)
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
   */
  def apply(name: String = "Messages")(implicit app: Application, lang: Lang): String = apply(name, allMessages)

  /**
   * Generates a JavaScript function able to compute localized messages for a given keys subset.
   * 
   * Example:
   * 
   * {{{
   *   JsMessages.subset("MyMessages")(
   *     "error.required",
   *     "error.number"
   *   )
   * }}}
   */
  def subset(name: String = "Messages")(keys: String*)(implicit app: Application, lang: Lang): String = {
    val messages = (for {
      key <- keys
      message <- allMessages.get(key)
    } yield (key, message)).toMap
    apply(name, messages)
  }

  def apply(name: String, messages: Map[String, String])(implicit app: Application, lang: Lang): String = {
    import org.apache.commons.lang.StringEscapeUtils.escapeJavaScript
    """var %s=(function(){var ms={%s}; return function(k){var m=ms[k]||k;for(var i=1,l=arguments.length;i<l;i++){m=m.replace('{'+(i-1)+'}',arguments[i])} return m}})();""".format(
           name,
           (for ((key, msg) <- messages) yield {
             "'%s':'%s'".format(escapeJavaScript(key), escapeJavaScript(msg.replace("''", "'")))
           }).mkString(",")
    )
  }

  private def allMessages(implicit app: Application, lang: Lang) =
    Messages.messages.get("default").getOrElse(Map.empty) ++ Messages.messages.get(lang.code).getOrElse(Map.empty)
}
