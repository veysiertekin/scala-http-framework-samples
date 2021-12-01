package com.test.example.http4s

import cats.effect.IO
import cats.implicits._
import com.test.example.http4s.CoffeeShop.Drink
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter

object Http4sRoutes {
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

  def coffeeRoutes(C: CoffeeShop[IO]): HttpRoutes[IO] = {
    val interpreter = Http4sServerInterpreter[IO]()
    interpreter.toRoutes(getTypesEndpoint.serverLogic(_ => toEither(C.types))) <+>
      interpreter.toRoutes(getDrinksEndpoint.serverLogic(coffeeType => toEither(C.drinks(coffeeType)))) <+>
      interpreter.toRoutes(OpenAPIDocumentation.generateDocsEndpoint[IO](List(getTypesEndpoint, getDrinksEndpoint)))
  }

  private def toEither[T]: IO[T] => IO[Either[String, T]] =
    _.map(_.asRight[String])
      .recoverWith {
        case e: RuntimeException => IO.pure(e.getMessage.asLeft[T])
      }
}
