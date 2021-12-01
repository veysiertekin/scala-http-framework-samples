package com.test.example.zio

import zhttp.http._
import zio.Has
import zio.json._

object CoffeeShopEndpoints {
  val all: Http[Has[CoffeeShopService], HttpError, Request, UResponse] = Http.collectM[Request] {
    case Method.GET -> Root / "coffee" =>
      for {
        types <- CoffeeShopService.getTypes
      } yield Response.jsonString(types.toJson)
    case Method.GET -> Root / "coffee" / coffeeType =>
      for {
        drinks <- CoffeeShopService.getDrinks(coffeeType)
      } yield Response.jsonString(drinks.toJson)
  }.catchAll(err => Http.fail(HttpError.InternalServerError(msg = err.toString)))
}
