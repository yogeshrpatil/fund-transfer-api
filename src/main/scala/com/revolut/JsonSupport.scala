package com.revolut

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.revolut.actors.AccountManager.Accounts
import com.revolut.models.Models.{ Account, Transaction }
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val transactionJsonFormat = jsonFormat4(Transaction)
  implicit val accountJsonFormat = jsonFormat4(Account.apply)
  implicit val accountsJsonFormat = jsonFormat1(Accounts)
}