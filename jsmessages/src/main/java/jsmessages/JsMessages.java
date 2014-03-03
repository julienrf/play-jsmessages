package jsmessages;

import play.Application;
import play.api.templates.JavaScript;
import play.i18n.Lang;
import play.libs.Scala;
import play.mvc.Http;

/**
 * Generate a JavaScript function computing localized messages of a Play! application (Java API).
 */
public class JsMessages {

    final jsmessages.api.JsMessages api;

    /**
     * @param app Play! application to get messages from
     */
    public JsMessages(Application app) {
        this.api = new jsmessages.api.JsMessages(app.getWrappedApplication());
    }

    /**
     * Generates a JavaScript function computing localized messages using, if possible,
     * a language handled by your application and set in the Accept-Language header.
     * @param namespace Namespace to which assign the generated function
     * @return The function definition
     */
    public JavaScript generate(String namespace) {
        return generate(namespace, Http.Context.current().lang());
    }

    /**
     * Generates a JavaScript function computing localized messages using the given Lang.
     * @param namespace Namespace to which assign the generated function
     * @param lang Lang to use
     * @return The function definition
     */
    public JavaScript generate(String namespace, Lang lang) {
        return api.apply(scala.Option.apply(namespace), lang);
    }

    /**
     * Generates a JavaScript function computing all messages.
     * @param namespace Namespace to which assign the generated function
     * @return The function definition
     */
    public JavaScript generateAll(String namespace) {
        return api.all(scala.Option.apply(namespace));
    }

    /**
     * Generates a JavaScript function computing localized messages for a given set of i18n keys.
     * @param namespace Namespace to which assign the generated function
     * @param keys Keys to use
     * @return The function definition
     */
    public JavaScript subset(String namespace, String... keys) {
        return subset(namespace, Http.Context.current().lang(),keys);
    }

    /**
     * Generates a JavaScript function computing localized messages for a given set of i18n keys,
     * using the given Lang.
     * @param namespace Namespace of which assign the generated function
     * @param lang Lang to use
     * @param keys Keys to use
     * @return The function definition
     */
    public JavaScript subset(String namespace, Lang lang, String... keys) {
        return api.subset(scala.Option.apply(namespace), Scala.toSeq(keys), lang);
    }

    /**
     * Generates a JavaScript function computing all messages,
     * using the given Lang.
     * @param namespace Namespace of which assign the generated function
     * @param keys Keys to use
     * @return The function definition
     */
    public JavaScript subsetAll(String namespace, String... keys) {
        return api.subsetAll(scala.Option.apply(namespace), Scala.toSeq(keys));
    }

}
