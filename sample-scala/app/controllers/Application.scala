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
    Ok(views.html.index2(messages))
  }

  val jsMessages = Action { implicit request =>
    Ok(messages(Some("window.Messages"))).as(JAVASCRIPT)
  }

  val all1 = Action {
    Ok(views.html.all1())
  }

  val all2 = Action {
    Ok(views.html.all2(messages))
  }

  val allJsMessages = Action {
    Ok(messages.all(Some("window.Messages"))).as(JAVASCRIPT)
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
}
