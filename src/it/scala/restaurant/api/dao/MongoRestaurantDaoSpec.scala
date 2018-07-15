package restaurant.api.dao

import java.util.UUID

import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}
import restaurant.api.config.MongoConfig
import restaurant.api.model.RestaurantRecord
import restaurant.api.util.IntegrationSpecContext

import scala.concurrent.duration._

class MongoRestaurantDaoSpec extends FlatSpec
  with ScalaFutures with Matchers with ForAllTestContainer with IntegrationSpecContext {

  override val container = GenericContainer("mongo:4", exposedPorts = Seq(27017))

  "dao" should "create and get the record" in new Wiring {
    dao.create(record).futureValue

    dao.find(record._id).futureValue should be(Some(record))
  }

  it should "delete the saved record" in new Wiring {
    dao.create(record).futureValue

    dao.delete(record._id)

    dao.find(record._id).futureValue should be(None)
  }

  it should "update the saved record" in new Wiring {
    dao.create(record).futureValue

    val updatedRecord: RestaurantRecord = record.copy(name = "changed_name")
    dao.update(updatedRecord)

    dao.find(record._id).futureValue should be(Some(updatedRecord))
  }

  it should "not update then record when create is called multiple times" in new Wiring {
    dao.create(record).futureValue

    val updatedRecord: RestaurantRecord = record.copy(name = "changed_name")
    dao.create(updatedRecord)

    dao.find(record._id).futureValue should be(Some(record))
  }

  it should "list all the records" in new Wiring {
    override val config: MongoConfig = super.config.copy(db = "list_test_db")

    dao.create(record).futureValue
    dao.create(record.copy(_id = UUID.randomUUID())).futureValue
    dao.create(record.copy(_id = UUID.randomUUID())).futureValue

    dao.list().futureValue.size should be(3)
  }

  it should "find the record by name and address" in new Wiring {
    val recordToFind: RestaurantRecord = record.copy(name = UUID.randomUUID().toString, address = UUID.randomUUID().toString)

    dao.create(recordToFind).futureValue

    dao.findByNameAndAddress(recordToFind.name, recordToFind.address).futureValue should be(Some(recordToFind))
  }

  private trait Wiring {
    val record = RestaurantRecord(
      name = "restaurant #1",
      cuisines = Seq("ukrainian", "chinese"),
      address = "far far away",
      description = Some("test restaurant")
    )

    def config = MongoConfig(
      container.containerIpAddress,
      container.container.getMappedPort(27017),
      "test"
    )

    lazy val dao = new MongoRestaurantDao(config)
  }

  override implicit def patienceConfig: PatienceConfig = PatienceConfig(3.seconds, 200.millis)

}
