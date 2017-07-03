package jsmessages.japi;

import play.mvc.Http;

public class Helper {

    public static play.api.i18n.Messages messagesFromCurrentHttpContext() {
        return Http.Context.current().messages().asScala();
    }

}
