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
package org.apache.ibatis.cache.decorators;

import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * @author Clinton Begin
 */
public class LoggingCache implements Cache {

  private final Log log;
  private final Cache delegate;
  protected int requests = 0;
  protected int hits = 0;

  public LoggingCache(Cache delegate) {
    this.delegate = delegate;
    this.log = LogFactory.getLog(getId());
  }


  public String getId() {
    return delegate.getId();
  }


  public int getSize() {
    return delegate.getSize();
  }


  public void putObject(Object mKey, Object object) {
    delegate.putObject(mKey, object);
  }


  public Object getObject(Object mKey) {
    requests++;
    final Object value = delegate.getObject(mKey);
    if (value != null) {
      hits++;
    }
    if (log.isDebugEnabled()) {
      log.debug("Cache Hit Ratio [" + getId() + "]: " + getHitRatio());
    }
    return value;
  }


  public Object removeObject(Object mKey) {
    return delegate.removeObject(mKey);
  }


  public void clear() {
    delegate.clear();
  }


  public ReadWriteLock getReadWriteLock() {
    return null;
  }


  public int hashCode() {
    return delegate.hashCode();
  }


  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  private double getHitRatio() {
    return (double) hits / (double) requests;
  }

}
