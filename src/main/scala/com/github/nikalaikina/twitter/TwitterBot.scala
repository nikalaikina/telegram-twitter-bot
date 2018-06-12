package com.github.nikalaikina.twitter

import info.mukel.telegrambot4s.api.declarative.Commands
import info.mukel.telegrambot4s.api.{Polling, TelegramBot}
import info.mukel.telegrambot4s.models.Message

object TwitterBot extends TelegramBot with Polling with Commands with EventCallbacks {
  import com.danielasfregola.twitter4s.util.Configurations._

  lazy val token: String = config.getString("telegram.token")

  val auth = new TwitterAuth(consumerTokenKey, consumerTokenSecret)

  onCommand('start) { implicit msg: Message =>
    for {
      (url, clientF) <- auth.requestUrl
      _ <- reply(s"Follow the link to log in Twitter: $url")
      client <- clientF
      me <- client.restClient.verifyCredentials()
      myId = me.data.id
    } yield {
      logger.info(s"Subscribed!")
      reply("Successfully subscribed to your twitter updates!")
      client.copy(callbacks = Seq(debugCallback, defaultCallback(myId)))
    }
  }

}
