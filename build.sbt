name := "twitter-telegram-bot"

version := "0.1"

scalaVersion := "2.12.5"

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "com.github.nikdon" % "telepooz" % "0.5.6"
libraryDependencies += "com.danielasfregola" %% "twitter4s" % "5.5"
libraryDependencies += "org.twitter4j" % "twitter4j-core" % "4.0.6"

mainClass in (Compile, run) := Some("com.github.nikalaikina.twitter.Main")
