package com.test.example.zio

import sttp.capabilities.zio.ZioStreams
import sttp.tapir.Endpoint
import sttp.tapir.generic.auto._
import sttp.tapir.json.zio._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir._
import zhttp.http._
import zio._

object CoffeeShopEndpoints {
  val getTypesLogic: Unit => ZIO[Has[CoffeeShopService], String, Array[String]] = _ => CoffeeShopService.getTypes.mapError(_.getMessage)
  val getDrinksLogic: String => ZIO[Has[CoffeeShopService], String, Array[Drink]] = coffeeType => CoffeeShopService.getDrinks(coffeeType).mapError(_.getMessage)

  val baseEndpoint: Endpoint[Unit, String, Unit, Any] =
    endpoint.tags(List("coffee-shop")).errorOut(stringBody).in("coffee")

  val getTypesEndpoint: ZEndpoint[Unit, String, Array[String]] = baseEndpoint
    .name("List coffee types")
    .summary("List all coffee types in the shop.")
    .out(jsonBody[Array[String]].description("Coffee types"))

  val getDrinksEndpoint: ZEndpoint[String, String, Array[Drink]] = baseEndpoint
    .name("List coffee types")
    .summary("List all coffee types in the shop.")
    .in(path[String](name = "coffeeType").description("Coffee type"))
    .out(jsonBody[Array[Drink]].description("Drinks in the type category"))

  type EffectType[A] = RIO[Has[CoffeeShopService], A]

  val getTypesServerEndpoint: ZServerEndpoint[Has[CoffeeShopService], Unit, String, Array[String]] = getTypesEndpoint.zServerLogic[Has[CoffeeShopService]](getTypesLogic)
  val getDrinksServerEndpoint: ZServerEndpoint[Has[CoffeeShopService], String, String, Array[Drink]] = getDrinksEndpoint.zServerLogic[Has[CoffeeShopService]](getDrinksLogic)

  //noinspection TypeAnnotation
  val allEndpoints =
    List(getTypesServerEndpoint, getDrinksServerEndpoint)
      .asInstanceOf[List[ServerEndpoint[_, _, _, ZioStreams, EffectType]]]

  val httpApp: Http[Has[CoffeeShopService], Throwable, Request, Response[Has[CoffeeShopService], Throwable]] =
    ZioHttpInterpreter().toHttp(allEndpoints)
}
