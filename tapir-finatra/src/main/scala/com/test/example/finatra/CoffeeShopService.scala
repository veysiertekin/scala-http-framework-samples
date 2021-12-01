package com.test.example.finatra

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.finatra.http.marshalling.DefaultMessageBodyReader
import com.twitter.util.Future

trait CoffeeShopService {
  def getTypes: Unit => Future[Array[String]]

  def getDrinks: String => Future[Array[Drink]]
}

class CoffeeShopServiceImpl(reader: DefaultMessageBodyReader) extends CoffeeShopService {
  val githubPages: Service[Request, Response] = Http.client
    .withTransport.tls("veysiertekin.github.io")
    .newService("veysiertekin.github.io:443")

  override def getTypes: Unit => Future[Array[String]] = _ =>
    githubPages(Request("/coffee-shop/drinks/"))
      .map(result =>
        reader.parse[Array[String]](result)
      )

  override def getDrinks: String => Future[Array[Drink]] = coffeeType =>
    githubPages(Request(s"/coffee-shop/drinks/$coffeeType/"))
      .map(result =>
        reader.parse[Array[Drink]](result)
      )
}
