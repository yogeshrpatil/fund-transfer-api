package revolut

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object QuickstartServer extends App with AppRoutes {

  implicit val system: ActorSystem = ActorSystem("revoluteFundTransferAPI")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val userRegistryActor: ActorRef = ???

  lazy val routes: Route = appRoutes

  Http().bindAndHandle(routes, "localhost", 8088)

  Await.result(system.whenTerminated, Duration.Inf)
}