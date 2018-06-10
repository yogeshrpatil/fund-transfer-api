package com.revolut

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.revolut.actors.AccountManager.Accounts
import com.revolut.actors.TransactionManager.CreateTransaction
import com.revolut.models.{ TransactionStatus, Transaction, Account }
import spray.json._

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val transactionJsonFormat = jsonFormat4(CreateTransaction)
  implicit val accountJsonFormat = jsonFormat4(Account)
  implicit val accountsJsonFormat = jsonFormat1(Accounts)
  implicit val transactionStatusJsonFormat = new EnumJsonConverter(TransactionStatus)
  implicit val transactionsJsonFormat = jsonFormat8(Transaction)
}

class EnumJsonConverter[T <: scala.Enumeration](enu: T) extends RootJsonFormat[T#Value] {
  override def write(obj: T#Value): JsValue = JsString(obj.toString)

  override def read(json: JsValue): T#Value = {
    json match {
      case JsString(txt) => enu.withName(txt)
      case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
    }
  }
}