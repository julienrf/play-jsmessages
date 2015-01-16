package controllers;

import jsmessages.JsMessages;
import jsmessages.JsMessagesFactory;
import play.libs.Scala;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Application extends Controller {

    private final JsMessages jsMessages;

    @Inject
    public Application(JsMessagesFactory jsMessagesFactory) {
        jsMessages = jsMessagesFactory.all();
    }

    public Result index1() {
        return ok(views.html.index1.render());
    }

    public Result jsMessages() {
        return ok(jsMessages.apply(Scala.Option("window.Messages"), Http.Context.current().lang()));
    }

}