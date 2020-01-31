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
package org.apache.ibatis.executor.resultset;

import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;

public class PendingRelation {
    private MetaObject metaObject;
    private ResultMapping propertyMapping;
 
    public MetaObject getMetaObject(){
      return metaObject;
    }
    public ResultMapping getPropertyMapping(){
      return propertyMapping;
    }
    public void setMetaObject(MetaObject metaObject){
      this.metaObject=metaObject;
    }
    public void setPropertyMapping(ResultMapping propertyMapping){
      this.propertyMapping=propertyMapping;
    }
    
}