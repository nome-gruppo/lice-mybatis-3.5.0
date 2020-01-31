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

import org.junit.jupiter.api.Test;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlBuilderTest {

  @Test
  public void shouldProduceExpectedSimpleSelectStatement() {
    String expected =
        "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" +
            "FROM PERSON P\n" +
            "WHERE (P.ID like #id# AND P.FIRST_NAME like #firstName# AND P.LAST_NAME like #lastName#)\n" +
            "ORDER BY P.LAST_NAME";
    assertEquals(expected, example2("a", "b", "c"));
  }

  @Test
  public void shouldProduceExpectedSimpleSelectStatementMissingFirstParam() {
    String expected =
        "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" +
            "FROM PERSON P\n" +
            "WHERE (P.FIRST_NAME like #firstName# AND P.LAST_NAME like #lastName#)\n" +
            "ORDER BY P.LAST_NAME";
    assertEquals(expected, example2(null, "b", "c"));
  }

  @Test
  public void shouldProduceExpectedSimpleSelectStatementMissingFirstTwoParams() {
    String expected =
        "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" +
            "FROM PERSON P\n" +
            "WHERE (P.LAST_NAME like #lastName#)\n" +
            "ORDER BY P.LAST_NAME";
    assertEquals(expected, example2(null, null, "c"));
  }

  @Test
  public void shouldProduceExpectedSimpleSelectStatementMissingAllParams() {
    String expected =
        "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" +
            "FROM PERSON P\n" +
            "ORDER BY P.LAST_NAME";
    assertEquals(expected, example2(null, null, null));
  }

  @Test
  public void shouldProduceExpectedComplexSelectStatement() {
    String expected =
        "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME, P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON\n" +
            "FROM PERSON P, ACCOUNT A\n" +
            "INNER JOIN DEPARTMENT D on D.ID = P.DEPARTMENT_ID\n" +
            "INNER JOIN COMPANY C on D.COMPANY_ID = C.ID\n" +
            "WHERE (P.ID = A.ID AND P.FIRST_NAME like ?) \n" +
            "OR (P.LAST_NAME like ?)\n" +
            "GROUP BY P.ID\n" +
            "HAVING (P.LAST_NAME like ?) \n" +
            "OR (P.FIRST_NAME like ?)\n" +
            "ORDER BY P.ID, P.FULL_NAME";
    assertEquals(expected, example1());
  }

  private static String example1() {
    select("P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME");
    select("P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON");
    from("PERSON P");
    from("ACCOUNT A");
    innerJoin("DEPARTMENT D on D.ID = P.DEPARTMENT_ID");
    innerJoin("COMPANY C on D.COMPANY_ID = C.ID");
    where("P.ID = A.ID");
    where("P.FIRST_NAME like ?");
    or();
    where("P.LAST_NAME like ?");
    groupBy("P.ID");
    having("P.LAST_NAME like ?");
    or();
    having("P.FIRST_NAME like ?");
    orderBy("P.ID");
    orderBy("P.FULL_NAME");
    return sql();
  }

  private static String example2(String id, String firstName, String lastName) {
    select("P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME");
    from("PERSON P");
    if (id != null) {
      where("P.ID like #id#");
    }
    if (firstName != null) {
      where("P.FIRST_NAME like #firstName#");
    }
    if (lastName != null) {
      where("P.LAST_NAME like #lastName#");
    }
    orderBy("P.LAST_NAME");
    return sql();
  }

}
