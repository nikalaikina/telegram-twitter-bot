package com.github.nikalaikina.twitter

import akka.actor.{ActorRef, Props}
import info.mukel.telegrambot4s.api.declarative.Commands
import info.mukel.telegrambot4s.api.{Polling, TelegramBot}
import info.mukel.telegrambot4s.models.{ChatId, Message}

import scala.collection.mutable

object TwitterBot extends TelegramBot with Polling with Commands with EventCallbacks {
  import com.danielasfregola.twitter4s.util.Configurations._

  lazy val token: String = config.getString("telegram.token")

  val auth = new TwitterAuth(consumerTokenKey, consumerTokenSecret)

  val actorMap: mutable.Map[ChatId, ActorRef] = mutable.Map()

  def newChatActor(chatId: ChatId) = system.actorOf(Props(classOf[ChatStateActor], chatId, auth), s"chat-${chatId.toEither.merge.toString}")

  def chatActor(implicit msg: Message) = {
    actorMap.getOrElse(msg.chat.id, newChatActor(msg.chat.id))
  }

  onCommand('start) { implicit msg: Message =>
    chatActor ! TwitterLoginRequest
  }

  onCommand('follow_back_list) { implicit msg: Message =>
    chatActor ! FollowBackList
  }

}
