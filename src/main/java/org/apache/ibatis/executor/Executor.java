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
package org.apache.ibatis.executor;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/**
 * Executor：Executor接口共有两个实现类，分别是BaseExecutor和CachingExecutor，CachingExecutor是缓存执行器
 * sql的具体执行是通过调用SqlSession接口的对应的方法去执行的，而SqlSession最终都是通过调用了自己的Executor对象的query和update去执行的。
 * Executor：是mybatis的一个核心接口。mybatis中的执行器，会根据mybatis解析Mapper.xml文件来选择相应的执行类型
 * 所有的Mapper语句的执行都是通过Executor进行的。
 * <p>
 * Executor定义了各种处理方法。
 *
 * Executor的主要作用：sql的具体执行是通过调用SqlSession接口的对应的方法去执行的，而SqlSession最终都是通过调用了自己的Executor对象的query和update去执行的。
 *
 * Executor的实现类时在Configuration类中
 *
 * @author Clinton Begin
 */
 public interface Executor {
      ResultHandler NO_RESULT_HANDLER = null;

     int update(MappedStatement ms, Object parameter) throws SQLException;

     <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException;

      <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;
      <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException;

      List<BatchResult> flushStatements() throws SQLException;

      void commit(boolean required) throws SQLException;

      void rollback(boolean required) throws SQLException;

      CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql);

      boolean isCached(MappedStatement ms, CacheKey key);

      void clearLocalCache();

      void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType);

      Transaction getTransaction();

      void close(boolean forceRollback);

     boolean isClosed();

      void setExecutorWrapper(Executor executor);
 }
