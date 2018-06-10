package com.revolut.actors

import akka.actor.{ Actor, ActorLogging, Props }
import com.revolut.actors.TransactionManager.{ CreateTransaction, GetTransaction }
import com.revolut.dao.{ AccountStorage, TransactionStorage }
import com.revolut.models.{ TransactionStatus, Transaction }

class TransactionManager extends Actor with ActorLogging {

  override def receive: Receive = {
    case tran: GetTransaction => sender() ! TransactionStorage.get(tran.id)
    case tran: CreateTransaction => {
      val transaction: Transaction = tran
      TransactionStorage.addTransaction(transaction)
      sender() ! s"Transaction created successfully with id: ${transaction.id}"
      executeTransaction(transaction)
    }
  }

  private def executeTransaction(transaction: Transaction) = {
    TransactionStorage.updateStatus(transaction.id, TransactionStatus.INPROGRESS, "Transaction Started.")
    validateTransaction(transaction) match {
      case "Ok" => {
        deductAmountFromSourceAccount(transaction)
        addAmountToReceiverAccount(transaction)
        TransactionStorage.updateStatus(transaction.id, TransactionStatus.COMPLETED, "Transaction Completed successfully.")
      }
      case message: String => TransactionStorage.updateStatus(transaction.id, TransactionStatus.FAILED, s"Transaction Failed with reason: ${message}")
    }
  }

  private def deductAmountFromSourceAccount(transaction: Transaction): Unit = AccountStorage.updateBalance(transaction.fromId, AccountStorage.get(transaction.fromId).balance - transaction.amount)
  private def addAmountToReceiverAccount(transaction: Transaction): Unit = AccountStorage.updateBalance(transaction.fromId, AccountStorage.get(transaction.fromId).balance + transaction.amount)

  private def validateTransaction(transaction: Transaction) = transaction match {
    case transaction if (AccountStorage.get(transaction.fromId) == null) => "Source Account does not exists"
    case transaction if (AccountStorage.get(transaction.toId) == null) => "Receiver Account does not exists"
    case transaction if (transaction.amount > 0) => "Invalid transfer amount. Amount should be valid greater than zero number."
    case transaction if (AccountStorage.get(transaction.toId).balance >= transaction.amount) => "Insufficient fund in source account"
    case _ => "Ok"
  }
}

object TransactionManager {
  def prop: Props = Props[TransactionManager]
  case class GetTransaction(id: String)
  case class CreateTransaction(fromId: String, toId: String, amount: Double, memo: String)
  implicit def transactionConverter(transaction: CreateTransaction): Transaction = new Transaction(fromId = transaction.fromId, toId = transaction.toId, amount = transaction.amount, memo = transaction.memo)
}

