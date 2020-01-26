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
package org.apache.ibatis.logging.commons;

import org.apache.ibatis.logging.slf4j.Slf4jImpl;

/**
 * @author Clinton Begin
 */
public class JakartaCommonsLoggingImpl implements org.apache.ibatis.logging.Log {

  
  private Slf4jImpl slf;

  public JakartaCommonsLoggingImpl(String clazz) {
    this.slf=new Slf4jImpl(clazz);
  }

  @Override
  public boolean isDebugEnabled() {
    return slf.isDebugEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return slf.isTraceEnabled();
  }

  @Override
  public void error(String s, Throwable e) {
    slf.error(s,e);
  }

  @Override
  public void error(String s) {
    slf.error(s);
  }

  @Override
  public void debug(String s) {
    slf.debug(s);
  }

  @Override
  public void trace(String s) {
    slf.trace(s);
  }

  @Override
  public void warn(String s) {
    slf.warn(s);
  }

}
