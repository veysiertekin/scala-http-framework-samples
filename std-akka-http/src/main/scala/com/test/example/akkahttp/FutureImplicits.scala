package com.test.example.akkahttp

import akka.actor.typed.ActorRef
import akka.pattern.StatusReply
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object FutureImplicits {
  implicit class ActorExtensions[T](future: Future[T]) {
    def pipeWithStatusTo(replyTo: ActorRef[StatusReply[T]])(implicit ec: ExecutionContext): Unit = {
      future.onComplete {
        case Success(data) => replyTo ! StatusReply.success(data)
        case Failure(e) => replyTo ! StatusReply.error(e)
      }
    }
  }
}
