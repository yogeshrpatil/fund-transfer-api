package com.revolut

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.revolut.actors.AccountManager.{ Accounts, OpenAccount, CloseAccount, GetAccountDetails }
import com.revolut.models.Models.Account

import scala.concurrent.duration._

trait AppRoutes extends JsonSupport {
  implicit def system: ActorSystem
  lazy val log = Logging(system, classOf[AppRoutes])
  def accountManagerActor: ActorRef
  implicit lazy val timeout = Timeout(5.seconds)

  lazy val appRoutes: Route =
    path("healthcheck") {
      get {
        complete("I am healthy")
      }
    } ~
      path("accounts") {
        concat({
          put {
            entity(as[Account]) { account =>
              onSuccess((accountManagerActor ? OpenAccount(account)).mapTo[Account]) { response => complete(response) }
            }
          }
        }, {
          path("accounts") {
            get {
              onSuccess((accountManagerActor ? GetAccountDetails).mapTo[Accounts]) { response => complete(response) }
            }
          }
        })
      } ~ {
        path("accounts" / Segment) { id =>
          concat(
            {
              get {
                onSuccess((accountManagerActor ? GetAccountDetails(id)).mapTo[Account]) { response => complete(response) }
              }
            },
            delete {
              onSuccess((accountManagerActor ? CloseAccount(id)).mapTo[Account]) { response => complete(response) }
            }
          )
        }
      }

}
