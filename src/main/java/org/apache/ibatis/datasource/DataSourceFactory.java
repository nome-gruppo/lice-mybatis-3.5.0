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
package org.apache.ibatis.datasource;

import java.util.Properties;
import javax.sql.DataSource;

/**
 * DataSourceFactory：数据库资源工厂，将由jndi、pooled、unpooled三种数据连接工厂实现
 *
 * @author Clinton Begin
 */
public interface DataSourceFactory {

    //设置属性
    void setProperties(Properties props);

    //获取数据库资源，JDK中的接口规范。可以获取数据库连接对象Connection
    DataSource getDataSource();

}
