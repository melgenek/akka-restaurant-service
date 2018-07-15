package restaurant.api.util


import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

object SpecContext {

  val context: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(16))

}

trait SpecContext {

  implicit val context: ExecutionContext = IntegrationSpecContext.context

}
