package com.github.nikalaikina.twitter

import com.danielasfregola.twitter4s.entities.enums.SimpleEventCode._
import com.danielasfregola.twitter4s.entities.enums.TweetEventCode.{Favorite, FavoritedRetweet, QuotedTweet, Unfavorite}
import com.danielasfregola.twitter4s.entities.streaming.UserStreamingMessage
import com.danielasfregola.twitter4s.entities.streaming.common._
import com.danielasfregola.twitter4s.entities.streaming.user._
import com.danielasfregola.twitter4s.entities.{DirectMessage, Tweet, User}
import info.mukel.telegrambot4s.api.declarative.Messages
import info.mukel.telegrambot4s.methods.ParseMode
import info.mukel.telegrambot4s.models.Message

trait EventCallbacks extends Messages {


  def defaultCallback(myUserId: Long)(implicit message: Message): PartialFunction[UserStreamingMessage, Unit] = {
    case m: Tweet if m.in_reply_to_user_id.contains(myUserId) =>
      for {
        source <- m.user
      } {
        reply(tweetAction(source, s"""replied to you""", m), Some(ParseMode.Markdown))
      }

    case m: Tweet =>
      for {
        source <- m.user
        tweet <- m.retweeted_status
      } {
        if (tweet.user.exists(_.id == myUserId)) {
          reply(tweetAction(source, "retweeted", tweet), Some(ParseMode.Markdown))
        } else if (tweet.in_reply_to_user_id.contains(myUserId)) {
          reply(tweetAction(source, s"""replied _${tweet.text}_ to tweet:""", tweet), Some(ParseMode.Markdown))
        }
      }

    case m: FriendsLists =>

    case m: DirectMessage =>
      reply(s"New direct message from *${m.sender_screen_name}*: `${m.text}`", Some(ParseMode.Markdown))
    case m: DisconnectMessage =>
      reply("Disconnected, click /start to log in again", Some(ParseMode.Markdown))

    case TweetEvent(time, Favorite,         target: User, source: User, tweet) =>
      reply(tweetAction(source, "favorited", tweet), Some(ParseMode.Markdown))
    case TweetEvent(time, FavoritedRetweet, target: User, source: User, tweet) =>
      reply(tweetAction(source, "favorited retweet", tweet), Some(ParseMode.Markdown))
    case TweetEvent(time, Unfavorite,       target: User, source: User, tweet) =>
      reply(tweetAction(source, "unfavorited", tweet), Some(ParseMode.Markdown))
    case TweetEvent(time, QuotedTweet,      target: User, source: User, tweet) =>
      reply(tweetAction(source, "quoted", tweet), Some(ParseMode.Markdown))

    case m: TwitterListEvent =>

    case m: FriendsListsStringified =>

    case m: LimitNotice =>

    case m: LocationDeletionNotice =>

    case SimpleEvent(time, AccessRevoked, target: User, source: User, target_object: Option[String]) =>
      reply(userAction(source, "revoked access"), Some(ParseMode.Markdown))
    case SimpleEvent(time, Block,         target: User, source: User, target_object: Option[String]) =>
      reply(userAction(source, "blocked you"), Some(ParseMode.Markdown))
    case SimpleEvent(time, Unblock,       target: User, source: User, target_object: Option[String]) =>
      reply(userAction(source, "unblocked you"), Some(ParseMode.Markdown))
    case SimpleEvent(time, Follow,        target: User, source: User, target_object: Option[String]) =>
      reply(userAction(source, "followed you"), Some(ParseMode.Markdown))
    case SimpleEvent(time, Unfollow,      target: User, source: User, target_object: Option[String]) =>
      reply(userAction(source, "unfollowed you"), Some(ParseMode.Markdown))
    case SimpleEvent(time, UserUpdate,    target: User, source: User, target_object: Option[String]) =>

    case m: StatusDeletionNotice =>

    case m: StatusWithheldNotice =>

    case m: UserWithheldNotice =>

    case m: WarningMessage =>

    case m: UserStreamingMessage =>
      logger.debug(s"Message: $m")
  }


  def debugCallback(implicit message: Message): PartialFunction[UserStreamingMessage, Unit] = {
    case m: UserStreamingMessage =>
      logger.debug(s"Debug: $m")
  }


  private def userAction(source: User, action: String) = {
    s"${source.screen_name} $action " +
      s"(${source.followers_count} followers, ${if (source.following) "following" else "not following"})"
  }

  private def tweetAction(source: User, action: String, tweet: Tweet) = {
    s"""*${source.screen_name}* $action tweet `${tweet.text}`"""
  }
}
