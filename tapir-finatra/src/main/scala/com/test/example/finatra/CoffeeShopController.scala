package com.test.example.finatra

import com.test.example.finatra.CoffeeShopEndpoints._
import com.twitter.finatra.http.Controller
import com.twitter.util.Future
import javax.inject.{Inject, Singleton}
import sttp.tapir.server.finatra.{FinatraServerInterpreter, TapirController}

@Singleton
class CoffeeShopController @Inject()(coffeeShopService: CoffeeShopService)
  extends Controller
    with TapirController
    with FutureImplicits {
  val interpreter: FinatraServerInterpreter = FinatraServerInterpreter()

  addTapirRoute(interpreter.toRoute(getTypesEndpoint.serverLogic(coffeeShopService.getTypes.toEither)))
  addTapirRoute(interpreter.toRoute(getDrinksEndpoint.serverLogic(coffeeShopService.getDrinks.toEither)))

  OpenAPIDocumentation.generateDocsEndpoint[Future](List(getTypesEndpoint, getDrinksEndpoint))
    .foreach(endpoint => addTapirRoute(interpreter.toRoute(endpoint)))
}
