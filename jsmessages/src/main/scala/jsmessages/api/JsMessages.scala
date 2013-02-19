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
   */
  def apply(namespace: Option[String] = None)(implicit app: Application, lang: Lang): String = apply(namespace, allMessages)

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
  def subset(namespace: Option[String] = None)(keys: String*)(implicit app: Application, lang: Lang): String = {
    val messages = (for {
      key <- keys
      message <- allMessages.get(key)
    } yield (key, message)).toMap
    apply(namespace, messages)
  }

  def apply(namespace: Option[String], messages: Map[String, String]): String = {
    import org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript
    """%s(function(){var ms={%s};
    return function(k, o){var d=[];var m=ms[k];if (typeof o=="string"){for(var i=1;i<arguments.length;i++){d.push(arguments[i]);}}else if(o!=null){if(typeof o.data=="string")d=[o.data];if(typeof o.data=="object")d=o.data;if(m==null){if(typeof o.alt == "string") m=ms[o.alt];if(typeof o.alt=="object")for(i=0;i<o.alt.length&&m==null;i++)m=ms[o.alt[i]];}}m=m||k;for(i=0;j<d.length;i++){m=m.replace('{'+i+'}',d[i])}return m
    }})();""".format(
           namespace.map{_ + "="}.getOrElse(""),
           (for ((key, msg) <- messages) yield {
             "'%s':'%s'".format(escapeEcmaScript(key), escapeEcmaScript(msg.replace("''", "'")))
           }).mkString(",")
    )
  }

  private def allMessages(implicit app: Application, lang: Lang) =
    Messages.messages.get("default").getOrElse(Map.empty) ++ Messages.messages.get(lang.code).getOrElse(Map.empty)
}
