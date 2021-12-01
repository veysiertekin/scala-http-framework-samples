package com.test.example.akkahttp

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.test.example.akkahttp.CoffeeShopActor._
import scala.concurrent.Future

class CoffeeRoutes(actor: ActorRef[CoffeeShopActor.Command])(implicit val system: ActorSystem[_]) extends JsonSupport {
  private implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("sample-http-app.routes.ask-timeout"))

  def getTypes: Future[Array[String]] = actor askWithStatus GetTypes

  def getDrinks(coffeeType: String): Future[Array[Drink]] = actor askWithStatus { replyTo =>
    GetDrinks(coffeeType, replyTo)
  }

  val routes: Route =
    pathPrefix("coffee") {
      concat(
        pathEnd {
          get {
            onSuccess(getTypes) { types =>
              complete(StatusCodes.OK, types)
            }
          }
        },
        path(Segment) { coffeeType =>
          onSuccess(getDrinks(coffeeType)) { types =>
            complete(StatusCodes.OK, types)
          }
        }
      )
    }
}
