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

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import java.sql.Types
import java.util.ArrayList
import java.util.List
import java.util.Map

/**
 * RecordBuilder.
 */
object RecordBuilder {
  @SuppressWarnings(Array("unchecked")) final def build(config: Config, rs: ResultSet): List[Record] = {
    val result: List[Record] = new ArrayList[Record]
    val rsmd: ResultSetMetaData = rs.getMetaData
    val columnCount: Int = rsmd.getColumnCount
    val labelNames: Array[String] = new Array[String](columnCount + 1)
    val types: Array[Int] = new Array[Int](columnCount + 1)
    buildLabelNamesAndTypes(rsmd, labelNames, types)
    while (rs.next) {
      val record: Record = new Record
      record.setColumnsMap(config.containerFactory.getColumnsMap)
      val columns: Map[String, AnyRef] = record.getColumns
      {
        var i: Int = 1
        while (i <= columnCount) {
          {
            var value: AnyRef = null
            if (types(i) < Types.BLOB) value = rs.getObject(i)
            else if (types(i) == Types.CLOB) value = ModelBuilder.handleClob(rs.getClob(i))
            else if (types(i) == Types.NCLOB) value = ModelBuilder.handleClob(rs.getNClob(i))
            else if (types(i) == Types.BLOB) value = ModelBuilder.handleBlob(rs.getBlob(i))
            else value = rs.getObject(i)
            columns.put(labelNames(i), value)
          }
          ({
            i += 1; i - 1
          })
        }
      }
      result.add(record)
    }
    return result
  }

  private final def buildLabelNamesAndTypes(rsmd: ResultSetMetaData, labelNames: Array[String], types: Array[Int]) {
    {
      var i: Int = 1
      while (i < labelNames.length) {
        {
          labelNames(i) = rsmd.getColumnLabel(i)
          types(i) = rsmd.getColumnType(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }
}







