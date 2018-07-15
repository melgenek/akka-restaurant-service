package restaurant.api.controller

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import restaurant.api.dto.{CreateRestaurantRequest, UpdateRestaurantRequest}
import restaurant.api.service.RestaurantService
import restaurant.api.util.AbstractController

class RestaurantController(service: RestaurantService) extends AbstractController with FailFastCirceSupport {

  override def route: Route = pathPrefix("restaurants") {
    pathEndOrSingleSlash {
      get {
        list
      } ~ post {
        create
      }
    } ~ path(JavaUUID) { id =>
      get {
        find(id)
      } ~ put {
        update(id)
      } ~ delete {
        deleteRestaurant(id)
      }
    }
  }

  private def list: Route = complete(service.list())

  private def create: Route =
    entity(as[CreateRestaurantRequest]) { request =>
      onSuccess(service.create(request)) {
        case Right(restaurant) => complete(StatusCodes.Created -> restaurant)
        case Left(restaurant) => complete(StatusCodes.Conflict -> restaurant)
      }
    }

  private def find(id: UUID): Route =
    onSuccess(service.find(id)) {
      case Some(restaurant) => complete(restaurant)
      case None => complete(StatusCodes.NotFound)
    }

  private def update(id: UUID): Route =
    entity(as[UpdateRestaurantRequest]) { request =>
      onSuccess(service.update(id, request)) {
        complete(StatusCodes.NoContent)
      }
    }

  private def deleteRestaurant(id: UUID): Route =
    onSuccess(service.delete(id)) {
      complete(StatusCodes.NoContent)
    }

}
