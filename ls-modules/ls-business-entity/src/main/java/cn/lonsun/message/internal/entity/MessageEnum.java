/*
 * MessageEnum.java         2015年11月26日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.message.internal.entity;

/**
 * 消息常量类 <br/>
 *
 * @date 2015年11月26日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public enum MessageEnum {

    INDEX(1L, "首页"), // 首页
    COLUMN(2L, "栏目页"), // 栏目页
    CONTENT(3L, "文章页"), // 文章页

    PUBLISH(1L, "发布"), // 发布
    UNPUBLISH(0L, "取消发布"), // 取消发布
    OVER(2L, "取消任务"), // 取消任务
    RESTART(3L, "重新开始"), // 重新开始

    CONTENTINFO(1L, "栏目"), // 内容协同
    PUBLICINFO(2L, "部门"); // 信息公开

    // 定义私有变量
    private Long value;
    private String name;

    // 构造函数，枚举类型只能为私有
    private MessageEnum(Long value, String name) {
        this.value = value;
        this.name = name;
    }

    public Long value() {
        return this.value;
    }

    public String getName() {
        return name;
    }
}