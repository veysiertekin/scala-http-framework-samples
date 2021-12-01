package com.test.example.http_example

import com.test.http_example.BuildInfo
import sttp.tapir.Endpoint
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.redoc.bundle.RedocInterpreter
import sttp.tapir.server.ServerEndpoint

object OpenAPIDocumentation {
  def generateDocsEndpoint[F[_]](endpoint: List[Endpoint[_, _, _, _, _]]): List[ServerEndpoint[Any, F]] =
    RedocInterpreter().fromEndpoints[F](endpoint, BuildInfo.name, BuildInfo.version)

  def generateOpenApiYaml(endpoints: List[Endpoint[_, _, _, _, _]]): String =
    OpenAPIDocsInterpreter().toOpenAPI(endpoints, BuildInfo.name, BuildInfo.version).toYaml
}
