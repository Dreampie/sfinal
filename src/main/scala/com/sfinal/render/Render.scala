package com.sfinal.render

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import com.sfinal.core.Const

/**
 * Created by ice on 14-12-3.
 */
trait Render {


  var view: String = null
  protected var request: HttpServletRequest = null
  protected var response: HttpServletResponse = null

  def context(request: HttpServletRequest, response: HttpServletResponse): Render = {
    this.request = request
    this.response = response
    this
  }

  def context(request: HttpServletRequest, response: HttpServletResponse, viewPath: String): Render = {
    this.request = request
    this.response = response
    if (view != null && !view.startsWith("/")) view = viewPath + view
    this
  }

  /**
   * Render to client
   */
  def render

}

object Render {
  private var encodingPrivate: String = Const.DEFAULT_ENCODING
  private var devModePrivate: Boolean = Const.DEFAULT_DEV_MODE

  def init(encoding: String, devMode: Boolean) {
    this.encodingPrivate = encoding
    this.devModePrivate = devMode
  }

  def encoding: String = {
    encodingPrivate
  }

  def encoding_=(encoding: String) {
    encodingPrivate = encoding
  }

  def devMode: Boolean = {
    devModePrivate
  }

  def devMode_=(devMode: Boolean) {
    devModePrivate = devMode
  }

}