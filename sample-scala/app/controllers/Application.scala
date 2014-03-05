package controllers

import play.api.mvc.{Controller, Action}
import play.api.Play.current
import jsmessages.api.JsMessages

object Application extends Controller {

  val messages = new JsMessages

  val index = Action {
    Ok(views.html.index1())
  }

  val index1 = Action {
    Ok(views.html.index1())
  }

  val index2 = Action { implicit request =>
    Ok(views.html.index2())
  }

  val jsMessages = Action { implicit request =>
    Ok(messages(Some("window.Messages")))
  }

  val all1 = Action {
    Ok(views.html.all1())
  }

  val all2 = Action {
    Ok(views.html.all2())
  }

  val allJsMessages = Action {
    Ok(messages.all(Some("window.Messages")))
  }

  val allJsMessagesTmpl = Action {
    Ok(views.js.all(messages))
  }

  val jsMessagesTmpl = Action { implicit request =>
    Ok(views.js.messages(messages))
  }

  val en = Action {
    Ok(views.html.en())
  }

  val enUS = Action {
    Ok(views.html.enUS())
  }

  val fr = Action {
    Ok(views.html.fr())
  }

  val subset = Action {
    Ok(views.html.subset.subset())
  }

  val subsetAll = Action {
    Ok(views.html.subset.subsetAll())
  }

  val filter = Action {
    Ok(views.html.filter.filter())
  }

  val filterAll = Action {
    Ok(views.html.filter.filterAll())
  }

  val subsetMessages = Action {
    Ok(messages.subset("greeting", "apostrophe")(Some("window.Messages")))
  }

  val subsetAllMessages = Action {
    Ok(messages.subsetAll("greeting", "apostrophe")(Some("window.Messages")))
  }

  val filterMessages = Action {
    Ok(messages.filter(_.startsWith("error."))(Some("window.Messages")))
  }

  val filterAllMessages = Action {
    Ok(messages.filterAll(_.startsWith("error."))(Some("window.Messages")))
  }
}
