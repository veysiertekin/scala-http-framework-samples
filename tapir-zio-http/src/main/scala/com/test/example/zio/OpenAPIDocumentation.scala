package com.test.example.zio

import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.redoc.Redoc
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zhttp.http.{Http, Request, Response}
import zio.Task

object OpenAPIDocumentation {
  val openApi: OpenAPI = OpenAPIDocsInterpreter()
    .serverEndpointsToOpenAPI(CoffeeShopEndpoints.allEndpoints, "zio-http tapir example", "0.1")

  val docsApp: Http[Any, Throwable, Request, Response[Any, Throwable]] =
    ZioHttpInterpreter().toHttp(Redoc[Task](title = "Coffee Shop API", yaml = openApi.toYaml))
}
