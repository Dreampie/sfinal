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

import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.sql.Blob
import java.sql.Clob
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import java.sql.Types
import java.util.ArrayList
import java.util.List
import java.util.Map

/**
 * ModelBuilder.
 */
object ModelBuilder {
  @SuppressWarnings(Array("rawtypes", "unchecked")) final def build(rs: ResultSet, modelClass: Class[_ <: Model[_ <: Model[_ <: Model[_]]]]): List[T] = {
    val result: List[T] = new ArrayList[T]
    val rsmd: ResultSetMetaData = rs.getMetaData
    val columnCount: Int = rsmd.getColumnCount
    val labelNames: Array[String] = new Array[String](columnCount + 1)
    val types: Array[Int] = new Array[Int](columnCount + 1)
    buildLabelNamesAndTypes(rsmd, labelNames, types)
    while (rs.next) {
      val ar: Model[_] = modelClass.newInstance
      val attrs: Map[String, AnyRef] = ar.getAttrs
      {
        var i: Int = 1
        while (i <= columnCount) {
          {
            var value: AnyRef = null
            if (types(i) < Types.BLOB) value = rs.getObject(i)
            else if (types(i) == Types.CLOB) value = handleClob(rs.getClob(i))
            else if (types(i) == Types.NCLOB) value = handleClob(rs.getNClob(i))
            else if (types(i) == Types.BLOB) value = handleBlob(rs.getBlob(i))
            else value = rs.getObject(i)
            attrs.put(labelNames(i), value)
          }
          ({
            i += 1; i - 1
          })
        }
      }
      result.add(ar.asInstanceOf[T])
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

  def handleBlob(blob: Blob): Array[Byte] = {
    if (blob == null) return null
    var is: InputStream = null
    try {
      is = blob.getBinaryStream
      val data: Array[Byte] = new Array[Byte](blob.length.asInstanceOf[Int])
      is.read(data)
      is.close
      return data
    }
    catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
    finally {
      try {
        is.close
      }
      catch {
        case e: IOException => {
          throw new RuntimeException(e)
        }
      }
    }
  }

  def handleClob(clob: Clob): String = {
    if (clob == null) return null
    var reader: Reader = null
    try {
      reader = clob.getCharacterStream
      val buffer: Array[Char] = new Array[Char](clob.length.asInstanceOf[Int])
      reader.read(buffer)
      return new String(buffer)
    }
    catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
    finally {
      try {
        reader.close
      }
      catch {
        case e: IOException => {
          throw new RuntimeException(e)
        }
      }
    }
  }
}



