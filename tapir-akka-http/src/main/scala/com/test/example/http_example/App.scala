package com.test.example.http_example

import akka.actor.CoordinatedShutdown
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object App {
  implicit val interpreter: AkkaHttpServerInterpreter = AkkaHttpServerInterpreter()

  case object UserInitiatedShutdown extends CoordinatedShutdown.Reason

  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext

    val futureBinding = Http()
      .newServerAt("0.0.0.0", 9000)
      .bind(routes)
      .map(_.addToCoordinatedShutdown(30.seconds))

    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>

      val coffeeShopActor = context.spawn(CoffeeShopActor(), "CoffeeShopActor")
      context.watch(coffeeShopActor)

      val coffeeRoutes = new CoffeeRoutes(coffeeShopActor)(context.system, interpreter)
      val openApiEndpoint = OpenAPIDocumentation.generateDocsEndpoint[Future](CoffeeEndpoints.endpoints)

      startHttpServer(coffeeRoutes.routes ~ interpreter.toRoute(openApiEndpoint))(context.system)
      Behaviors.empty
    }

    val system = ActorSystem[Nothing](rootBehavior, "SampleServer")
    //CoordinatedShutdown(system).run(UserInitiatedShutdown)
  }
}
