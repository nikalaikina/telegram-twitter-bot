package com.github.nikalaikina.twitter

import java.util.concurrent.ConcurrentHashMap

import cats.effect.IO
import com.danielasfregola.twitter4s.TwitterAuthenticationClient
import com.danielasfregola.twitter4s.entities.authentication.RequestToken
import com.typesafe.scalalogging.LazyLogging
import org.http4s.client.blaze.Http1Client
import org.http4s.client.oauth1
import org.http4s.dsl.io._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.{HttpService, _}

import scala.concurrent.{Future, Promise}

class TwitterAuth(val consumerKey: String, val consumerSecret: String) extends LazyLogging {
  import scala.concurrent.ExecutionContext.Implicits.global

  object TokenParam extends QueryParamDecoderMatcher[String]("oauth_token")
  object VerifierParam extends QueryParamDecoderMatcher[String]("oauth_verifier")

  private val map = new ConcurrentHashMap[String, (String, Promise[TwitterClient])]()

  private val authClient = TwitterAuthenticationClient()

  private val authService: HttpService[IO] = HttpService[IO] {
    case r @ GET -> Root :? TokenParam(token) +& VerifierParam(verifier) =>
      logger.info(s"Auth result: $r")
      val (secret, promise) = map.remove(token)
      authClient.accessToken(RequestToken(token, secret), oauth_verifier = verifier).foreach { access =>
        logger.info(s"Got access: $access")
        promise.success(TwitterClient(access.accessToken))
      }
      Ok("Hi!")
  }

  private val server = BlazeBuilder[IO]
    .bindHttp(8080, "0.0.0.0")
    .mountService(authService, "/auth")
    .start
    .unsafeRunSync()

  private val httpClient = Http1Client[IO]().unsafeRunSync

  private val consumer = oauth1.Consumer(consumerKey, consumerSecret)
//  val token = oauth1.Token(restClient1.accessToken.key, restClient1.accessToken.secret)

  private def authParams(): IO[String] = {
    val apiUri = Uri.uri("https://api.twitter.com/oauth/request_token")
    val req: Request[IO] = Request(uri = apiUri)
    val target: IO[Request[IO]] = oauth1.signRequest(
      req = req,
      consumer = consumer,
      callback = Some(Uri.uri("http://159.65.127.193:8080/auth")),
      verifier = None,
      token = None
    )

    httpClient.expect[String](target)
  }

  def requestUrl: Future[(String, Future[TwitterClient])] = authParams().unsafeToFuture().map { params =>
    logger.info(s"Got params: $params")
    val promise = Promise[TwitterClient]
    val paramsMap = parseParams(params)
    val token = paramsMap("oauth_token")
    val urlForAuth = "https://api.twitter.com/oauth/authorize?oauth_token=" + token
    val secret = paramsMap("oauth_token_secret")
    map.put(token, (secret, promise))
    (urlForAuth, promise.future)
  }

  private def parseParams(s: String): Map[String, String] = {
    s.split("&").map { v =>
      val param = v.split("=")
      param(0) -> param(1)
    }.toMap
  }
}
