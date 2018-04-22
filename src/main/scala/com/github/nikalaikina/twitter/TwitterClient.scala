package com.github.nikalaikina.twitter

import com.danielasfregola.twitter4s.entities.streaming.UserStreamingMessage
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import com.danielasfregola.twitter4s.{TwitterRestClient, TwitterStreamingClient}
import com.typesafe.scalalogging.LazyLogging

import scala.util.Failure

case class TwitterClient(
  accessToken: AccessToken,
  callback: PartialFunction[UserStreamingMessage, Unit] = PartialFunction.empty
) extends LazyLogging {
  import com.danielasfregola.twitter4s.util.Configurations._

  import scala.concurrent.ExecutionContext.Implicits.global

  val consumerToken = ConsumerToken(key = consumerTokenKey, secret =  consumerTokenSecret)

  val restClient = TwitterRestClient(consumerToken, accessToken)
  restClient.verifyCredentials().onComplete {
    case Failure(x) => logger.error("Credentials are not verified", x)
    case x          => logger.info(x.toString)
  }

  val streamingClient = TwitterStreamingClient(consumerToken, accessToken)
  streamingClient.userEvents()(callback)

}
