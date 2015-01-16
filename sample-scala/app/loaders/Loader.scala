package loaders

import controllers.{Assets, Application}
import jsmessages.JsMessagesFactoryComponents
import play.api.i18n.I18nComponents
import play.api.{BuiltInComponents, BuiltInComponentsFromContext, ApplicationLoader}
import play.api.ApplicationLoader.Context

class Loader extends ApplicationLoader {
  def load(context: Context) = (new BuiltInComponentsFromContext(context) with JsMessagesComponents).application
}

trait JsMessagesComponents extends BuiltInComponents with I18nComponents with JsMessagesFactoryComponents {
  val applicationCtl = new Application(jsMessagesFactory, messagesApi)
  val assets = new Assets(httpErrorHandler)
  def routes = new router.Routes(httpErrorHandler, applicationCtl, assets)
}