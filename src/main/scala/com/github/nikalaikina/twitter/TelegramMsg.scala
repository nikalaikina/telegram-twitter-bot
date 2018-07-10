package com.github.nikalaikina.twitter

import info.mukel.telegrambot4s.methods.{ParseMode, SendMessage}
import info.mukel.telegrambot4s.models.{ChatId, ReplyMarkup}

case class TelegramMsg(
  text                  : String,
  disableNotification   : Boolean = false,
  replyToMessageId      : Option[Int] = None,
  replyMarkup           : Option[ReplyMarkup] = None
) {
  def toSendMessage(chatId: ChatId) = SendMessage(
    chatId,
    text,
    parseMode = Some(ParseMode.Markdown),
    disableNotification = Some(disableNotification),
    replyToMessageId = replyToMessageId,
    replyMarkup = replyMarkup
  )
}
