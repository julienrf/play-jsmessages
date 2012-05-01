package controllers;

import play.mvc.*;
import views.html.*;
import jsmessages.JsMessages;

public class Application extends Controller {

    public static Result index1() {
        return ok(index1.render());
    }

    public static Result jsMessages() {
        return ok(JsMessages.generate("Messages")).as("application/javascript");
    }

}