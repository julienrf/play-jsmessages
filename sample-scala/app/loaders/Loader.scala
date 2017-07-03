package loaders

import javax.inject.Inject

import controllers.{Application, Assets, AssetsMetadata}
import jsmessages.JsMessagesFactoryComponents
import play.api.i18n.I18nComponents
import play.api.{ApplicationLoader, BuiltInComponents, BuiltInComponentsFromContext}
import play.api.ApplicationLoader.Context
import play.api.mvc.ControllerComponents
import play.filters.HttpFiltersComponents
import router.Routes

class Loader @Inject() (
                         assetsMetadata         : AssetsMetadata,
                         httpFiltersComponents  : HttpFiltersComponents
                       )
  extends ApplicationLoader
{
  def load(context: Context) = {
    val comp = new BuiltInComponentsFromContext(context) with JsMessagesComponents {
      override def httpFilters = httpFiltersComponents.httpFilters
      override def _assetsMetadata = assetsMetadata
      override protected def _controllerComponents = controllerComponents
    }
    comp.application
  }
}


trait JsMessagesComponents extends BuiltInComponents with I18nComponents with JsMessagesFactoryComponents {

  protected def _assetsMetadata: AssetsMetadata
  protected def _controllerComponents: ControllerComponents

  val applicationCtl = new Application(jsMessagesFactory, _controllerComponents)
  val assets = new Assets(httpErrorHandler, _assetsMetadata)
  lazy val router = new Routes(httpErrorHandler, applicationCtl, assets)

}