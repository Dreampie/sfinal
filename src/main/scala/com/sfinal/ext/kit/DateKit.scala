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
package com.sfinal.ext.kit

import java.util.Date
import com.sfinal.kit.StrKit
import com.sfinal.kit.StrKit

/**
 * DateKit.
 */
object DateKit {
  def setDateFromat(dateFormat: String) {
    if (StrKit.isBlank(dateFormat)) throw new IllegalArgumentException("dateFormat can not be blank.")
    DateKit.dateFormat = dateFormat
  }

  def setTimeFromat(timeFormat: String) {
    if (StrKit.isBlank(timeFormat)) throw new IllegalArgumentException("timeFormat can not be blank.")
    DateKit.timeFormat = timeFormat
  }

  def toDate(dateStr: String): Date = {
    throw new RuntimeException("Not finish!!!")
  }

  def toStr(date: Date): String = {
    return toStr(date, DateKit.dateFormat)
  }

  def toStr(date: Date, format: String): String = {
    throw new RuntimeException("Not finish!!!")
  }

  var dateFormat: String = "yyyy-MM-dd"
  var timeFormat: String = "yyyy-MM-dd HH:mm:ss"
}


