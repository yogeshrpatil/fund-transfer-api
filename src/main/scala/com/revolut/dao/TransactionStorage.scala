package com.revolut.dao

import java.util.concurrent.ConcurrentHashMap

import com.revolut.models.{ Transaction, TransactionStatus }

object TransactionStorage {
  private val storage = new ConcurrentHashMap[String, Transaction]()

  def get(id: String): Transaction = storage.get(id)
  def addTransaction(transaction: Transaction): Transaction = storage.put(transaction.id, transaction)
  def updateStatus(id: String, newStatus: TransactionStatus.Value, statusMessage: String) = storage.put(id, storage.get(id).copy(status = newStatus, statusMessage = statusMessage))
}
