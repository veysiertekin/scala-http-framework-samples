package com.test.example.finatra

import sttp.tapir.Endpoint
import sttp.tapir.redoc.bundle.RedocInterpreter
import sttp.tapir.server.ServerEndpoint

object OpenAPIDocumentation {
  def generateDocsEndpoint[F[_]](endpoint: List[Endpoint[_, _, _, _, _]]): List[ServerEndpoint[Any, F]] = {
    RedocInterpreter().fromEndpoints[F](endpoint, "finatra tapir example", "1.0")
  }
}
