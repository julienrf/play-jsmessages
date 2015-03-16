package test

import loaders.JsMessagesComponents
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable._
import play.api._
import play.api.libs.ws.WSClient
import play.api.libs.ws.ning.NingWSComponents
import play.api.test.Helpers.{await => pawait, _}
import play.api.test._
import play.core.server.NettyServer

class IntegrationSpec extends Specification {

  case class Scope(browser: TestBrowser, ws: WSClient, baseUrl: String)
  
  def example[A : AsResult](f: Scope => A): Result = {
    val ctx = ApplicationLoader.createContext(Environment.simple())
    val components = new BuiltInComponentsFromContext(ctx) with JsMessagesComponents with NingWSComponents
    val webDriver = WebDriverFactory(Helpers.HTMLUNIT)
    val port = 3333
    val baseUrl = s"http://localhost:$port"
    val browser = TestBrowser(webDriver, Some(baseUrl))
    Helpers.running(TestServer(port, components.application))(
      AsResult.effectively(f(Scope(browser, components.wsClient, baseUrl)))
    )
  }

  "Application" should {

    "compute localized messages" in {
      example { scope =>

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

        for (i <- 1 to 2) {
          scope.browser.goTo("/test/" + i)
          val divs = scope.browser.$("div")
          testsEnUS.foreach { kv =>
            divs.get(kv._2).getText must equalTo (kv._1)
          }

          scope.browser.goTo("/all/" + i)
          val allDivs = scope.browser.$("div")
          testsEn.foreach { kv =>
            allDivs.get(kv._2).getText must equalTo (kv._1)
          }
        }

        scope.browser.goTo("/en")
        val allDivsEn = scope.browser.$("div")
        testsEn.foreach { kv =>
          allDivsEn.get(kv._2).getText must equalTo (kv._1)
        }

        scope.browser.goTo("/enUS")
        val allDivsEnUS = scope.browser.$("div")
        testsEnUS.foreach { kv =>
          allDivsEnUS.get(kv._2).getText must equalTo (kv._1)
        }

        scope.browser.goTo("/fr")
        val allDivsFr = scope.browser.$("div")
        testsFr.foreach { kv =>
          allDivsFr.get(kv._2).getText must equalTo (kv._1)
        }

        scope.browser.goTo("/cn")
        val allDivsCn = scope.browser.$("div")
        testsEn.foreach { kv =>
          allDivsCn.get(kv._2).getText must equalTo (kv._1)
        }

        scope.browser.goTo("/noLang")
        val allDivsNoLang = scope.browser.$("div")
        testsEn.foreach { kv =>
          allDivsNoLang.get(kv._2).getText must equalTo (kv._1)
        }

        val testsSubset = List(
          "Hello US World!",
          "Hello US People!",
          "root.parent.child",
          "I'm happy!",
          "error.maxLength"
        ).zipWithIndex

        scope.browser.goTo("/subset")
        val allDivsSubset = scope.browser.$("div")
        testsSubset.foreach { kv =>
          allDivsSubset.get(kv._2).getText must equalTo (kv._1)
        }

        scope.browser.goTo("/subsetAll")
        val allDivsSubsetAll = scope.browser.$("div")
        testsSubset.foreach { kv =>
          allDivsSubsetAll.get(kv._2).getText must equalTo (kv._1)
        }

        val testsFilter = List(
          "Required field",
          "greeting",
          "wrong",
          "root.parent.child",
          "Field is way too long"
        ).zipWithIndex

        scope.browser.goTo("/filter")
        val allDivsFilter = scope.browser.$("div")
        testsFilter.foreach { kv =>
          allDivsFilter.get(kv._2).getText must equalTo (kv._1)
        }

        scope.browser.goTo("/filterAll")
        val allDivsFilterAll = scope.browser.$("div")
        testsFilter.foreach { kv =>
          allDivsFilterAll.get(kv._2).getText must equalTo (kv._1)
        }

        pawait(scope.ws.url(s"${scope.baseUrl}/messages.js").withHeaders("Accept-Language"->"en").get()).body must contain ("Hello {0}!")
        pawait(scope.ws.url(s"${scope.baseUrl}/messages.js").withHeaders("Accept-Language"->"fr").get()).body must contain ("Bonjour {0} !")
        pawait(scope.ws.url(s"${scope.baseUrl}/all-messages.js").get()).body must contain ("Hello {0}!")
        pawait(scope.ws.url(s"${scope.baseUrl}/all-messages.js").get()).body must contain ("Bonjour {0} !")
      }
    }
  }
}
