package com.test.example.http4s

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Implicits {
  implicit class FunctionImplicits[I, O](f: I => Future[O]) {
    def toEither(implicit ec: ExecutionContext): I => Future[Either[String, O]] = { input =>
      f(input).transformWith {
        case Success(data) => Future.successful(Right(data))
        case Failure(e) => Future.successful(Left(e.getMessage))
      }
    }
  }
}
