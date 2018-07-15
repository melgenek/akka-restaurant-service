package restaurant.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.{Config, ConfigFactory}
import restaurant.api.config.MongoConfig
import restaurant.api.controller.{HealthController, RestaurantController}
import restaurant.api.dao.MongoRestaurantDao
import restaurant.api.service.RestaurantServiceImpl
import restaurant.api.util.AbstractController

import scala.concurrent.ExecutionContextExecutor

trait Wiring {

  val config: Config = ConfigFactory.load()

  implicit val actorSystem: ActorSystem = ActorSystem("api", config)

  implicit val materializer: Materializer = ActorMaterializer()(actorSystem)

  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

  lazy val mongoConfig = MongoConfig(
    config.getString("mongo.host"),
    config.getInt("mongo.port"),
    config.getString("mongo.db")
  )
  lazy val dao = new MongoRestaurantDao(mongoConfig)

  lazy val service = new RestaurantServiceImpl(dao)

  lazy val controllers: Set[AbstractController] = Set(
    new RestaurantController(service),
    new HealthController
  )

  lazy val routes: Route = controllers.foldLeft[Route](reject)(_ ~ _.route)

}
