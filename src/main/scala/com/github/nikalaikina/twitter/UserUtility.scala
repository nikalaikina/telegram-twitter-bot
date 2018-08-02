package com.github.nikalaikina.twitter

import com.danielasfregola.twitter4s.entities.{RatedData, User, Users}

import scala.concurrent.Future

trait UserUtility {

  import scala.concurrent.ExecutionContext.Implicits.global


  def followBack(implicit state: ChatState): Future[TelegramMsg] = {
    getSubscribers.map { followers =>
      TelegramMsg(followers
        .filterNot(_.following)
        .sortBy(-_.followers_count)
        .take(20)
        .map(describeUser(_))
        .mkString("\n"))
    }
  }

  private def getSubscribers(implicit state: ChatState): Future[Seq[User]] = {
    getAll({ cursor =>
      state.client.restClient.followersForUserId(state.myTwitterId, skip_status = true, cursor = cursor, count = 200)
    }, (u: Users) => u.next_cursor).map(_.flatMap(_.users))
  }

  def findBots(implicit state: ChatState): Future[TelegramMsg] = {
    getSubscribers.map { followers =>
      TelegramMsg(followers
        .filter(isBot)
        .map(describeUser(_))
        .mkString("\n"))
    }
  }

  def isBot(user: User): Boolean = {
    !Seq(
      user.statuses_count < 3,
      user.friends_count < 5
    ).contains(false)
  }

  private def describeUser(u: User): String = {
    s"""https://twitter.com/${u.screen_name} *${u.name}* `${u.followers_count}`""".stripMargin
  }

  private def getAll[T](f: Long => Future[RatedData[T]], toCursor: T => Long): Future[Seq[T]] =  {
    def rec(acc: List[T] = List.empty, cursor: Long = -1): Future[Seq[T]] = {
      f(cursor).flatMap { res =>
        if (toCursor(res.data) == 0) {
          Future.successful(res.data :: acc)
        } else {
          rec(res.data :: acc, toCursor(res.data))
        }
      }
    }

    rec()
  }

}
