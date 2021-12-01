package com.test.example.finatra

import cats.implicits._
import com.twitter.util._

trait FutureImplicits {
  implicit class Extensions[I, O](f: I => Future[O]) {
    def toEither: I => Future[Either[String, O]] = { input =>
      f(input).transform {
        case Return(data) => Future.value(data.asRight[String])
        case Throw(e) => Future.value(e.getMessage.asLeft[O])
      }
    }
  }
}
