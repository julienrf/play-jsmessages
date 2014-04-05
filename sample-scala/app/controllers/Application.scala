package controllers

import play.api.mvc.{Controller, Action}
import play.api.Play.current
import jsmessages.api.JsMessages

object Application extends Controller {

  val messages = JsMessages.default

  val index = Action {
    Ok(views.html.index1())
  }

  val index1 = Action {
    Ok(views.html.index1())
  }

  val index2 = Action {
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

  val cn = Action {
    Ok(views.html.cn())
  }

  val messagesSubset = JsMessages.subset("greeting", "apostrophe")

  val subset = Action {
    Ok(views.html.subset.subset())
  }

  val subsetMessages = Action { implicit request =>
    Ok(messagesSubset(Some("window.Messages")))
  }

  val subsetAll = Action {
    Ok(views.html.subset.subsetAll())
  }

  val subsetAllMessages = Action {
    Ok(messagesSubset.all(Some("window.Messages")))
  }

  val filteredMessages = JsMessages.filtering(_.startsWith("error."))

  val filter = Action {
    Ok(views.html.filter.filter())
  }

  val filterMessages = Action { implicit request =>
    Ok(filteredMessages(Some("window.Messages")))
  }

  val filterAll = Action {
    Ok(views.html.filter.filterAll())
  }

  val filterAllMessages = Action {
    Ok(filteredMessages.all(Some("window.Messages")))
  }
}
