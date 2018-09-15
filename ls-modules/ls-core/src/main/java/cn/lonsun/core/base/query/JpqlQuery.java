/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.core.base.query;

import cn.lonsun.core.util.Assert;

/**
 * 基于对象查询语言字符串的查询。
 * 可以指定定位查询参数或命名查询参数，也可以针对查询结果取子集。
 * @author Dzl
 */
public class JpqlQuery extends BaseQuery<JpqlQuery> {
    private final String jpql;

    /**
     * 使用仓储和JPQL语句创建JPQL查询。
     * @param jpql JPQL查询语句
     */
    public JpqlQuery(String jpql) {
        Assert.notBlank(jpql);
        this.jpql = jpql;
    }

    /**
     * 获取JPQL查询语句
     * @return JPQL查询语句
     */
    public String getJpql() {
        return jpql;
    }    
}
