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
package org.apache.ibatis.session;

/**
 * RowBounds：mybatis用于分页的对象，主要的功能是分页。
 * 该分页操作是对ResultSet结果集进行分页，也就是人们常说的逻辑分页，而非物理分页。
 *
 * @author Clinton Begin
 */
public class RowBounds {

    //默认第几条记录开始
    public static final int NO_ROW_OFFSET = 0;
    //默认取多少条
    public static final int NO_ROW_LIMIT = Integer.MAX_VALUE;

    //存储结果集
    public static final RowBounds DEFAULT = new RowBounds();

    //从第一条开始取记录，offset属性是偏移量
    private final int offset;
    //每次取多少条，limit最大值为2147483647==Integer.MAX_VALUE
    private final int limit;

    public RowBounds() {
        this.offset = NO_ROW_OFFSET;
        this.limit = NO_ROW_LIMIT;
    }

    public RowBounds(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

}
