package controllers;

import play.Play;
import play.mvc.*;
import views.html.*;
import jsmessages.JsMessages;

public class Application extends Controller {

    final static JsMessages messages = new JsMessages(Play.application());

    public static Result index1() {
        return ok(index1.render());
    }

    public static Result jsMessages() {
        return ok(messages.generate("window.Messages"));
    }

}