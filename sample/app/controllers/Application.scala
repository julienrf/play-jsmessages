package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import jsmessages.JsMessages

object Application extends Controller {

  def index1 = Action {
    Ok(views.html.index1())
  }

  def index2 = Action { implicit request =>
    Ok(views.html.index2())
  }

  def jsMessages = Action { implicit request =>
    Ok(JsMessages("Messages")).as(JAVASCRIPT)
  }

}