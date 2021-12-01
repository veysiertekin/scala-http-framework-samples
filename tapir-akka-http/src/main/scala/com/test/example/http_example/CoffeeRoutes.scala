package com.test.example.http_example

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.test.example.http_example.CoffeeShopActor._
import com.test.example.http_example.Implicits._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.Future

class CoffeeRoutes(actor: ActorRef[CoffeeShopActor.Command])(implicit val system: ActorSystem[_], interpreter: AkkaHttpServerInterpreter) {
  private implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("sample-http-app.routes.ask-timeout"))

  import system.executionContext

  def getTypes: Unit => Future[Array[String]] =
    _ => actor askWithStatus GetTypes

  def getDrinks: String => Future[Array[Drink]] = coffeeType =>
    actor askWithStatus { replyTo =>
      GetDrinks(coffeeType, replyTo)
    }

  val routes: Route = {
    import akka.http.scaladsl.server.Directives._

    interpreter.toRoute(CoffeeEndpoints.getTypesEndpoint.serverLogic(getTypes.toEither)) ~
      interpreter.toRoute(CoffeeEndpoints.getDrinksEndpoint.serverLogic(getDrinks.toEither))
  }
}
