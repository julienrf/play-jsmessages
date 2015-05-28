package jsmessages.japi;

import play.i18n.Messages;
import play.mvc.Http;

public class Helper {

    public static play.api.i18n.Messages messagesFromCurrentHttpContext() {
        Messages javaMessages = Http.Context.current().messages();
        return new play.api.i18n.Messages(javaMessages.lang(), javaMessages.messagesApi().scalaApi());
    }

}
