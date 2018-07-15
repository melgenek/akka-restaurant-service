package restaurant.api.controller

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import restaurant.api.dto.{CreateRestaurantRequest, Restaurant, UpdateRestaurantRequest}
import restaurant.api.service.RestaurantService
import restaurant.api.util.BaseSpec

class RestaurantControllerSpec extends BaseSpec with ScalatestRouteTest with FailFastCirceSupport {

  "GET /restaurants" should "list all restaurants" in new Wiring {
    when(service.list()).thenReturnAsync(Seq(restaurant))

    Get("/restaurants") ~> controller.route ~> check {
      status should equal(StatusCodes.OK)

      entityAs[Seq[Restaurant]] should be(Seq(restaurant))
    }
  }

  it should "return empty list when no restaurants" in new Wiring {
    when(service.list()).thenReturnAsync(Seq.empty)

    Get("/restaurants") ~> controller.route ~> check {
      status should equal(StatusCodes.OK)

      entityAs[Seq[Restaurant]].size should be(0)
    }
  }

  "POST /restaurants" should "return the new restaurant" in new Wiring {
    when(service.create(any())).thenReturnAsync(Right(restaurant))

    Post(s"/restaurants", createRequest) ~> controller.route ~> check {
      status should equal(StatusCodes.Created)

      entityAs[Restaurant] should be(restaurant)
    }
  }

  it should "return the existing restaurant" in new Wiring {
    when(service.create(any())).thenReturnAsync(Left(restaurant))

    Post(s"/restaurants", createRequest) ~> controller.route ~> check {
      status should equal(StatusCodes.Conflict)

      entityAs[Restaurant] should be(restaurant)
    }
  }

  "GET /restaurants/{id}" should "get the restaurant" in new Wiring {
    when(service.find(id)).thenReturnAsync(Option(restaurant))

    Get(s"/restaurants/$id") ~> controller.route ~> check {
      status should equal(StatusCodes.OK)

      entityAs[Restaurant] should be(restaurant)
    }
  }

  it should "return nothing when the restaurant is not found" in new Wiring {
    when(service.find(id)).thenReturnAsync(None)

    Get(s"/restaurants/$id") ~> controller.route ~> check {
      status should equal(StatusCodes.NotFound)
    }
  }

  "PUT /restaurants/{id}" should "update the restaurant" in new Wiring {
    when(service.update(any(), any())).thenReturnAsync(())

    Put(s"/restaurants/$id", updateRequest) ~> controller.route ~> check {
      status should equal(StatusCodes.NoContent)

      verify(service).update(id, updateRequest)
    }
  }

  "DELETE /restaurants/{id}" should "delete the restaurant" in new Wiring {
    when(service.delete(any())).thenReturnAsync(())

    Delete(s"/restaurants/$id") ~> controller.route ~> check {
      status should equal(StatusCodes.NoContent)

      verify(service).delete(id)
    }
  }

  private trait Wiring {
    val id: UUID = UUID.randomUUID()
    val name = "restaurant #1"
    val cuisines = Seq("ukrainian", "chinese")
    private val address = "far far away"
    private val description = Some("test restaurant")
    val restaurant = Restaurant(
      id = id,
      name = name,
      cuisines = cuisines,
      address = address,
      description = description
    )
    val updateRequest = UpdateRestaurantRequest(name, cuisines, address, description)
    val createRequest = CreateRestaurantRequest(name, cuisines, address, description)

    val service: RestaurantService = mock[RestaurantService]

    val controller = new RestaurantController(service)
  }

}
