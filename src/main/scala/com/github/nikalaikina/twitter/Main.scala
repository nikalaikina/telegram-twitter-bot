package com.github.nikalaikina.twitter

import com.danielasfregola.twitter4s.entities.streaming.UserStreamingMessage
import com.github.nikdon.telepooz.engine._

object Main extends Telepooz with App {
  import com.danielasfregola.twitter4s.util.Configurations._

  val auth = new TwitterAuth(consumerTokenKey, consumerTokenSecret)

  implicit val are = new ApiRequestExecutor {}
  val poller       = new Polling
  val reactor = new Reactor {
    val reactions = CommandBasedReactions()
      .on("/start")(implicit message ⇒
        args ⇒ {
          for {
            (url, clientF) <- auth.requestUrl
            _ <- reply(s"Follow the link to log in Twitter: $url")
            client <- clientF
          } yield {
            client.subscribe(Seq(
              { m: UserStreamingMessage =>
                reply(m.toString)
              }
            ))

          }
        })
      .on("/test")(implicit message ⇒
        args ⇒ {
          println(s"You are tested! $args")
          reply("You are tested!")
        })
  }

  instance.run((are, poller, reactor))

}
