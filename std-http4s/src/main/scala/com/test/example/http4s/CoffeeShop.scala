package com.test.example.http4s

import cats.effect.Concurrent
import cats.implicits._
import com.test.example.http4s.CoffeeShop.Drink
import io.circe.generic.auto._
import org.http4s.Method._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl

trait CoffeeShop[F[_]] {
  def types: F[List[String]]

  def drinks(coffeeType: String): F[List[Drink]]
}

object CoffeeShop {
  val endpoint = "https://veysiertekin.github.io/coffee-shop/drinks/"

  def apply[F[_]](implicit ev: CoffeeShop[F]): CoffeeShop[F] = ev

  final case class Drink(title: String, id: Int, description: String, ingredients: List[String])

  object Drink {
    implicit def drinkEntityDecoder[F[_] : Concurrent]: EntityDecoder[F, Drink] = jsonOf

    implicit def drinkEntityEncoder[F[_]]: EntityEncoder[F, Drink] = jsonEncoderOf
  }

  final case class CoffeeShopError(e: Throwable) extends RuntimeException

  def impl[F[_] : Concurrent](C: Client[F]): CoffeeShop[F] = new CoffeeShop[F] {
    val dsl: Http4sClientDsl[F] = new Http4sClientDsl[F] {}

    import dsl._

    def types: F[List[String]] = {
      C.expect[List[String]](GET(Uri.unsafeFromString(endpoint)))
        .adaptError { case t => CoffeeShopError(t) }
    }

    def drinks(coffeeType: String): F[List[Drink]] = {
      C.expect[List[Drink]](GET(Uri.unsafeFromString(s"$endpoint/$coffeeType/")))
        .adaptError { case t => CoffeeShopError(t) }
    }
  }
}
