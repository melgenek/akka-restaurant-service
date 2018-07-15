package restaurant.api.dao

import java.util.UUID

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase, _}
import restaurant.api.config.MongoConfig
import restaurant.api.model.RestaurantRecord

import scala.concurrent.{ExecutionContext, Future}


object RestaurantDao {

  case class SearchCriteria(name: String, address: String)

}

trait RestaurantDao {

  def find(id: UUID): Future[Option[RestaurantRecord]]

  def findByNameAndAddress(name: String, address: String): Future[Option[RestaurantRecord]]

  def list(): Future[Seq[RestaurantRecord]]

  def create(record: RestaurantRecord): Future[Unit]

  def update(record: RestaurantRecord): Future[Unit]

  def delete(id: UUID): Future[Unit]

}

class MongoRestaurantDao(config: MongoConfig)
                        (implicit ec: ExecutionContext) extends RestaurantDao {

  private val codecRegistry =
    fromRegistries(fromProviders(classOf[RestaurantRecord]), DEFAULT_CODEC_REGISTRY)
  private val client = MongoClient(s"mongodb://${config.host}:${config.port}")
  private val db: MongoDatabase = client.getDatabase(config.db).withCodecRegistry(codecRegistry)
  private val collection: MongoCollection[RestaurantRecord] = db.getCollection("restaurants")

  override def find(id: UUID): Future[Option[RestaurantRecord]] =
    collection.find(equal("_id", id)).headOption()

  override def findByNameAndAddress(name: String, address: String): Future[Option[RestaurantRecord]] =
    collection.find(and(
      equal("name", name),
      equal("address", address))
    ).headOption()

  override def list(): Future[Seq[RestaurantRecord]] =
    collection.find().toFuture()

  override def create(record: RestaurantRecord): Future[Unit] =
    collection.insertOne(record).toFuture().map(_ => ())

  override def update(record: RestaurantRecord): Future[Unit] =
    collection
      .replaceOne(equal("_id", record._id), record)
      .toFuture().map(_ => ())

  override def delete(id: UUID): Future[Unit] =
    collection.deleteOne(equal("_id", id)).toFuture().map(_ => ())

}
