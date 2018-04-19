name := "twitter-telegram-bot"

version := "0.1"

scalaVersion := "2.12.5"

val http4sVersion = "0.18.9"

resolvers += "jitpack" at "https://jitpack.io"

scalacOptions ++= Seq("-Ypartial-unification")

libraryDependencies += "com.github.nikdon" % "telepooz" % "0.5.6"
libraryDependencies += "com.danielasfregola" %% "twitter4s" % "5.5"
libraryDependencies += "org.twitter4j" % "twitter4j-core" % "4.0.6"
libraryDependencies += "org.http4s" %% "http4s-core" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-dsl" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-blaze-client" % http4sVersion


mainClass in (Compile, run) := Some("com.github.nikalaikina.twitter.Main")
