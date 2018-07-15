package restaurant.api.util

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

object IntegrationSpecContext {

  val context: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(16))

}

trait IntegrationSpecContext {

  implicit val context: ExecutionContext = IntegrationSpecContext.context

}
