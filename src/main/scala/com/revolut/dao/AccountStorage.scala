package com.revolut.dao

import java.util.concurrent.ConcurrentHashMap

import com.revolut.models.Account

import scala.collection.JavaConverters._

object AccountStorage {

  private val storage = new ConcurrentHashMap[String, Account]()

  def add(account: Account): Account = storage.put(account.id, account)

  def get(id: String): Account = storage.get(id)

  def get: Seq[Account] = storage.values().asScala.toSeq

  def delete(id: String): Account = storage.remove(id)

  def updateBalance(id: String, amount: Double): Account = storage.put(id, storage.get(id).copy(balance = amount))
}
