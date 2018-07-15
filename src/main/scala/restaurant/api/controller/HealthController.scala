package restaurant.api.controller

import akka.http.scaladsl.server.Route
import restaurant.api.util.AbstractController

class HealthController extends AbstractController {

  override def route: Route = path("v1" / "healthcheck") {
    complete("Alive")
  }

}
