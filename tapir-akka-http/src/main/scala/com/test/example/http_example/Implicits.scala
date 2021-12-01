package com.test.example.http_example

import akka.actor.typed.ActorRef
import akka.pattern.StatusReply
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Implicits {
  implicit class ActorExtensions[T](future: Future[T]) {
    def pipeWithStatusTo(replyTo: ActorRef[StatusReply[T]])(implicit ec: ExecutionContext): Unit = {
      future.onComplete {
        case Success(data) => replyTo ! StatusReply.success(data)
        case Failure(e) => replyTo ! StatusReply.error(e)
      }
    }
  }

  implicit class FunctionImplicits[I, O](f: I => Future[O]) {
    def toEither(implicit ec: ExecutionContext): I => Future[Either[String, O]] = { input =>
      f(input).transformWith {
        case Success(data) => Future.successful(Right(data))
        case Failure(e) => Future.successful(Left(e.getMessage))
      }
    }
  }
}
