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
package com.lice.mapper;

import com.lice.pojo.Employee;
import org.apache.ibatis.jdbc.SQL;

/**
 * description: EmployeeProvider mybatis使用@Provider注解<br>
 * date: 2019/10/18 3:29 <br>
 * author: lc <br>
 * version: 1.0 <br>
 */
public class EmployeeProvider {

    public String selectById(final Integer empId) {
        return "select emp_id, emp_name,emp_no,emp_birthday,dep_no from tb_employee where emp_id=#{empId}";

    }


}
