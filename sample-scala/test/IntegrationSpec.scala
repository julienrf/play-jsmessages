package test

import org.scalatestplus.play.{HtmlUnitFactory, OneBrowserPerSuite, PlaySpec}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.test.Helpers.{await => pawait, _}

class IntegrationSpec extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory {

  lazy val wsClient = app.injector.instanceOf[WSClient]


  val testsEn = List(
    "Hello World!",
    "Hello People!",
    "Complex key",
    "Arg0 Zero and arg1 1 are on a boat",
    "wrong.key",
    "I'm happy!",
    "Hi there World!",
    "Now valid",
    "Greetings baby!",
    "Zero, 1111 and true"
  ).zipWithIndex

  val testsEnUS = List(
    "Hello US World!",
    "Hello US People!",
    "Complex key",
    "Arg0 Zero and arg1 1 are on a boat",
    "wrong.key",
    "I'm happy!",
    "Hi there World US!",
    "Now valid US",
    "Greetings baby US!",
    "Zero, 1111 and true"
  ).zipWithIndex

  val testsFr = List(
    "Bonjour World !",
    "Bonjour People !",
    "Clé complexe",
    "Arg0 Zero and arg1 1 are on a boat",
    "wrong.key",
    "Salut l'ami",
    "Salut ici World!",
    "Désormais valide",
    "Salut toi!",
    "Zero, 1111 et true"
  ).zipWithIndex


  val testsSubset = List(
    "Hello US World!",
    "Hello US People!",
    "root.parent.child",
    "I'm happy!",
    "error.maxLength"
  ).zipWithIndex

  val testsFilter = List(
    "Required field",
    "greeting",
    "wrong",
    "root.parent.child",
    "Field is way too long"
  ).zipWithIndex



  def urlPrefix = s"http://localhost:$port"

  def _mkHtmlTests(urlPath: String, propers: List[(String, Int)]) = {
    go to s"$urlPrefix$urlPath"
    val divs = findAll( tagName("div") ).toSeq
    for {
      (k, v) <- propers
    } {
      divs(v).text mustBe k
    }
  }

  "HTML pages" must {

    "test/*" in {
      for (i <- 1 to 2) {
        _mkHtmlTests( s"/test/$i", testsEnUS )
      }
    }

    "all/*" in {
      for (i <- 1 to 2) {
        _mkHtmlTests( s"/all/$i", testsEn )
      }
    }

    "en" in {
      _mkHtmlTests( "/en", testsEn )
    }

    "enUS" in {
      _mkHtmlTests( "/enUS", testsEnUS )
    }

    "fr" in {
      _mkHtmlTests( "/fr", testsFr )
    }

    "cn" in {
      _mkHtmlTests( "/cn", testsEn )
    }

    "noLang" in {
      _mkHtmlTests( "/noLang", testsEn )
    }

    "subset" in {
      _mkHtmlTests( "/subset", testsSubset )
    }

    "subsetAll" in {
      _mkHtmlTests( "/subsetAll", testsSubset )
    }

    "filter" in {
      _mkHtmlTests( "/filter", testsFilter )
    }

    "filterAll" in {
      _mkHtmlTests( "/filterAll", testsFilter )
    }

  }



  /** execute any tests against raw response body string. */
  def _mkRawBodyTest[U](urlPath: String, httpHeaders: (String, String)*)(f: String => U) = {
    val fut = wsClient.url(s"$urlPrefix$urlPath")
      .withHttpHeaders(httpHeaders: _*).get()
    f(pawait(fut).body)
  }

  "GET messages.js" must {

    "contain Hello for english" in {
      _mkRawBodyTest( "/messages.js", "Accept-Language" -> "en") { b =>
        b must include ("Hello {0}!")
      }
    }

    "contain Bonjour for French" in {
      _mkRawBodyTest("/messages.js", "Accept-Language" -> "fr") { b =>
        b must include ("Bonjour {0} !")
      }
    }

  }


  "GET all-messages.js" must {

    "contain both Hello and Bonjour" in {
      _mkRawBodyTest("/all-messages.js") { b =>
        b must include ("Hello {0}!")
        b must include ("Bonjour {0} !")
      }
    }

  }

}
