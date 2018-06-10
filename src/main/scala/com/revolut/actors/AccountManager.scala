package com.revolut.actors

import akka.actor.{ Actor, ActorLogging, Props }
import com.revolut.actors.AccountManager.{ Accounts, CloseAccount, GetAccountDetails, OpenAccount }
import com.revolut.dao.AccountStorage
import com.revolut.models.Account

class AccountManager extends Actor with ActorLogging {

  override def receive: Receive = {
    case GetAccountDetails => sender() ! Accounts(AccountStorage.get)
    case OpenAccount(account) =>
      AccountStorage.add(account) match {
        case null => sender() ! (s"Account with ${account.id} created successfully.")
        case acc => sender() ! (s"Account with ${account.id} updated successfully.")
      }
    case GetAccountDetails(id) => sender() ! AccountStorage.get(id)
    case CloseAccount(id) => sender() ! AccountStorage.delete(id)
  }
}

object AccountManager {
  def prop: Props = Props[AccountManager]

  case object GetAccountDetails
  case class GetAccountDetails(id: String)
  case class OpenAccount(account: Account)
  case class Accounts(accounts: Seq[Account])
  case class CloseAccount(id: String)
}