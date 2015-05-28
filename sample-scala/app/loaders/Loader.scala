package loaders

import controllers.{Assets, Application}
import jsmessages.JsMessagesFactoryComponents
import play.api.i18n.I18nComponents
import play.api.{BuiltInComponents, BuiltInComponentsFromContext, ApplicationLoader}
import play.api.ApplicationLoader.Context
import router.Routes

class Loader extends ApplicationLoader {
  def load(context: Context) = (new BuiltInComponentsFromContext(context) with JsMessagesComponents).application
}

trait JsMessagesComponents extends BuiltInComponents with I18nComponents with JsMessagesFactoryComponents {
  val applicationCtl = new Application(jsMessagesFactory, messagesApi)
  val assets = new Assets(httpErrorHandler)
  lazy val router = new Routes(httpErrorHandler, applicationCtl, assets)
}