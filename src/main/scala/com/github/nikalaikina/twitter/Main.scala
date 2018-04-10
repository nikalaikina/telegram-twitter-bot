package com.github.nikalaikina.twitter

import com.github.nikdon.telepooz.engine._

object Main extends Telepooz with App {

  implicit val are = new ApiRequestExecutor {}
  val poller       = new Polling
  val reactor = new Reactor {
    val reactions = CommandBasedReactions()
      .on("/start")(implicit message ⇒
        args ⇒ {
          println(s"You are started! $args")
          reply("You are started!")
        })
      .on("/test")(implicit message ⇒
        args ⇒ {
          println(s"You are tested! $args")
          reply("You are tested!")
        })
  }

  instance.run((are, poller, reactor))

}
