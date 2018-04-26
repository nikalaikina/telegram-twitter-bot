name := "twitter-telegram-bot"

version := "0.1"

scalaVersion := "2.12.5"

val http4sVersion = "0.18.9"

resolvers += "jitpack" at "https://jitpack.io"

scalacOptions ++= Seq("-Ypartial-unification")

libraryDependencies += "info.mukel" %% "telegrambot4s" % "3.0.14"

libraryDependencies += "com.danielasfregola" %% "twitter4s" % "6.1-SNAPSHOT"
libraryDependencies += "org.http4s" %% "http4s-core" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-dsl" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-blaze-client" % http4sVersion
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"


mainClass in (Compile, run) := Some("com.github.nikalaikina.twitter.Main")
