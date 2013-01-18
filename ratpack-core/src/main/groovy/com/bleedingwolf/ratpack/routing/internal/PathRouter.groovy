package com.bleedingwolf.ratpack.routing.internal

import com.bleedingwolf.ratpack.request.Request
import com.bleedingwolf.ratpack.request.Responder
import com.bleedingwolf.ratpack.request.internal.ResponderFactory
import groovy.transform.CompileStatic

import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest
import com.bleedingwolf.ratpack.routing.Router

@CompileStatic
class PathRouter implements Router {

  final String method
  final String path

  private Pattern regex
  private List<String> names = []
  ResponderFactory responderFactory

  PathRouter(String path, String method, ResponderFactory responderFactory) {
    this.path = path
    this.method = method.toLowerCase()
    this.responderFactory = responderFactory

    parsePath()
  }

  private void parsePath() {
    String regexString = path

    def placeholderPattern = Pattern.compile("(:\\w+)")
    placeholderPattern.matcher(path).each { List<String> match ->
      def name = match[1][1..-1]
      regexString = regexString.replaceFirst(match[0], "([^/?&#]+)")
      names << name
    }

    regex = Pattern.compile(regexString)
  }

  @Override
  Responder route(HttpServletRequest request) {
    if (request.method.toLowerCase() != method) {
      return null
    }

    def matcher = regex.matcher(request.pathInfo)
    if (matcher.matches()) {
      responderFactory.createResponder(new Request(request, toUrlParams(matcher)))
    } else {
      null
    }
  }

  Map<String, String> toUrlParams(Matcher matcher) {
    def params = [:]
    names.eachWithIndex { String it, Integer i ->
      params[it] = matcher.group(i + 1)
    }
    params
  }

}
