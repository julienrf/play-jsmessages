package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import jsmessages.api.JsMessages

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Basic example to compute localized messages of Play Application on client side"))
  }

}