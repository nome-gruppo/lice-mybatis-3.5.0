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
package com.lice.demo;

/**
 * description: MyInterfaceImpl <br>
 * date: 2019/8/21 22:53 <br>
 * author: lc <br>
 * version: 1.0 <br>
 */
public class MyInterfaceImpl implements MyInterface {
    @Override
    public void print() {
        System.out.println("实现类的方法被调用....");
    }
}
