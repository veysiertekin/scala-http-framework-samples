package com.test.example.zio

import zhttp.http.HttpData.CompleteData
import zhttp.http._
import zhttp.service.{ChannelFactory, Client, EventLoopGroup}
import zio._
import zio.json.JsonCodec._
import zio.json.{DecoderOps, DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

final case class Drink(title: String, id: Int, description: String, ingredients: List[String])

object Drink {
  implicit val decoder: JsonDecoder[Drink] = DeriveJsonDecoder.gen[Drink]
  implicit val encoder: JsonEncoder[Drink] = DeriveJsonEncoder.gen[Drink]
}

trait CoffeeShopService {
  def getTypes: IO[Throwable, Array[String]]

  def getDrinks(coffeeType: String): IO[Throwable, Array[Drink]]
}

object CoffeeShopService {
  def getTypes: ZIO[Has[CoffeeShopService], Throwable, Array[String]] =
    ZIO.serviceWith[CoffeeShopService](_.getTypes)

  def getDrinks(coffeeType: String): ZIO[Has[CoffeeShopService], Throwable, Array[Drink]] =
    ZIO.serviceWith[CoffeeShopService](_.getDrinks(coffeeType))

  def layer: URLayer[Any, Has[CoffeeShopService]] = CoffeeShopLive.toLayer
}

case class CoffeeShopLive() extends CoffeeShopService {
  private val hostHeader: Header = Header.host("protected-dawn-85371.herokuapp.com")
  private val endpoint = "http://protected-dawn-85371.herokuapp.com/coffee-shop/drinks"

  private val env = ChannelFactory.auto ++ EventLoopGroup.auto()

  override def getTypes: IO[Throwable, Array[String]] =
    invokeGetRequest[Array[String]](endpoint)

  override def getDrinks(coffeeType: String): IO[Throwable, Array[Drink]] =
    invokeGetRequest[Array[Drink]](s"$endpoint/$coffeeType")

  private def invokeGetRequest[T: JsonDecoder](path: String): IO[Throwable, T] = {
    for {
      url <- ZIO.fromEither(URL.fromString(path))
      req = Request(Method.GET -> url, headers = List(hostHeader))
      response <- Client.request(req).provideLayer(env)
      types <- ZIO.fromEither {
        response.content match {
          case CompleteData(data) => mapToThrowable(data.map(_.toChar).mkString.fromJson[T])
          case _ => Left(new Exception("unexpected response"))
        }
      }
    } yield types
  }

  private def mapToThrowable[T]: Either[String, T] => Either[Throwable, T] = {
    case Right(result) => Right(result)
    case Left(message) => Left(new Exception(message))
  }
}
