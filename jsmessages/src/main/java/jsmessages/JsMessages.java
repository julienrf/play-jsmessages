package jsmessages;

import play.i18n.Lang;
import play.mvc.Http;
import play.libs.Scala;
import play.api.Play;

import java.util.Map;
import java.util.HashMap;

public class JsMessages {

    private static scala.collection.immutable.Map<String, scala.collection.immutable.Map<String,String>> emptyMap(){
        return Scala.<String, scala.collection.immutable.Map<String,String>>emptyMap();
    }

    private static scala.collection.immutable.Map<String, scala.collection.immutable.Map<String,String>> asScala(Map<String, Map<String,String>> external){
        if( external != null ){
            Map<String, scala.collection.immutable.Map<String,String>> build = new HashMap<String, scala.collection.immutable.Map<String,String>>();
            for( Map.Entry<String, Map<String, String>> entry : external.entrySet() ){
                build.put( entry.getKey(), Scala.asScala(entry.getValue()) );
            }
            return Scala.asScala(build);
        }
        return emptyMap();
    }

    /**
     * Generates a JavaScript function computing localized messages using, if possible,
     * a language handled by your application and set in the Accept-Language header.
     * @param namespace Namespace to which assign the generated function
     * @return The function definition
     */
    public static String generate(String namespace) {
        return generate(namespace, null, Http.Context.Implicit.lang());
    }

    /**
     * Generates a JavaScript function computing localized messages using, if possible,
     * a language handled by your application and set in the Accept-Language header.
     * @param namespace Namespace to which assign the generated function
     * @param external Additional keys, is overriden by the application keys.
     * @return The function definition
     */
    public static String generate(String namespace, Map<String, Map<String, String>> external ) {
        return generate(namespace, external, Http.Context.Implicit.lang());
    }

    /**
     * Generates a JavaScript function computing localized messages using the given Lang.
     * @param namespace Namespace to which assign the generated function
     * @param lang Lang to use
     * @return The function definition
     */
    public static String generate(String namespace, Lang lang) {
        return generate(namespace, null, lang);
    }

    /**
     * Generates a JavaScript function computing localized messages using the given Lang.
     * @param namespace Namespace to which assign the generated function
     * @param external Additional keys, is overriden by the application keys.
     * @param lang Lang to use
     * @return The function definition
     */
    public static String generate(String namespace, Map<String, Map<String, String>> external, Lang lang) {
        return jsmessages.api.JsMessages.apply(scala.Option.apply(namespace), asScala(external), Play.current(), lang);
    }

    /**
     * Generates a JavaScript function computing localized messages for a given set of i18n keys.
     * @param namespace Namespace to which assign the generated function
     * @param keys Keys to use
     * @return The function definition
     */
    public static String subset(String namespace, String... keys) {
        return subset(namespace, null, Http.Context.Implicit.lang(),keys);
    }

    /**
     * Generates a JavaScript function computing localized messages for a given set of i18n keys.
     * @param namespace Namespace to which assign the generated function
     * @param external Additional keys, is overriden by the application keys.
     * @param keys Keys to use
     * @return The function definition
     */
    public static String subset(String namespace, Map<String, Map<String, String>> external, String... keys) {
        return subset(namespace, external, Http.Context.Implicit.lang(), keys);
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
        return subset(namespace, null, Http.Context.Implicit.lang(), keys);
    }

    /**
     * Generates a JavaScript function computing localized messages for a given set of i18n keys,
     * using the given Lang.
     * @param namespace Namespace of which assign the generated function
     * @param external Additional keys, is overriden by the application keys.
     * @param lang Lang to use
     * @param keys Keys to use
     * @return The function definition
     */
    public static String subset(String namespace, Map<String, Map<String, String>> external, Lang lang, String... keys) {
        return jsmessages.api.JsMessages.subset(scala.Option.apply(namespace), asScala(external), Scala.toSeq(keys), Play.current(), lang);
    }

}
