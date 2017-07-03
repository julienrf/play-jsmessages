package test

import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import org.scalatestplus.play.{HtmlUnitFactory, OneBrowserPerSuite, PlaySpec}
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import play.api.test.Helpers.{await => pawait}

class IntegrationSpec extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory {

  def urlPrefix = s"http://localhost:$port"

  "HTML content" must {

    "Say hello world" in {
      go to s"$urlPrefix/"
      find(tagName("body")).get.text mustBe "Hello World! Hello There!"
    }

  }


  lazy val wsClient = app.injector.instanceOf[WSClient]

  /** execute any tests against raw response body string. */
  def _mkRawBodyTest[U](urlPath: String, httpHeaders: (String, String)*)(f: String => U) = {
    val fut = wsClient.url(s"$urlPrefix$urlPath")
      .withHttpHeaders(httpHeaders: _*).get()
    f(pawait(fut).body)
  }

  "GET messages.js" must {

    "contain Hello for en" in {
      _mkRawBodyTest("/messages.js", "Accept-Language" -> "en") { b =>
        b must include ("Hello {0}!")
      }
    }

    "contain Bonjour for fr" in {
      _mkRawBodyTest( "/messages.js", "Accept-Language" -> "fr") { b =>
        b must include ("Bonjour {0} !")
      }
    }

  }

}
