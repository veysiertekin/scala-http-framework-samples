package com.test.example.http4s

import cats.effect.{Async, IO, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object Http4sServer {
  def stream: Stream[IO, Nothing] = {
    for {
      client <- Stream.resource(EmberClientBuilder.default[IO].build)
      alg = CoffeeShop.impl(client)

      httpApp = Http4sRoutes.coffeeRoutes(alg).orNotFound
      finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

      exitCode <- Stream.resource(
        EmberServerBuilder.default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"9000")
          .withHttpApp(finalHttpApp)
          .build >>
          Resource.eval(Async[IO].never)
      )
    } yield exitCode
  }.drain
}
