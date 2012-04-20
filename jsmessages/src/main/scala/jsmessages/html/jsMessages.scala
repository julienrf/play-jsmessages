package jsmessages.html

import play.api.templates.Html

object jsMessages {

  /**
   * Generates a JavaScript function able to compute localized messages.
   * 
   * For example:
   * 
   * {{{
   *   @jsMessages("MyMessages")
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
  def apply(name: String = "Messages")(implicit app: play.api.Application, lang: play.api.i18n.Lang) = {
    Html(("""|<script type="text/javascript">
             |%s
             |</script>""").stripMargin.format(jsmessages.JsMessages(name)))
  }

}