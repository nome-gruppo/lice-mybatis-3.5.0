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
package org.apache.ibatis.logging.stdout;

import org.apache.ibatis.logging.Log;

import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * @author Clinton Begin
 */
public class StdOutImpl implements Log {

  private Logger log = Logger.getLogger("Logger");
  public StdOutImpl(String clazz) {
    // Do Nothing
  }

  @Override
  public boolean isDebugEnabled() {
    return true;
  }

  @Override
  public boolean isTraceEnabled() {
    return true;
  }

  @Override
  public void error(String s, Throwable e) {
    log.log(Level.WARNING, s, e);
  }

  @Override
  public void error(String s) {
    log.info(s);
  }

  @Override
  public void debug(String s) {
    log.info(s);
  }

  @Override
  public void trace(String s) {
    log.info(s);
  }

  @Override
  public void warn(String s) {
    log.info(s);
  }
}
