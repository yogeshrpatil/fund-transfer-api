package com.revolut.models

import java.util.UUID

object Models {
  case class Account(id: String, firstName: String, lastName: String, balance: Double)

  object Account {
    def apply(firstName: String, lastName: String, balance: Double): Account = Account(UUID.randomUUID().toString, firstName, lastName, balance)
  }

  case class Transaction(fromId: String, toId: String, amount: Double, memo: String)
}