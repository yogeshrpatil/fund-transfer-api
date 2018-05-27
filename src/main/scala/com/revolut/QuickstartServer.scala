package com.revolut

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.revolut.actors.AccountManager

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object QuickstartServer extends App with AppRoutes {

  implicit val system: ActorSystem = ActorSystem("revoluteFundTransferAPI")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val accountManagerActor: ActorRef = system.actorOf(AccountManager.prop, "accountManagerActor")

  lazy val routes: Route = appRoutes

  Http().bindAndHandle(routes, "localhost", 8088)

  Await.result(system.whenTerminated, Duration.Inf)
}