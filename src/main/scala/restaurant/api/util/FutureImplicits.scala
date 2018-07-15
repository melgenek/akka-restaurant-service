package restaurant.api.util

import org.mongodb.scala.SingleObservable

import scala.concurrent.{Future, Promise}
import scala.language.implicitConversions

object FutureImplicits {

  implicit def observableToFuture[T](observable: SingleObservable[T]): Future[T] = {
    val p: Promise[T] = Promise[T]
    observable.headOption()

    p.future
  }

}
