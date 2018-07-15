package restaurant.api.util

import akka.http.scaladsl.server.{Directives, Route}

trait AbstractController extends Directives {

  def route: Route

}
