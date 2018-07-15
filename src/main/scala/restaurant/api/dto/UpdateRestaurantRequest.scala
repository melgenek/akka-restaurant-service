package restaurant.api.dto

case class UpdateRestaurantRequest(name: String,
                                   cuisines: Seq[String],
                                   address: String,
                                   description: Option[String])
