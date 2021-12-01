package com.test.example.finatra

import com.twitter.finatra.http.annotations.RouteParam

final case class GetDrinksRequest(@RouteParam `type`: String)

final case class Drink(title: String, id: Int, description: String, ingredients: List[String])
