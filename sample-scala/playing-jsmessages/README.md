# [Playing JsMessages](https://playing-jsmessages.herokuapp.com/)
Basic example to compute localized messages of Play Application on client side

Basically, [Play JsMessages](https://github.com/julienrf/play-jsmessages) library takes the i18n messages of our Play application, sends them to the client-side as a JSON object and defines a JavaScript function returning a message value from a given language and the message key and arguments.
- Used [Play JsMessages](https://github.com/julienrf/play-jsmessages) library to access i18n messages from client-side
- Embedded JS & CSS libraries with [WebJars](http://www.webjars.org/).
- Integrating with a CSS framework (Twitter Bootstrap 3.1.1)
- Bootswatch-Darkly with Twitter Bootstrap 3.1.1 to improve the look and feel of the application

-----------------------------------------------------------------------
###Instructions :-
-----------------------------------------------------------------------
* To run the Play Framework, you need JDK 6 or later
* Install Typesafe Activator if you do not have it already. You can get it from [here](http://www.playframework.com/download) 
* Execute `./activator clean compile` to build the product
* Execute `./activator run` to execute the product
* playing-jsmessages should now be accessible at localhost:9000

-----------------------------------------------------------------------
###References :-
-----------------------------------------------------------------------
* [Play Framework](http://www.playframework.com/)
* [Play JsMessages](https://github.com/julienrf/play-jsmessages)
* [Bootstrap 3.1.1](http://getbootstrap.com/css/)
* [Bootswatch](http://bootswatch.com/darkly/)
* [WebJars](http://www.webjars.org/)