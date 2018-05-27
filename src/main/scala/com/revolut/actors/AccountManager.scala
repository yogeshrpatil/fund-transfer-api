package com.revolut.actors

import akka.actor.{ Actor, ActorLogging, Props }
import com.revolut.actors.AccountManager.{ DeleteAccount, Accounts, AddAccount, GetAccount }
import com.revolut.dao.AccountStorage
import com.revolut.models.Models.Account

class AccountManager extends Actor with ActorLogging {

  override def receive: Receive = {
    case GetAccount => sender() ! Accounts(AccountStorage.get)
    case AddAccount(account) =>
      AccountStorage.add(account); sender() ! AccountStorage.get(account.id)
    case GetAccount(id) => sender() ! AccountStorage.get(id)
    case DeleteAccount(id) => sender() ! AccountStorage.delete(id)
  }
}

object AccountManager {
  def prop: Props = Props[AccountManager]

  case object GetAccount
  case class GetAccount(id: String)
  case class AddAccount(account: Account)
  case class Accounts(accounts: Seq[Account])
  case class DeleteAccount(id: String)
}