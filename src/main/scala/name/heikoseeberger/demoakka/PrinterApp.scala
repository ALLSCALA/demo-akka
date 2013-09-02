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

import akka.actor.{ Actor, ActorSystem, Props }
import scala.io.StdIn

object PrinterApp extends App {

  val system = ActorSystem("demo-system")
  val printer = system.actorOf(Props(new Printer), "printer")
  printer ! "Hello, world!"

  StdIn.readLine(f"Hit ENTER to exit ...%n")
  system.shutdown()
  system.awaitTermination()
}

class Printer extends Actor {

  override def receive: Receive = {
    case message => println(message)
  }
}
