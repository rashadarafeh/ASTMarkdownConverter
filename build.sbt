name := """markdown-html-converter"""
organization := "com.mailchimp.interview"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.14"

libraryDependencies += guice
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.specs2" %% "specs2-core" % "4.13.0" % Test
javaOptions += "-Xmx4g"
fork := true
javaOptions in fork +="-Xmx4g"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.mailchimp.interview.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.mailchimp.interview.binders._"
