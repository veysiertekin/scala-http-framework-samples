package com.test.example.http4s

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    Http4sServer.stream.compile.drain.as(ExitCode.Success)
}
