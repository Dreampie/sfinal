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
 * TxReadUncommitted.
 */
class TxReadUncommitted extends Tx {
  protected override def getTransactionLevel(config: Config): Int = {
    return TRANSACTION_READ_UNCOMMITTED
  }

  /**
   * A constant indicating that
   * dirty reads, non-repeatable reads and phantom reads can occur.
   * This level allows a row changed by one transaction to be read
   * by another transaction before any changes in that row have been
   * committed (a "dirty read").  If any of the changes are rolled back,
   * the second transaction will have retrieved an invalid row.
   */
  private var TRANSACTION_READ_UNCOMMITTED: Int = 1
}