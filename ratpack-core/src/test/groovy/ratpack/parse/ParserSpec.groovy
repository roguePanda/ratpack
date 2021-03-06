/*
 * Copyright 2013 the original author or authors.
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

package ratpack.parse

import ratpack.handling.Context
import ratpack.http.TypedData
import ratpack.test.internal.RatpackGroovyDslSpec

class ParserSpec extends RatpackGroovyDslSpec {

  static class IntParser extends NoOptParserSupport {
    IntParser() {
      super("text/plain")
    }

    @Override
    <T> T parse(Context context, TypedData requestBody, Class<T> type) throws Exception {
      if (type == Integer) {
        context.request.body.text.toInteger()
      } else {
        return null
      }
    }
  }

  def "can use parsers"() {
    when:
    modules {
      bind IntParser
    }
    handlers {
      post {
        def i = parse Integer
        response.send(i.getClass().toString())
      }
    }

    then:
    request.body("123")
    postText() == Integer.toString()
  }

}
