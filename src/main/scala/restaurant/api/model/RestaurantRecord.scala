package restaurant.api.model

import java.util.UUID

case class RestaurantRecord(_id: UUID = UUID.randomUUID(),
                            name: String,
                            cuisines: Seq[String],
                            address: String,
                            description: Option[String] = None)
