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
package org.apache.ibatis.builder.annotation;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.StatementType;

public class MapperAnnotationBuilderSupport{
  private KeyGenerator keyGenerator;
  private String keyProperty;
  private String keyColumn;
  private boolean useCache;
  private boolean flushCache;
  private Integer fetchSize;
  private Integer timeout;
  private ResultSetType resultSetType;
  private StatementType statementType;

  public MapperAnnotationBuilderSupport(boolean flushCache,boolean useCache,Integer fetchSize,Integer timeout,ResultSetType resultSetType,StatementType statementType){
    this.flushCache=flushCache;
    this.useCache=useCache;
    this.fetchSize=fetchSize;
    this.timeout=timeout;
    this.resultSetType=resultSetType;
    this.statementType=statementType;
  }

  public MapperAnnotationBuilderSupport(KeyGenerator keyGenerator, String keyProperty, String keyColumn){
    this.keyGenerator=keyGenerator;
    this.keyProperty=keyProperty;
    this.keyColumn=keyColumn;
  }

  public MapperAnnotationBuilderSupport(){
    this.keyGenerator=null;
    this.keyProperty=null;
    this.keyColumn=null;
  }

  public KeyGenerator getKeyGenerator(){
    return this.keyGenerator;
  }

  public String getKeyProperty(){
    return this.keyProperty;
  }

  public String getKeyColumn(){
    return this.keyColumn;
  }

  public boolean getFlush(){
    return this.flushCache;
  }

  public boolean getUse(){
    return this.useCache;
  }

  public Integer getFetch(){
    return this.fetchSize;
  }

  public Integer getTimeout(){
    return this.timeout;
  }

  public ResultSetType getResultSetType(){
    return this.resultSetType;
  }

  public StatementType getStatement(){
    return this.statementType;
  }



}
