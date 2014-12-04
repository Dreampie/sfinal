/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sfinal.plugin.activerecord.tx

/**
 * TxSerializable.
 */
class TxSerializable extends Tx {
  protected override def getTransactionLevel(config: Config): Int = {
    return TRANSACTION_SERIALIZABLE
  }

  /**
   * A constant indicating that
   * dirty reads, non-repeatable reads and phantom reads are prevented.
   * This level includes the prohibitions in
   * <code>TRANSACTION_REPEATABLE_READ</code> and further prohibits the
   * situation where one transaction reads all rows that satisfy
   * a <code>WHERE</code> condition, a second transaction inserts a row that
   * satisfies that <code>WHERE</code> condition, and the first transaction
   * rereads for the same condition, retrieving the additional
   * "phantom" row in the second read.
   */
  private var TRANSACTION_SERIALIZABLE: Int = 8
}




