package com.test.example.finatra

import com.google.inject
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter

object App extends HttpServer {
  override protected def modules: Seq[inject.Module] = Seq(CoffeeShopModule)

  override protected def defaultHttpPort: String = ":9000"

  override protected def configureHttp(router: HttpRouter): Unit =
    router.add[CoffeeShopController]
}
