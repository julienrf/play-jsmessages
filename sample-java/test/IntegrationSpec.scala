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

        browser.goTo("http://localhost:3333/")

        browser.$("body").getTexts().get(0) must equalTo ("Hello World! Hello There!")

        pawait(WS.url("http://localhost:3333/messages.js").withHeaders("Accept-Language"->"en").get).body must contain ("Hello {0}!")

        pawait(WS.url("http://localhost:3333/messages.js").withHeaders("Accept-Language"->"fr").get).body must contain ("Bonjour {0} !")
      }
    }
    
  }
  
}
