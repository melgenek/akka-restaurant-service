package restaurant.api.controller

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import com.typesafe.config.{Config, ConfigFactory}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpecLike, Matchers}
import restaurant.api.Wiring
import restaurant.api.dto.{CreateRestaurantRequest, Restaurant}

import scala.concurrent.duration._

class RestaurantControllerScenarioSpec extends FlatSpecLike with ScalatestRouteTest with FailFastCirceSupport
  with ScalaFutures with Matchers with ForAllTestContainer {

  override val container = GenericContainer("mongo:4", exposedPorts = Seq(27017))

  it should "create and delete a restaurant" in new ScenarioWiring {
    var createdRestaurant: Restaurant = _
    Post(s"/restaurants", createRequest) ~> app.routes ~> check {
      status should equal(StatusCodes.Created)

      createdRestaurant = entityAs[Restaurant]
    }

    Get(s"/restaurants/${createdRestaurant.id}") ~> app.routes ~> check {
      status should equal(StatusCodes.OK)

      entityAs[Restaurant] should be(createdRestaurant)
    }

    Delete(s"/restaurants/${createdRestaurant.id}") ~> app.routes ~> check {
      status should equal(StatusCodes.NoContent)
    }

    Get(s"/restaurants/${createdRestaurant.id}") ~> app.routes ~> check {
      status should equal(StatusCodes.NotFound)
    }
  }

  it should "create and update a restaurant" in new ScenarioWiring {
    var createdRestaurant: Restaurant = _
    Post(s"/restaurants", createRequest) ~> app.routes ~> check {
      status should equal(StatusCodes.Created)

      createdRestaurant = entityAs[Restaurant]
    }

    val newName: String = UUID.randomUUID().toString
    val updateRequest = CreateRestaurantRequest(newName, cuisines, address, description)

    Put(s"/restaurants/${createdRestaurant.id}", updateRequest) ~> app.routes ~> check {
      status should equal(StatusCodes.NoContent)
    }

    Get(s"/restaurants/${createdRestaurant.id}") ~> app.routes ~> check {
      status should equal(StatusCodes.OK)

      entityAs[Restaurant] should be(createdRestaurant.copy(name = newName))
    }
  }


  private trait ScenarioWiring {
    val name = "restaurant #1"
    val cuisines = Seq("ukrainian", "chinese")
    val address = "far far away"
    val description = Some("test restaurant")
    val createRequest = CreateRestaurantRequest(name, cuisines, address, description)

    val app: Wiring = new Wiring {
      override val config: Config = ConfigFactory.load().withFallback(
        ConfigFactory.parseString(
          s"""{
             |mongo.host = ${container.containerIpAddress}
             |mongo.port = ${container.container.getMappedPort(27017)}
             |mongo.db = test
          }""".stripMargin)
      )
    }
  }

  override implicit def patienceConfig: PatienceConfig = PatienceConfig(5.seconds, 200.millis)

}
