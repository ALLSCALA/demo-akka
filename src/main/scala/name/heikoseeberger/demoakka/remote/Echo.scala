/*
 * Copyright 2013 Heiko Seeberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package name.heikoseeberger.demoakka
package remote

import akka.actor.{ ActorIdentity, ActorLogging, ActorRef, ActorSystem, Identify, ReceiveTimeout, Terminated }
import akka.actor.ActorDSL._
import scala.concurrent.duration._

object EchoClientApp extends App {

  System.setProperty("akka.remote.netty.tcp.port", "0") // Use a random port for the client
  val hostname = args.headOption getOrElse "localhost"

  implicit val system = ActorSystem("demo-system")
  actor(new Act with ActorLogging {
    val echoSelection = context.actorSelection(s"akka.tcp://demo-system@$hostname:2552/user/echo")
    becomeInitializing()

    def becomeInitializing(): Unit = {
      log.info("Initializing")
      context.setReceiveTimeout(5 seconds)
      identifyEcho()
      become {
        case ActorIdentity(0, Some(echo)) => becomeRunning(echo)
        case ReceiveTimeout               => identifyEcho()
      }
    }

    def becomeRunning(echo: ActorRef): Unit = {
      log.info("Running")
      import context.dispatcher
      context.setReceiveTimeout(Duration.Undefined)
      val cancellable = context.system.scheduler.schedule(0 seconds, 5 seconds, echo, "Hello, world!")
      context.watch(echo)
      become {
        case Terminated(`echo`) =>
          cancellable.cancel()
          becomeInitializing()
        case message =>
          log.info(message.toString)
      }
    }

    def identifyEcho(): Unit =
      echoSelection ! Identify(0)
  })

  readLine(f"Hit ENTER to exit ...%n")
  system.shutdown()
  system.awaitTermination()
}

object EchoServerApp extends App {

  val hostname = args.headOption getOrElse "localhost"
  System.setProperty("akka.remote.netty.tcp.hostname", hostname)

  implicit val system = ActorSystem("demo-system")
  actor("echo")(new Act {
    become {
      case message =>
        println(s"Received $message")
        sender() ! message
    }
  })

  readLine(f"Hit ENTER to exit ...%n")
  system.shutdown()
  system.awaitTermination()
}
