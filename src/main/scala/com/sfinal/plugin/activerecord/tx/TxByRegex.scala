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

import java.sql.SQLException
import java.util.regex.Pattern
import com.sfinal.aop.Interceptor
import com.sfinal.core.ActionInvocation
import com.sfinal.kit.StrKit
import com.sfinal.plugin.activerecord._

/**
 * TxByRegex.
 */
class TxByRegex extends Interceptor {
  def this(regex: String) {
    this()
    `this`(regex, true)
  }

  def this(regex: String, caseSensitive: Boolean) {
    this()
    if (StrKit.isBlank(regex)) throw new IllegalArgumentException("regex can not be blank.")
    pattern = if (caseSensitive) Pattern.compile(regex) else Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
  }

  def intercept(ai: ActionInvocation) {
    var config: Config = Tx.getConfigWithTxConfig(ai)
    if (config == null) config = DbKit.getConfig
    if (pattern.matcher(ai.getActionKey).matches) {
      DbPro.use(config.getName).tx(new IAtom {
        def run: Boolean = {
          ai.invoke
          return true
        }
      })
    }
    else {
      ai.invoke
    }
  }

  private var pattern: Pattern = null
}





