package com.test.example.akkahttp

import akka.actor.CoordinatedShutdown
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import scala.util.{Failure, Success}

object App {
  case object UserInitiatedShutdown extends CoordinatedShutdown.Reason

  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext

    val futureBinding = Http()
      .newServerAt("0.0.0.0", 9000)
      .bind(routes)

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

      val routes = new CoffeeRoutes(coffeeShopActor)(context.system)
      startHttpServer(routes.routes)(context.system)
      Behaviors.empty
    }

    val system = ActorSystem[Nothing](rootBehavior, "SampleServer")
    //CoordinatedShutdown(system).run(UserInitiatedShutdown)
  }
}
