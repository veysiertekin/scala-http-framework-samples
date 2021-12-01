package com.test.example.finatra

import com.google.inject.Provides
import com.twitter.finatra.http.marshalling.DefaultMessageBodyReader
import com.twitter.inject.TwitterModule
import javax.inject.Singleton

object CoffeeShopModule extends TwitterModule {
  @Provides
  @Singleton
  def coffeeShopService(reader: DefaultMessageBodyReader): CoffeeShopService =
    new CoffeeShopServiceImpl(reader)
}
