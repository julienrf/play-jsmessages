package controllers

import play.api.mvc.{Controller, Action}
import play.api.Play.current
import jsmessages.api.JsMessages

object Application extends Controller {

  val messages = new JsMessages

  val index1 = Action {
    Ok(views.html.index1())
  }

  val index2 = Action { implicit request =>
    Ok(views.html.index2(messages))
  }

  val jsMessages = Action { implicit request =>
    Ok(messages(Some("window.Messages"))).as(JAVASCRIPT)
  }

}