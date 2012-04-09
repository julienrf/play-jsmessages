package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import jsmessages.JsMessages

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def jsMessages = Action { implicit request =>
    Ok(JsMessages("Messages")).as(JAVASCRIPT)
  }
}