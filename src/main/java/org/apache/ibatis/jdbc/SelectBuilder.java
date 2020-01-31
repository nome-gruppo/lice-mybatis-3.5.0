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
 * @author Clinton Begin
 */
@Deprecated
public class SelectBuilder {

  private static final ThreadLocal<SQL> localSQL = new ThreadLocal<>();

  static {
    begin();
  }

  private SelectBuilder() {
    // Prevent Instantiation
  }

  public static void begin() {
    RESET();
  }

  public static void RESET() {
    localSQL.set(new SQL());
  }

  public static void SELECT(String columns) {
    sqlMethod().select(columns);
  }

  public static void SELECT_DISTINCT(String columns) {
    sqlMethod().selectDistinct(columns);
  }

  public static void FROM(String table) {
    sqlMethod().FROM(table);
  }

  public static void JOIN(String join) {
    sqlMethod().JOIN(join);
  }

  public static void INNER_JOIN(String join) {
    sqlMethod().INNER_JOIN(join);
  }

  public static void LEFT_OUTER_JOIN(String join) {
    sqlMethod().LEFT_OUTER_JOIN(join);
  }

  public static void RIGHT_OUTER_JOIN(String join) {
    sqlMethod().RIGHT_OUTER_JOIN(join);
  }

  public static void OUTER_JOIN(String join) {
    sqlMethod().OUTER_JOIN(join);
  }

  public static void WHERE(String conditions) {
    sqlMethod().WHERE(conditions);
  }

  public static void OR() {
    sqlMethod().OR();
  }

  public static void AND() {
    sqlMethod().AND();
  }

  public static void GROUP_BY(String columns) {
    sqlMethod().GROUP_BY(columns);
  }

  public static void HAVING(String conditions) {
    sqlMethod().HAVING(conditions);
  }

  public static void ORDER_BY(String columns) {
    sqlMethod().ORDER_BY(columns);
  }

  public static String SQL() {
    try {
      return sqlMethod().toString();
    } finally {
      RESET();
    }
  }

  private static SQL sqlMethod() {
    return localSQL.get();
  }

}
