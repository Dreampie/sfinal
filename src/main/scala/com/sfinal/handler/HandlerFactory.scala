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
package com.sfinal.handler

import java.util.List

/**
 * HandlerFactory.
 */
object HandlerFactory {
  /**
   * Build handler chain
   */
  def getHandler(handlerList: List[Handler], actionHandler: Handler): Handler = {
    var result: Handler = actionHandler
    {
      var i: Int = handlerList.size - 1
      while (i >= 0) {
        {
          val temp: Handler = handlerList.get(i)
          temp.nextHandler = result
          result = temp
        }
        ({
          i -= 1; i + 1
        })
      }
    }
    return result
  }
}

class HandlerFactory {
  private def this() {
    this()
  }
}





