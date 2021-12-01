package com.test.example.zio

import zhttp.http.{Http, Request, Response}
import zhttp.service.Server
import zio._
import zio.magic._

object App extends zio.App {
  val endpoints: Http[Has[CoffeeShopService], Throwable, Request, Response[Has[CoffeeShopService], Throwable]] =
    OpenAPIDocumentation.docsApp <> CoffeeShopEndpoints.httpApp

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    Server.start(9000, endpoints)
      .inject(CoffeeShopService.layer)
      .exitCode
  }
}
