package restaurant.api.dto

import java.util.UUID

case class Restaurant(id: UUID,
                      name: String,
                      cuisines: Seq[String],
                      address: String,
                      description: Option[String])
