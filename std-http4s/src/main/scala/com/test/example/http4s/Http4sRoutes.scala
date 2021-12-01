package com.test.example.http4s

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

object Http4sRoutes {

  def coffeeRoutes[F[_] : Sync](C: CoffeeShop[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "coffee" =>
        for {
          drinkTypes <- C.types
          resp <- Ok(drinkTypes)
        } yield resp
      case GET -> Root / "coffee" / coffeeType =>
        for {
          drinks <- C.drinks(coffeeType)
          resp <- Ok(drinks)
        } yield resp
    }
  }
}
