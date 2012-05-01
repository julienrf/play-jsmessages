package jsmessages;

import play.i18n.Lang;
import play.mvc.Http;
import play.libs.Scala;
import play.api.Play;

public class JsMessages {

    /**
     * Generates a JavaScript function computing localized messages using, if possible,
     * a language handled by your application and set in the Accept-Language header.
     * @param namespace Namespace to which assign the generated function
     * @return The function definition
     */
    public static String generate(String namespace) {
        return generate(namespace, Http.Context.Implicit.lang());
    }

    /**
     * Generates a JavaScript function computing localized messages using the given Lang.
     * @param namespace Namespace to which assign the generated function
     * @param lang Lang to use
     * @return The function definition
     */
    public static String generate(String namespace, Lang lang) {
        return jsmessages.api.JsMessages.apply(scala.Option.apply(namespace), Play.current(), lang);
    }

    /**
     * Generates a JavaScript function computing localized messages for a given set of i18n keys.
     * @param namespace Namespace to which assign the generated function
     * @param keys Keys to use
     * @return The function definition
     */
    public static String subset(String namespace, String... keys) {
        return subset(namespace, Http.Context.Implicit.lang());
    }

    /**
     * Generates a JavaScript function computing localized messages for a given set of i18n keys,
     * using the given Lang.
     * @param namespace Namespace of which assign the generated function
     * @param lang Lang to use
     * @param keys Keys to use
     * @return The function definition
     */
    public static String subset(String namespace, Lang lang, String... keys) {
        return jsmessages.api.JsMessages.subset(scala.Option.apply(namespace), Scala.toSeq(keys), Play.current(), lang);
    }

}
