package jsmessages;

import play.i18n.Lang;
import play.mvc.Http;
import play.libs.Scala;
import play.api.Play;

public class JsMessages {

    public static String apply(String name) {
        return apply(name, Http.Context.Implicit.lang());
    }

    public static String apply(String name, Lang lang) {
        return jsmessages.api.JsMessages.apply(name, Play.current(), lang);
    }

    public static String subset(String name, String... keys) {
        return subset(name, Http.Context.Implicit.lang());
    }

    public static String subset(String name, Lang lang, String... keys) {
        return jsmessages.api.JsMessages.subset(name, Scala.toSeq(keys), Play.current(), lang);
    }

}
