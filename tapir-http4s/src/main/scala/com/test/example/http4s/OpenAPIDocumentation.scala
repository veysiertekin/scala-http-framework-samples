package com.test.example.http4s

import sttp.tapir.Endpoint
import sttp.tapir.redoc.bundle.RedocInterpreter
import sttp.tapir.server.ServerEndpoint

object OpenAPIDocumentation {
  def generateDocsEndpoint[F[_]](endpoint: List[Endpoint[_, _, _, _, _]]): List[ServerEndpoint[Any, F]] = {
    RedocInterpreter().fromEndpoints[F](endpoint, "http4s tapir example", "1.0")
  }
}
