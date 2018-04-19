package com.github.nikalaikina.twitter

import cats.effect.IO
import com.danielasfregola.twitter4s.entities.streaming.UserStreamingMessage
import com.danielasfregola.twitter4s.{TwitterRestClient, TwitterStreamingClient}
import org.http4s.{HttpService, _}
import org.http4s.client.blaze.Http1Client
import org.http4s.client.oauth1
import org.http4s.dsl.io._
import org.http4s.server.blaze.BlazeBuilder

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

  val authService: HttpService[IO] = HttpService[IO] {
    case r =>
      println(":" * 100)
      println("cheers")
      println(r)
      Ok(s"Hello, lalala.")
  }

  val builder = BlazeBuilder[IO]
    .bindHttp(8080, "0.0.0.0")
    .mountService(authService, "/auth")
    .start
    .unsafeRunSync()

  val httpClient = Http1Client[IO]().unsafeRunSync


  val consumer = oauth1.Consumer(restClient1.consumerToken.key, restClient1.consumerToken.secret)
  val token = oauth1.Token(restClient1.accessToken.key, restClient1.accessToken.secret)

  def authParams(): IO[String] = {
    val apiUri = Uri.uri("https://api.twitter.com/oauth/request_token")
    val req: Request[IO] = Request(uri = apiUri)
    val target: IO[Request[IO]] = oauth1.signRequest(
      req = req,
      consumer = consumer,
      callback = Some(Uri.uri("http://159.65.127.193:8080/auth")),
      verifier = None,
      token = Some(token)
    )

    httpClient.expect[String](target)
  }

  authParams().unsafeToFuture().foreach { params =>
    println("-" * 100)
    println("params: " + params)
    val newParams = params.takeWhile(_ != '&')
    println(newParams)
    val value = "https://api.twitter.com/oauth/authorize?" + newParams
    println(value)
  }
}
