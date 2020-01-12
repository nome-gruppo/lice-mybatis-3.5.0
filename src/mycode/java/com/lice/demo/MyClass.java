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
 * description: MyClass <br>
 * date: 2019/8/21 22:54 <br>
 * author: lc <br>
 * version: 1.0 <br>
 */
public class MyClass {

    private MyInterface myInterface;

    public void print() {
        myInterface.print();
    }

    public static void main(String[] args) {
        MyClass myClass = new MyClass();
        myClass.print();
    }
}
