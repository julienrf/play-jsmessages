package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import jsmessages.api.JsMessages

object JsMessagesController extends Controller with JsMessagesController

trait JsMessagesController {
  this: Controller =>

  val messages = JsMessages.default

  val jsMessages = Action { implicit request =>
    Ok(messages.all(Some("window.Messages")))
  }

}