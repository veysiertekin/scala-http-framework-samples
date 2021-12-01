package com.test.example.zio

import zhttp.http.{Http, HttpError, Request, UResponse}
import zhttp.service.Server
import zio._
import zio.magic._

object App extends zio.App {
  val endpoints: Http[Has[CoffeeShopService], HttpError, Request, UResponse] = CoffeeShopEndpoints.all

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    Server.start(9000, endpoints.silent)
      .inject(CoffeeShopService.layer)
      .exitCode
  }
}
