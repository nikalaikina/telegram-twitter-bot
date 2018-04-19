package com.github.nikalaikina.twitter

import com.danielasfregola.twitter4s.entities.streaming.UserStreamingMessage
import com.danielasfregola.twitter4s.{TwitterRestClient, TwitterStreamingClient}

import scala.util.Failure


object Twitter extends App {
  import scala.concurrent.ExecutionContext.Implicits.global

  val restClient1 = TwitterRestClient()
  val streamingClient = TwitterStreamingClient()
  restClient1.verifyCredentials().onComplete {
    case Failure(x) =>
      x.printStackTrace()
    case x =>
      println(x)
  }

  streamingClient.userEvents() {
    case m: UserStreamingMessage =>
      println(m)
  }
}
