package com.test.example.akkahttp

import akka.actor.ClassicActorSystemProvider
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpRequest, ResponseEntity}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.pattern.StatusReply
import com.test.example.akkahttp.FutureImplicits.ActorExtensions
import scala.concurrent.{ExecutionContext, Future}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}


trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport

object CoffeeShopActor extends JsonSupport {
  private val endpoint = "https://veysiertekin.github.io/coffee-shop/drinks/"

  implicit val coffeeFormat: RootJsonFormat[Drink] = jsonFormat4(Drink)

  sealed trait Command

  final case class GetTypes(replyTo: ActorRef[StatusReply[Array[String]]]) extends Command

  final case class GetDrinks(`type`: String, replyTo: ActorRef[StatusReply[Array[Drink]]]) extends Command

  final case class Drink(title: String, id: Int, description: String, ingredients: List[String])

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      import context.{executionContext, system}

      Behaviors.receiveMessage {
        case GetTypes(replyTo) =>
          val request = Get(endpoint)

          val response = invokeRequest[Array[String]](request)
          response.pipeWithStatusTo(replyTo)

          Behaviors.same
        case GetDrinks(coffeeType, replyTo) =>
          val request = Get(s"$endpoint/$coffeeType/")

          val response = invokeRequest[Array[Drink]](request)
          response.pipeWithStatusTo(replyTo)

          Behaviors.same
      }
    }

  type ResponseUnmarshaller[T] = Unmarshaller[ResponseEntity, T]

  private def invokeRequest[T: ResponseUnmarshaller]
  (request: HttpRequest)
  (implicit ec: ExecutionContext, sys: ClassicActorSystemProvider): Future[T] =
    for {
      response <- Http().singleRequest(request)
      data <- Unmarshal(response.entity).to[T]
    } yield data

}
