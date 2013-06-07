package jsmessages;

import play.Application;
import play.api.templates.Html;
import play.i18n.Lang;
import play.libs.Scala;
import play.mvc.Http;

/**
 * Can generate a JavaScript function computing localized messages of a Play application
 */
public class JsMessages {

    final jsmessages.api.JsMessages api;

    public JsMessages(Application app) {
        this.api = new jsmessages.api.JsMessages(app.getWrappedApplication());
    }

    /**
     * Generates a JavaScript function computing localized messages using, if possible,
     * a language handled by your application and set in the Accept-Language header.
     * @param namespace Namespace to which assign the generated function
     * @return The function definition
     */
    public String generate(String namespace) {
        return generate(namespace, Http.Context.current().lang());
    }

    /**
     * Generates a JavaScript function computing localized messages using the given Lang.
     * @param namespace Namespace to which assign the generated function
     * @param lang Lang to use
     * @return The function definition
     */
    public String generate(String namespace, Lang lang) {
        return api.apply(scala.Option.apply(namespace), lang);
    }

    /**
     * Generates a JavaScript function computing localized messages for a given set of i18n keys.
     * @param namespace Namespace to which assign the generated function
     * @param keys Keys to use
     * @return The function definition
     */
    public String subset(String namespace, String... keys) {
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
    public String subset(String namespace, Lang lang, String... keys) {
        return api.subset(scala.Option.apply(namespace), Scala.toSeq(keys), lang);
    }

    /**
     * <p>Generates a JavaScript function computing localized messages.</p>
     *
     * For example:
     *
     * <pre>
     *   @jsMessages.html("window.MyMessages", lang)
     * </pre>
     *
     * Then use it in your JavaScript code as follows:
     *
     * <pre>
     *   alert(MyMessages('greeting', 'World'));
     * </pre>
     *
     * Provided you have the following message in your conf/messages file:
     *
     * <pre>
     * greeting=Hello {0}!
     * </pre>
     */
    public Html html(String namespace, Lang lang) {
        return api.html(scala.Option.apply(namespace), lang);
    }

    /**
     * <p>Generates a JavaScript function computing localized messages using the language provided by the current HTTP context</p>
     *
     * For example:
     *
     * <pre>
     *   @jsMessages.html("window.MyMessages")
     * </pre>
     *
     * Then use it in your JavaScript code as follows:
     *
     * <pre>
     *   alert(MyMessages('greeting', 'World'));
     * </pre>
     *
     * Provided you have the following message in your conf/messages file:
     *
     * <pre>
     * greeting=Hello {0}!
     * </pre>
     */
    public Html html(String namespace) {
        return html(namespace, Http.Context.current().lang());
    }

    /**
     * <p>Generates a JavaScript function computing localized messages for a given keys subset.</p>
     *
     * <p>Example:</p>
     *
     * <pre>
     *    @jsMessages.subsetHtml("window.MyMessages", lang)(
     *      "error.required",
     *      "error.number"
     *    )
     * </pre>
     */
    public Html subsetHtml(String namespace, Lang lang, String... keys) {
        return api.subsetHtml(scala.Option.apply(namespace), Scala.toSeq(keys), lang);
    }

    /**
     * <p>Generates a JavaScript function computing localized messages for a given keys subset using the language provided
     * by the current HTTP context</p>
     *
     * <p>Example:</p>
     *
     * <pre>
     *    @jsMessages.subsetHtml("window.MyMessages", lang)(
     *      "error.required",
     *      "error.number"
     *    )
     * </pre>
     */
    public Html subsetHtml(String namespace, String... keys) {
        return subsetHtml(namespace, Http.Context.current().lang(), keys);
    }
}
