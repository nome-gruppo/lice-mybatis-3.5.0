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
package org.apache.ibatis.logging.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.ibatis.logging.Log;

/**
 * ResultSet proxy to add logging
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 *
 */
public final class ResultSetLogger extends BaseJdbcLogger implements InvocationHandler {

  private static Set<Integer> blobTypes = new HashSet<>();
  private boolean first = true;
  private int rows;
  private final ResultSet rs;
  private final Set<Integer> blobColumns = new HashSet<>();
  private Logger log = Logger.getLogger("Logger");

  static {
    blobTypes.add(Types.BINARY);
    blobTypes.add(Types.BLOB);
    blobTypes.add(Types.CLOB);
    blobTypes.add(Types.LONGNVARCHAR);
    blobTypes.add(Types.LONGVARBINARY);
    blobTypes.add(Types.LONGVARCHAR);
    blobTypes.add(Types.NCLOB);
    blobTypes.add(Types.VARBINARY);
  }

  private ResultSetLogger(ResultSet rs, Log statementLog, int queryStack) {
    super(statementLog, queryStack);
    this.rs = rs;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] params) throws Exception {
    try {
      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(this, params);
      }
      Object o = method.invoke(rs, params);
      if ("next".equals(method.getName())) {
        if ((Boolean) o) {
          rows++;
          if (isTraceEnabled()) {
            ResultSetMetaData rsmd = rs.getMetaData();
            final int columnCount = rsmd.getColumnCount();
            if (first) {
              first = false;
              printColumnHeaders(rsmd, columnCount);
            }
            printColumnValues(columnCount);
          }
        } else {
          debug("     Total: " + rows, false);
        }
      }
      clearColumnInfo();
      return o;
    } catch (Exception t) {
      log.log(Level.WARNING, "exception", t);
      throw t;
    }
  }

  private void printColumnHeaders(ResultSetMetaData rsmd, int columnCount) throws SQLException {
    StringBuilder row = new StringBuilder();
    row.append("   Columns: ");
    for (int i = 1; i <= columnCount; i++) {
      if (blobTypes.contains(rsmd.getColumnType(i))) {
        blobColumns.add(i);
      }
      String colname = rsmd.getColumnLabel(i);
      row.append(colname);
      if (i != columnCount) {
        row.append(", ");
      }
    }
    trace(row.toString(), false);
  }

  private void printColumnValues(int columnCount) {
    StringBuilder row = new StringBuilder();
    row.append("       Row: ");
    for (int i = 1; i <= columnCount; i++) {
      String colname;
      try {
        if (blobColumns.contains(i)) {
          colname = "<<BLOB>>";
        } else {
          colname = rs.getString(i);
        }
      } catch (SQLException e) {
        // generally can't call getString() on a BLOB column
        colname = "<<Cannot Display>>";
      }
      row.append(colname);
      if (i != columnCount) {
        row.append(", ");
      }
    }
    trace(row.toString(), false);
  }

  /**
   * Creates a logging version of a ResultSet
   *
   * @param rs - the ResultSet to proxy
   * @return - the ResultSet with logging
   */
  public static ResultSet newInstance(ResultSet rs, Log statementLog, int queryStack) {
    InvocationHandler handler = new ResultSetLogger(rs, statementLog, queryStack);
    ClassLoader cl = ResultSet.class.getClassLoader();
    return (ResultSet) Proxy.newProxyInstance(cl, new Class[]{ResultSet.class}, handler);
  }

  /**
   * Get the wrapped result set
   *
   * @return the resultSet
   */
  public ResultSet getRs() {
    return rs;
  }

}
