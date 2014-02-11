package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.test.Helpers.{await => pawait}
import play.api.libs.ws.WS

class IntegrationSpec extends Specification {

  "Application" should {
    
    "compute localized messages" in {
      running(TestServer(3333), HTMLUNIT) { browser =>

        val tests = List(
          "Hello World!",
          "Hello People!",
          "Complex key",
          "Arg0 Zero and arg1 1 are on a boat",
          "wrong.key",
          "Hi there World!",
          "Now valid",
          "Greetings baby!",
          "Zero, 1111 and true"
        ).zipWithIndex

        for (i <- 1 to 2) {
          browser.goTo("http://localhost:3333/test/" + i)
          val divs = browser.$("div")
          tests.foreach { kv =>
            divs.get(kv._2).getText must equalTo (kv._1)
          }

          browser.goTo("http://localhost:3333/all/" + i)
          val allDivs = browser.$("div")
          tests.foreach { kv =>
            allDivs.get(kv._2).getText must equalTo (kv._1)
          }
        }

        pawait(WS.url("http://localhost:3333/messages.js").withHeaders("Accept-Language"->"en").get()).body must contain ("Hello {0}!")
        pawait(WS.url("http://localhost:3333/messages.js").withHeaders("Accept-Language"->"fr").get()).body must contain ("Bonjour {0} !")
        pawait(WS.url("http://localhost:3333/allMessages.js").get()).body must contain ("Hello {0}!")
        pawait(WS.url("http://localhost:3333/allMessages.js").get()).body must contain ("Bonjour {0} !")
      }
    }
  }
}
