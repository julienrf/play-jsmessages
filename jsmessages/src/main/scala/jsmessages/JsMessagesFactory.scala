package jsmessages

import javax.inject.{Inject, Singleton}

import play.api.{Configuration, Environment}
import play.api.i18n.MessagesApi
import play.api.inject.Module

/**
 * Defines various methods returning a [[JsMessages]] instance.
 *
 * Typical usage:
 *
 * {{{
 *   import jsmessages.JsMessagesFactory
 *   import play.api.i18n.{I18nSupport, MessagesApi}
 *   import play.api.mvc.{Action, Controller}
 *
 *   class Application(jsMessagesFactory: JsMessagesFactory, val messagesApi: MessagesApi) extends Controller with I18nSupport {
 *     val jsMessages = jsMessagesFactory.all
 *     val messages = Action { implicit request =>
 *       Ok(messages(Some("window.Messages")))
 *     }
 *   }
 * }}}
 *
 * @param messagesApi The underlying Play i18n module to retrieve messages from
 */
@Singleton
class JsMessagesFactory @Inject() (messagesApi: MessagesApi) {
  /**
   * @return a `JsMessages` instance using all the messages of `messagesApi`
   */
  def all: JsMessages = new JsMessages(messagesApi.messages)

  /**
   * Example:
   *
   * {{{
   *   val jsMessages = JsMessages.filtering(_.startsWith("error."))
   * }}}
   *
   * @param filter a predicate to filter message keys
   * @return a `JsMessages` instance keeping only messages whose keys satisfy `filter`
   */
  def filtering(filter: String => Boolean): JsMessages = {
    new JsMessages(messagesApi.messages.mapValues(_.filterKeys(filter)))
  }

  /**
   * Example:
   *
   * {{{
   *   val jsMessages = JsMessages.subset(
   *     "error.required",
   *     "error.number"
   *   )
   * }}}
   *
   * @param keys the list of keys to keep
   * @param app the application to retrieve messages from
   * @return a `JsMessages` instance keeping only messages whose keys are in `keys`
   */
  def subset(keys: String*): JsMessages = filtering(keys.contains)

}

trait JsMessagesFactoryComponents {

  def messagesApi: MessagesApi

  lazy val jsMessagesFactory = new JsMessagesFactory(messagesApi)

}
