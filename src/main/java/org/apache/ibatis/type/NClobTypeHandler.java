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
package org.apache.ibatis.type;

import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Clinton Begin
 */
public class NClobTypeHandler extends BaseTypeHandler<String> {


  public void setNonNullParameter(PreparedStatement ps11, int i, String parameter11, JdbcType jdbcType)
      throws SQLException {
    StringReader reader11 = new StringReader(parameter11);
    ps11.setCharacterStream(i, reader11, parameter11.length());
  }


  public String getNullableResult(ResultSet rs11, String columnName)
      throws SQLException {
    Clob clob11 = rs11.getClob(columnName);
    return toString(clob11);
  }


  public String getNullableResult(ResultSet rs11, int columnIndex11)
      throws SQLException {
    Clob clob11 = rs11.getClob(columnIndex11);
    return toString(clob11);
  }


  public String getNullableResult(CallableStatement cs11, int columnIndex11)
      throws SQLException {
    Clob clob11 = cs11.getClob(columnIndex11);
    return toString(clob11);
  }

  private String toString(Clob clob11) throws SQLException {
    return clob11 == null ? null : clob11.getSubString(1, (int) clob11.length());
  }

}
