package restaurant.api.service

import java.util.UUID

import org.mockito.Matchers.any
import org.mockito.Mockito.{never, verify, when}
import restaurant.api.dao.RestaurantDao
import restaurant.api.dto.{CreateRestaurantRequest, Restaurant, UpdateRestaurantRequest}
import restaurant.api.model.RestaurantRecord
import restaurant.api.util.{BaseSpec, SpecContext}

class RestaurantServiceSpec extends BaseSpec with SpecContext {

  "find" should "delegate to dao" in new Wiring {
    when(dao.find(id)).thenReturnAsync(Option(record))

    val res: Option[Restaurant] = service.find(id).futureValue

    res should be(Some(restaurant))
  }

  "list" should "delegate to dao" in new Wiring {
    when(dao.list()).thenReturnAsync(Seq(record))

    val res: Seq[Restaurant] = service.list().futureValue

    res should be(Seq(restaurant))
  }

  "delete" should "delegate to dao" in new Wiring {
    when(dao.delete(any())).thenReturnAsync(())

    service.delete(id).futureValue

    verify(dao).delete(id)
  }

  "update" should "delegate to dao" in new Wiring {
    when(dao.update(any())).thenReturnAsync(())
    val updateRequest = UpdateRestaurantRequest(name, cuisines, address, description)

    service.update(id, updateRequest).futureValue

    verify(dao).update(record)
  }

  "create" should "create new record when no exist" in new Wiring {
    when(dao.create(any())).thenReturnAsync(())
    when(dao.findByNameAndAddress(any(), any())).thenReturnAsync(None)

    val res: Restaurant = service.create(createRequest).futureValue.right.get

    res.id should not be null
    res.name should be(name)
    res.cuisines should be(cuisines)
    res.address should be(address)
    res.description should be(description)
    verify(dao).create(any())
  }

  it should "return existing record when exists" in new Wiring {
    when(dao.findByNameAndAddress(any(), any())).thenReturnAsync(Option(record))

    val res: Restaurant = service.create(createRequest).futureValue.left.get

    res should be(restaurant)
    verify(dao, never()).create(any())
  }

  private trait Wiring {
    val id: UUID = UUID.randomUUID()
    val name = "restaurant #1"
    val cuisines = Seq("ukrainian", "chinese")
    val address = "far far away"
    val description = Some("test restaurant")
    val record = RestaurantRecord(_id = id, name = name, cuisines = cuisines, address = address, description = description)
    val restaurant = Restaurant(id = id, name = name, cuisines = cuisines, address = address, description = description)
    val createRequest = CreateRestaurantRequest(name, cuisines, address, description)

    val dao: RestaurantDao = mock[RestaurantDao]

    val service = new RestaurantServiceImpl(dao)
  }


}
