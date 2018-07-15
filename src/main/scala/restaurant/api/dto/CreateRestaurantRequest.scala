package restaurant.api.dto

case class CreateRestaurantRequest(name: String,
                                   cuisines: Seq[String],
                                   address: String,
                                   description: Option[String])
