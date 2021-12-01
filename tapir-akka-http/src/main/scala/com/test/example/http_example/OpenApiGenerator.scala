package com.test.example.http_example

import java.nio.file.{Files, Path}

object OpenApiGenerator extends App {
  val path = Path.of(args.head)
  val yaml = OpenAPIDocumentation.generateOpenApiYaml(CoffeeEndpoints.endpoints)
  Files.write(path, yaml.getBytes)
}
