package controllers;

import jsmessages.JsMessages;
import jsmessages.JsMessagesFactory;
import play.i18n.MessagesApi;
import play.libs.Scala;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class Application extends Controller {

    private final JsMessages jsMessages;

    private final MessagesApi messagesApi;

    @Inject
    public Application(JsMessagesFactory jsMessagesFactory, MessagesApi messagesApi) {
        jsMessages = jsMessagesFactory.all();
        this.messagesApi = messagesApi;
    }

    public Result index1() {
        return ok(views.html.index1.render());
    }

    public Result jsMessages(Http.Request request) {
        return ok(jsMessages.apply(Scala.Option("window.Messages"), this.messagesApi.preferred(request).asScala()));
    }

}