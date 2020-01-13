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
public class SqlBuilder {

  private static final ThreadLocal<SQL> localSQL = new ThreadLocal<>();

  static {
    BEGIN();
  }

  private SqlBuilder() {
    // Prevent Instantiation
  }

  public static void BEGIN() {
    RESET();
  }

  public static void RESET() {
    localSQL.set(new SQL());
  }

  public static void UPDATE(String table) {
    sqlMethodSqlBuilder().UPDATE(table);
  }

  public static void SET(String sets) {
    sqlMethodSqlBuilder().SET(sets);
  }

  public static String SQL() {
    try {
      return sqlMethodSqlBuilder().toString();
    } finally {
        RESET();
    }
  }

  public static void INSERT_INTO(String tableName) {
    sqlMethodSqlBuilder().INSERT_INTO(tableName);
  }

  public static void VALUES(String columns, String values) {
    sqlMethodSqlBuilder().VALUES(columns, values);
  }

  public static void SELECT(String columns) {
    sqlMethodSqlBuilder().SELECT(columns);
  }

  public static void SELECT_DISTINCT(String columns) {
    sqlMethodSqlBuilder().SELECT_DISTINCT(columns);
  }

  public static void DELETE_FROM(String table) {
    sqlMethodSqlBuilder().DELETE_FROM(table);
  }

  public static void FROM(String table) {
    sqlMethodSqlBuilder().FROM(table);
  }

  public static void JOIN(String join) {
    sqlMethodSqlBuilder().JOIN(join);
  }

  public static void INNER_JOIN(String join) {
    sqlMethodSqlBuilder().INNER_JOIN(join);
  }

  public static void LEFT_OUTER_JOIN(String join) {
    sqlMethodSqlBuilder().LEFT_OUTER_JOIN(join);
  }

  public static void RIGHT_OUTER_JOIN(String join) {
    sqlMethodSqlBuilder().RIGHT_OUTER_JOIN(join);
  }

  public static void OUTER_JOIN(String join) {
    sqlMethodSqlBuilder().OUTER_JOIN(join);
  }

  public static void WHERE(String conditions) {
    sqlMethodSqlBuilder().WHERE(conditions);
  }

  public static void OR() {
    sqlMethodSqlBuilder().OR();
  }

  public static void AND() {
    sqlMethodSqlBuilder().AND();
  }

  public static void GROUP_BY(String columns) {
    sqlMethodSqlBuilder().GROUP_BY(columns);
  }

  public static void HAVING(String conditions) {
    sqlMethodSqlBuilder().HAVING(conditions);
  }

  public static void ORDER_BY(String columns) {
    sqlMethodSqlBuilder().ORDER_BY(columns);
  }

  private static SQL sqlMethodSqlBuilder() {
    return localSQL.get();
  }

}
