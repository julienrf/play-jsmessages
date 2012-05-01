package jsmessages;

import play.i18n.Lang;
import play.mvc.Http;
import play.libs.Scala;
import play.api.Play;

public class JsMessages {

    /**
     * Generates a JavaScript function computing localized messages using, if possible,
     * a language handled by your application and set in the Accept-Language header.
     * @param name Name of the JavaScript variable to create
     * @return The variable definition
     */
    public static String generate(String name) {
        return generate(name, Http.Context.Implicit.lang());
    }

    /**
     * Generates a JavaScript function computing localized messages using the given Lang.
     * @param name Name of the JavaScript variable to create
     * @param lang Lang to use
     * @return The variable definition
     */
    public static String generate(String name, Lang lang) {
        return jsmessages.api.JsMessages.apply(name, Play.current(), lang);
    }

    /**
     * Generates a JavaScript function computing localized messages for a given set of i18n keys.
     * @param name Name of the JavaScript variable to create
     * @param keys Keys to use
     * @return The variable definition
     */
    public static String subset(String name, String... keys) {
        return subset(name, Http.Context.Implicit.lang());
    }

    /**
     * Generates a JavaScript function computing localized messages for a given set of i18n keys,
     * using the given Lang.
     * @param name Name of the JavaScript variable to create
     * @param lang Lang to use
     * @param keys Keys to use
     * @return The variable definition
     */
    public static String subset(String name, Lang lang, String... keys) {
        return jsmessages.api.JsMessages.subset(name, Scala.toSeq(keys), Play.current(), lang);
    }

}
