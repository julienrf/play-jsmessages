package loaders

import javax.inject.Inject

import jsmessages.JsMessagesFactoryComponents
import play.api.i18n.I18nComponents
import play.api.{ApplicationLoader, BuiltInComponents, BuiltInComponentsFromContext}
import play.api.ApplicationLoader.Context
import play.filters.HttpFiltersComponents
import router.Routes

class Loader @Inject() (
                         routes                 : Routes,
                         httpFiltersComponents  : HttpFiltersComponents
                       )
  extends ApplicationLoader
{
  def load(context: Context) = {
    val comp = new BuiltInComponentsFromContext(context) with JsMessagesComponents {
      override val httpFilters = httpFiltersComponents.httpFilters
      override def router = routes
    }
    comp.application
  }
}


trait JsMessagesComponents extends BuiltInComponents with I18nComponents with JsMessagesFactoryComponents {

}