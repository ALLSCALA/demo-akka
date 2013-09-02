lazy val demoAkka = project in file(".")

name := "demo-akka"

Common.settings

libraryDependencies ++= Dependencies.demoAkka

initialCommands := """|import name.heikoseeberger.demoakka._
                      |import akka.actor._
                      |import akka.actor.ActorDSL._
                      |import akka.cluster._
                      |import akka.cluster.routing._
                      |import akka.routing._
                      |import akka.util._
                      |import com.typesafe.config._
                      |import scala.concurrent._
                      |import scala.concurrent.duration._""".stripMargin
