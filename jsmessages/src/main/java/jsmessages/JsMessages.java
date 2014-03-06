package jsmessages;

import play.Application;
import play.api.templates.JavaScript;
import play.i18n.Lang;
import play.libs.Scala;
import play.libs.F.Function;
import play.mvc.Http;

/**
 * Generate a JavaScript function computing localized messages of a Play! application (Java API).
 */
public class JsMessages {

    final jsmessages.api.JsMessages api;

    /**
     * @param api The underlying API to use
     */
    private JsMessages(jsmessages.api.JsMessages api) {
        this.api = api;
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
     * Factory method using the given application’s messages.
     * @param app Application to use
     */
    public static JsMessages create(Application app) {
        return new JsMessages(jsmessages.api.JsMessages.create(app.getWrappedApplication()));
    }

    /**
     * Factory method keeping only a subset of the application’s messages.
     * @param app Application to use
     * @param keys Keys to use
     */
    public static JsMessages subset(Application app, String... keys) {
        return new JsMessages(jsmessages.api.JsMessages.subset(Scala.toSeq(keys), app.getWrappedApplication()));
    }

    /**
     * Factory method filtering the application’s messages according to a predicate.
     * @param app Application to use
     * @param filter the predicate to use
     */
    public static JsMessages filtering(Application app, final Function<String, Boolean> filter) {
        return new JsMessages(jsmessages.api.JsMessages.filtering(new scala.runtime.AbstractFunction1<String, Object>() {
            @Override
            public Boolean apply(String key) {
                try {
                    return filter.apply(key);
                } catch (Throwable throwable) {
                    return false;
                }
            }
        }, app.getWrappedApplication()));
    }

}
