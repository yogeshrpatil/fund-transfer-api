package com.revolut

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.revolut.actors.AccountManager.{ Accounts, CloseAccount, GetAccountDetails, OpenAccount }
import com.revolut.actors.TransactionManager.{ GetTransaction, CreateTransaction }
import com.revolut.models.{ Transaction, Account }

import scala.concurrent.duration._

trait AppRoutes extends JsonSupport {
  implicit def system: ActorSystem
  lazy val log = Logging(system, classOf[AppRoutes])
  def accountManagerActor: ActorRef
  def transactionManagerActor: ActorRef
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
              onSuccess((accountManagerActor ? OpenAccount(account)).mapTo[String])(complete(_))
            }
          }
        }, {
          get {
            onSuccess((accountManagerActor ? GetAccountDetails).mapTo[Accounts])(complete(_))
          }
        })
      } ~ {
        path("accounts" / Segment) { id =>
          concat(
            {
              get {
                onSuccess((accountManagerActor ? GetAccountDetails(id)).mapTo[Account])(complete(_))
              }
            },
            delete {
              onSuccess((accountManagerActor ? CloseAccount(id)).mapTo[Account])(complete(_))
            }
          )
        }
      } ~
      path("transaction") {
        post {
          entity(as[CreateTransaction]) { transaction =>
            onSuccess((transactionManagerActor ? transaction).mapTo[String])(complete(_))
          }
        }
      } ~
      path("transaction" / Segment) { id =>
        get {
          onSuccess((transactionManagerActor ? GetTransaction(id)).mapTo[Transaction])(complete(_))
        }
      }

}
