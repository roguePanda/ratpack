/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bleedingwolf.ratpack.request

import javax.servlet.http.HttpServletResponse
import org.mortbay.jetty.HttpHeaders

class ResponderDsl {

  final Request request
  final Response response

  ResponderDsl(Request request, Response response) {
    this.request = request
    this.response = response
  }

  String render(Map<String, ?> context = [:], String templateName) {
    response.render(context, templateName)
  }

  String renderJson(Object obj) {
    response.renderJson(obj)
  }

  String renderString(String string) {
    response.renderString(string)
  }
  
  /**
   * Sends a temporary redirect response to the client using the specified redirect location URL.
   * @param location the redirect location URL
   */
  void sendRedirect(String location) {
    response.status = HttpServletResponse.SC_MOVED_TEMPORARILY
    response.headers[HttpHeaders.LOCATION] = new URL(new URL(request.servletRequest.requestURL.toString()), location).toString()
  }

}