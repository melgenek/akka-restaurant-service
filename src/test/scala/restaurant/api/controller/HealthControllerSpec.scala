package restaurant.api.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import restaurant.api.util.BaseSpec

class HealthControllerSpec extends BaseSpec with ScalatestRouteTest {

  "/v1/healthcheck" should "return alive message" in new Wiring {
    Get("/v1/healthcheck") ~> controller.route ~> check {
      status should equal(StatusCodes.OK)
      entityAs[String] should be("Alive")
    }
  }

  private trait Wiring {

    val controller = new HealthController

  }

}
