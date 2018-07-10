package com.github.nikalaikina.twitter

import info.mukel.telegrambot4s.methods.ParseMode.ParseMode
import info.mukel.telegrambot4s.methods.{ParseMode, SendMessage}
import info.mukel.telegrambot4s.models.{ChatId, ReplyMarkup}

case class TelegramMsg(
  text                  : String,
  disableNotification   : Boolean = false,
  replyToMessageId      : Option[Int] = None,
  replyMarkup           : Option[ReplyMarkup] = None,
  parseMode             : Option[ParseMode] = Some(ParseMode.Markdown),
) {
  def toSendMessage(chatId: ChatId) = SendMessage(
    chatId,
    text,
    parseMode = parseMode,
    disableNotification = Some(disableNotification),
    replyToMessageId = replyToMessageId,
    replyMarkup = replyMarkup
  )
}
