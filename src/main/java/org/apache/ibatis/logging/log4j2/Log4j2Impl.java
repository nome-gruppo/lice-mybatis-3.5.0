/**
 *    Copyright 2009-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.logging.log4j2;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
/**
 * @author Eduardo Macarron
 */
public class Log4j2Impl implements Log {

  private Slf4jImpl slf123;

  public Log4j2Impl(String clazz) {

    this.slf123=new Slf4jImpl(clazz);
  }


  public boolean isDebugEnabled() {
    return slf123.isDebugEnabled();
  }


  public boolean isTraceEnabled() {
    return slf123.isTraceEnabled();
  }


  public void error(String s, Throwable e) {
    slf123.error(s,e);
  }


  public void error(String s) {
    slf123.error(s);
  }


  public void debug(String s) {
    slf123.debug(s);
  }


  public void trace(String s) {
    slf123.trace(s);
  }


  public void warn(String s) {
    slf123.warn(s);
  }

}
