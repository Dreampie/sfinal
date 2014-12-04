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
package com.sfinal.plugin.activerecord

import java.io.Serializable
import java.util.List

/**
 * Page is the result of Model.paginate(......) or Db.paginate(......)
 */
object Page {
  private final val serialVersionUID: Long = -5395997221963176643L
}

class Page extends Serializable {
  /**
   * Constructor.
   * @param list the list of paginate result
   * @param pageNumber the page number
   * @param pageSize the page size
   * @param totalPage the total page of paginate
   * @param totalRow the total row of paginate
   */
  def this(list: List[T], pageNumber: Int, pageSize: Int, totalPage: Int, totalRow: Int) {
    this()
    this.list = list
    this.pageNumber = pageNumber
    this.pageSize = pageSize
    this.totalPage = totalPage
    this.totalRow = totalRow
  }

  /**
   * Return list of this page.
   */
  def getList: List[T] = {
    return list
  }

  /**
   * Return page number.
   */
  def getPageNumber: Int = {
    return pageNumber
  }

  /**
   * Return page size.
   */
  def getPageSize: Int = {
    return pageSize
  }

  /**
   * Return total page.
   */
  def getTotalPage: Int = {
    return totalPage
  }

  /**
   * Return total row.
   */
  def getTotalRow: Int = {
    return totalRow
  }

  private var list: List[T] = null
  private var pageNumber: Int = 0
  private var pageSize: Int = 0
  private var totalPage: Int = 0
  private var totalRow: Int = 0
}



