package jsmessages.html

import play.api.templates.Html
import jsmessages.api.JsMessages

object jsMessages {

  /**
   * Generates a JavaScript function able to compute localized messages.
   * 
   * For example:
   * 
   * {{{
   *   @jsMessages(Some("window.MyMessages"))
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
   */
  def apply(namespace: Option[String] = None)(implicit app: play.api.Application, lang: play.api.i18n.Lang) =
    script(JsMessages(namespace))

  /**
   * Generates a JavaScript function able to compute localized messages for a given keys subset.
   * 
   * Example:
   * 
   * {{{
   *    @jsMessages.subset(Some("window.MyMessages"))(
   *      "error.required",
   *      "error.number"
   *    )
   * }}}
   */
  def subset(namespace: Option[String] = None)(keys: String*)(implicit app: play.api.Application, lang: play.api.i18n.Lang) =
    script(JsMessages.subset(namespace)(keys: _*))

  private def script(js: String) = Html("""<script type="text/javascript">%s</script>""".format(js))

}