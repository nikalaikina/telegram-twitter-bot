package com.github.nikalaikina.twitter

import com.danielasfregola.twitter4s.entities.streaming.UserStreamingMessage
import info.mukel.telegrambot4s.api.declarative.Commands
import info.mukel.telegrambot4s.api.{Polling, TelegramBot}

object Main extends App {
  SafeBot.run()
}

object SafeBot extends TelegramBot with Polling with Commands {
  import com.danielasfregola.twitter4s.util.Configurations._

  lazy val token: String = config.getString("telegram.token")

  val auth = new TwitterAuth(consumerTokenKey, consumerTokenSecret)

  onCommand('start) { implicit msg =>
    for {
      (url, clientF) <- auth.requestUrl
      _ <- reply(s"Follow the link to log in Twitter: $url")
      client <- clientF
    } yield {
      logger.info(s"Subscribed!")
      client.subscribe(Seq(
        { m: UserStreamingMessage =>
          logger.info(s"Message: $m")
          reply(m.toString)
        }
      ))

    }
  }
}

