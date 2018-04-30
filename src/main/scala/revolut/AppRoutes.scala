package revolut

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.server.Route
import akka.util.Timeout

import scala.concurrent.duration._

trait AppRoutes extends JsonSupport {
  implicit def system: ActorSystem
  lazy val log = Logging(system, classOf[AppRoutes])
  def userRegistryActor: ActorRef
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  lazy val appRoutes: Route = ???
}
