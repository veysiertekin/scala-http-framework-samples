package com.test.example.finatra

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import javax.inject.{Inject, Singleton}

@Singleton
class CoffeeShopController @Inject()(coffeeShopService: CoffeeShopService) extends Controller {
  get("/coffee") { request: Request =>
    coffeeShopService.getTypes
      .map(response.ok.body)
  }

  get("/coffee/:type") { request: GetDrinksRequest =>
    coffeeShopService.getDrinks(request.`type`)
      .map(response.ok.body)
  }
}
