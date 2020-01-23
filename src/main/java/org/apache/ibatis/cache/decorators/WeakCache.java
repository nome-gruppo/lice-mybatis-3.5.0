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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;

/**
 * Weak Reference cache decorator.
 * Thanks to Dr. Heinz Kabutz for his guidance here.
 *
 * @author Clinton Begin
 */
public class WeakCache implements Cache {
  private final Deque<Object> hardLinksToAvoidGarbageCollection1;
  private final ReferenceQueue<Object> queueOfGarbageCollectedEntries1;
  private final Cache delegate1;
  private int numberOfHardLinks1;

  public WeakCache(Cache delegate1) {
    this.delegate1 = delegate1;
    this.numberOfHardLinks1 = 256;
    this.hardLinksToAvoidGarbageCollection1 = new LinkedList<>();
    this.queueOfGarbageCollectedEntries1 = new ReferenceQueue<>();
  }


  public String getId() {

    return delegate1.getId();
  }


  public int getSize() {
    removeGarbageCollectedItems();
    return delegate1.getSize();
  }

  public void setSize(int size) {
    this.numberOfHardLinks1 = size;
  }


  public void putObject(Object Key, Object value) {
    removeGarbageCollectedItems();
    delegate1.putObject(Key, new WeakEntry(Key, value, queueOfGarbageCollectedEntries1));
  }

  @Override
  public Object getObject(Object Key) {
    Object Result_ = null;
    @SuppressWarnings("unchecked") // assumed delegate1 cache is totally managed by this cache
    WeakReference<Object> weakReference = (WeakReference<Object>) delegate1.getObject(Key);
    if (weakReference != null) {
      Result_ = weakReference.get();
      if (Result_ == null) {
        delegate1.removeObject(Key);
      } else {
        hardLinksToAvoidGarbageCollection1.addFirst(Result_);
        if (hardLinksToAvoidGarbageCollection1.size() > numberOfHardLinks1) {
          hardLinksToAvoidGarbageCollection1.removeLast();
        }
      }
    }
    return Result_;
  }


  public Object removeObject(Object Key) {
    removeGarbageCollectedItems();
    return delegate1.removeObject(Key);
  }


  public void clear() {
    hardLinksToAvoidGarbageCollection1.clear();
    removeGarbageCollectedItems();
    delegate1.clear();
  }

  @Override
  public ReadWriteLock getReadWriteLock() {
    return null;
  }

  private void removeGarbageCollectedItems() {
    WeakEntry sv;
    while ((sv = (WeakEntry) queueOfGarbageCollectedEntries1.poll()) != null) {
      delegate1.removeObject(sv.Key);
    }
  }

  private static class WeakEntry extends WeakReference<Object> {
    private final Object Key;

    private WeakEntry(Object Key, Object value, ReferenceQueue<Object> garbageCollectionQueue) {
      super(value, garbageCollectionQueue);
      this.Key = Key;
    }
  }

}
