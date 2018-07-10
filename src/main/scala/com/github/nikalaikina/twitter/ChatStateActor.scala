package com.github.nikalaikina.twitter

import akka.event.LoggingReceive
import akka.pattern.pipe
import akka.persistence._
import info.mukel.telegrambot4s.models.ChatId

import scala.concurrent.Future

case class ChatState(client: TwitterClient, myTwitterId: Long)

class ChatStateActor(chatId: ChatId, auth: TwitterAuth) extends PersistentActor with UserUtility {
  override def persistenceId = s"chat-${chatId.toEither.merge.toString}"

  import scala.concurrent.ExecutionContext.Implicits.global


  def receiveCommand: Receive = {
    case e: ChatEvent =>
      persist(e)(mainReceive)
  }

  def mainReceive: Receive = LoggingReceive {
    case TwitterLoginRequest =>
      auth.requestUrl.map(t => (TwitterLoginUrl.apply _).tupled(t)) pipeTo self

    case TwitterLoginUrl(url, client) =>
      msg(TelegramMsg(text = url, parseMode = None))
      client.map(TwitterLogin(_, None)) pipeTo self

    case e @ TwitterLogin(client, None) =>
      val myTwitterId = client.restClient.verifyCredentials().map(_.data.id)
      myTwitterId.map(id => e.copy(myTwitterId = Some(id))) pipeTo self

    case TwitterLogin(client, Some(myTwitterId)) =>
      context become authedReceive(ChatState(client, myTwitterId))
  }

  def authedReceive(state: ChatState): Receive = {
    case FollowBackList =>
      followBack(state).map(UtilResponse) pipeTo self

    case UtilResponse(message) =>
      msg(message)
  }

  def receiveRecover: Receive = mainReceive

  private def text(s: String): Unit = msg(TelegramMsg(s))
  private def msg(m: TelegramMsg): Unit = {
    TwitterBot.request(m.toSendMessage(chatId))
  }

}


sealed trait ChatEvent

case object TwitterLoginRequest extends ChatEvent

case class TwitterLoginUrl(url: String, client: Future[TwitterClient]) extends ChatEvent
case class TwitterLogin(client: TwitterClient, myTwitterId: Option[Long]) extends ChatEvent

sealed trait UtilRequest extends ChatEvent

object FollowBackList extends UtilRequest

case class UtilResponse(msg: TelegramMsg) extends ChatEvent
