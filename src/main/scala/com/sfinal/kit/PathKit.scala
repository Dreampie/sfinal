package com.sfinal.kit

import java.io._

/**
 * Created by ice on 14-12-3.
 */

class PathKit private

object PathKit {
  private var webRootPathPrivate: String = null
  private var rootClassPathPrivate: String = null

  def path(clazz: Class): String = {
    val path: String = clazz.getResource("").getPath
    new File(path).getAbsolutePath
  }

  def path(obj: Object): String = {
    val path: String = obj.getClass.getResource("").getPath
    new File(path).getAbsolutePath
  }

  def rootClassPath: String = {
    if (rootClassPathPrivate == null) {
      try {
        val path: String = classOf[PathKit].getClassLoader.getResource("").toURI.getPath
        rootClassPathPrivate = new File(path).getAbsolutePath
      }
      catch {
        case e: Exception => {
          val path: String = classOf[PathKit].getClassLoader.getResource("").getPath
          rootClassPathPrivate = new File(path).getAbsolutePath
        }
      }
    }
    rootClassPathPrivate
  }

  def packagePath(obj: Object): String = {
    val p: Package = obj.getClass.getPackage
    if (p != null) p.getName.replaceAll("\\.", "/") else ""
  }

  def fileFromJar_=(file: String) {
    throw new RuntimeException("Not finish. Do not use this method.")
  }

  def webRootPath: String = {
    if (webRootPathPrivate == null) webRootPathPrivate = detectWebRootPath
    webRootPathPrivate
  }

  def webRootPath_=(webRootPath: String) {
    if (webRootPath == null) return
    if (webRootPath.endsWith(File.separator)) webRootPathPrivate = webRootPath.substring(0, webRootPath.length - 1)
    webRootPathPrivate = webRootPath
  }

  private def detectWebRootPath: String = {
    try {
      val path: String = classOf[PathKit].getResource("/").toURI.getPath
      new File(path).getParentFile.getParentFile.getCanonicalPath
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
  }
}