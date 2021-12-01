package com.test.example.http_example

import com.test.example.http_example.CoffeeShopActor._
import io.circe.generic.auto._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.{endpoint, _}

object CoffeeEndpoints {
  val baseEndpoint: PublicEndpoint[Unit, String, Unit, Any] =
    endpoint.tags(List("coffee-shop")).errorOut(stringBody).in("coffee")

  val getTypesEndpoint: PublicEndpoint[Unit, String, Array[String], Any] = baseEndpoint
    .name("List coffee types")
    .summary("List all coffee types in the shop.")
    .out(jsonBody[Array[String]].description("Coffee types"))

  val getDrinksEndpoint: PublicEndpoint[String, String, Array[Drink], Any] = baseEndpoint
    .name("List coffee types")
    .summary("List all coffee types in the shop.")
    .in(path[String](name = "coffeeType").description("Coffee type"))
    .out(jsonBody[Array[Drink]].description("Drinks in the type category"))

  val endpoints = List(getTypesEndpoint, getDrinksEndpoint)
}
