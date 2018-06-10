package com.revolut.models

import java.util.{ Date, UUID }

case class Account(id: String, firstName: String, lastName: String, balance: Double)

case class Transaction(id: String = UUID.randomUUID().toString, fromId: String, toId: String, amount: Double, date: String = new Date().toString, memo: String, status: TransactionStatus.Value = TransactionStatus.INITIATED, statusMessage: String = "")

object TransactionStatus extends Enumeration {
  val INITIATED = Value("Transaction INITIATED")
  val INPROGRESS = Value("Transaction INPROGRESS")
  val FAILED = Value("Transaction FAILED")
  val COMPLETED = Value("Transaction COMPLETED")
}