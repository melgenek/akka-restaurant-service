package restaurant.api.service

import java.util.UUID

import com.typesafe.scalalogging.StrictLogging
import io.scalaland.chimney.dsl._
import restaurant.api.dao.RestaurantDao
import restaurant.api.dto.{CreateRestaurantRequest, Restaurant, UpdateRestaurantRequest}
import restaurant.api.model.RestaurantRecord

import scala.concurrent.{ExecutionContext, Future}

trait RestaurantService {

  def find(id: UUID): Future[Option[Restaurant]]

  def list(): Future[Seq[Restaurant]]

  def create(request: CreateRestaurantRequest): Future[Either[Restaurant, Restaurant]]

  def update(id: UUID, request: UpdateRestaurantRequest): Future[Unit]

  def delete(id: UUID): Future[Unit]

}

class RestaurantServiceImpl(dao: RestaurantDao)
                           (implicit ec: ExecutionContext) extends RestaurantService with StrictLogging {

  def find(id: UUID): Future[Option[Restaurant]] = dao.find(id).map { restaurantOpt =>
    restaurantOpt.map(restaurantRecordToDto)
  }

  def list(): Future[Seq[Restaurant]] = dao.list().map { restaurants =>
    restaurants.map(restaurantRecordToDto)
  }

  def create(request: CreateRestaurantRequest): Future[Either[Restaurant, Restaurant]] =
    for {
      existingRecordOpt <- dao.findByNameAndAddress(request.name, request.address)
      existingRestaurantOpt = existingRecordOpt.map { existing =>
        logger.debug(s"Found an existing restaurant with name ${existing.name} and id ${existing._id}")
        restaurantRecordToDto(existing)
      }.map(Left[Restaurant, Restaurant]).map(Future.successful)
      result <- existingRestaurantOpt.getOrElse {
        createNewRecord(request).map(Right[Restaurant, Restaurant])
      }
    } yield result

  private def createNewRecord(request: CreateRestaurantRequest): Future[Restaurant] = {
    val recordToCreate: RestaurantRecord = request.into[RestaurantRecord]
      .withFieldConst(_._id, UUID.randomUUID())
      .transform
    logger.debug(s"Creating a new restaurant with name ${recordToCreate.name} and id ${recordToCreate._id}")

    dao.create(recordToCreate).map(_ => restaurantRecordToDto(recordToCreate))
  }

  def update(id: UUID, request: UpdateRestaurantRequest): Future[Unit] = {
    logger.debug(s"Updating the restaurant with id $id")
    val updatedRecord: RestaurantRecord = request.into[RestaurantRecord]
      .withFieldConst(_._id, id)
      .transform

    dao.update(updatedRecord)
  }

  def delete(id: UUID): Future[Unit] = {
    logger.debug(s"Deleting the restaurant with id $id")
    dao.delete(id)
  }

  private def restaurantRecordToDto(record: RestaurantRecord): Restaurant =
    record.into[Restaurant].withFieldRenamed(_._id, _.id).transform

}
