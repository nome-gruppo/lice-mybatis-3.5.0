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
package org.apache.ibatis.jdbc;

/**
 * @deprecated Use the {@link SQL} Class
 *
 * @author Jeff Butler
 */
@Deprecated
public class SqlBuilder {

  private static final ThreadLocal<SQL> localSQL = new ThreadLocal<>();

  static {
    begin();
  }

  private SqlBuilder() {
    // Prevent Instantiation
  }

  public static void begin() {
    reset();
  }

  public static void reset() {
    localSQL.set(new SQL());
  }

  public static void update(String table) {
    sqlMethodSqlBuilder().update(table);
  }

  public static void set(String sets) {
    sqlMethodSqlBuilder().set(sets);
  }

  public static String sql() {
    try {
      return sqlMethodSqlBuilder().toString();
    } finally {
        reset();
    }
  }

  public static void insertInto(String tableName) {
    sqlMethodSqlBuilder().insertInto(tableName);
  }

  public static void values(String columns, String values) {
    sqlMethodSqlBuilder().values(columns, values);
  }

  public static void select(String columns) {
    sqlMethodSqlBuilder().select(columns);
  }

  public static void selectDistinct(String columns) {
    sqlMethodSqlBuilder().selectDistinct(columns);
  }

  public static void deleteFrom(String table) {
    sqlMethodSqlBuilder().deleteFrom(table);
  }

  public static void from(String table) {
    sqlMethodSqlBuilder().from(table);
  }

  public static void join(String join) {
    sqlMethodSqlBuilder().JOIN(join);
  }

  public static void innerJoin(String join) {
    sqlMethodSqlBuilder().INNER_JOIN(join);
  }

  public static void leftOuterJoin(String join) {
    sqlMethodSqlBuilder().LEFT_OUTER_JOIN(join);
  }

  public static void rightOuterJoin(String join) {
    sqlMethodSqlBuilder().RIGHT_OUTER_JOIN(join);
  }

  public static void outerJoin(String join) {
    sqlMethodSqlBuilder().OUTER_JOIN(join);
  }

  public static void where(String conditions) {
    sqlMethodSqlBuilder().WHERE(conditions);
  }

  public static void or() {
    sqlMethodSqlBuilder().OR();
  }

  public static void and() {
    sqlMethodSqlBuilder().AND();
  }

  public static void groupBy(String columns) {
    sqlMethodSqlBuilder().GROUP_BY(columns);
  }

  public static void having(String conditions) {
    sqlMethodSqlBuilder().HAVING(conditions);
  }

  public static void orderBy(String columns) {
    sqlMethodSqlBuilder().ORDER_BY(columns);
  }

  private static SQL sqlMethodSqlBuilder() {
    return localSQL.get();
  }

}
