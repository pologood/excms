/*
 * HtmlEnum.java         2015年12月30日 <br/>
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

package cn.lonsun.staticcenter.service;

/**
 * 动态请求常亮类 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月30日 <br/>
 */
public enum HtmlEnum {
    /**
     * 模块
     */
    SITE("site"), // 站点
    CONTENT("content"), // 内容协同
    PUBLIC("public"), // 信息公开
    GOVBBS("govbbs"), // 信息公开
    COMMENTS("comments"), // 评论
    CORRECTION("correction"), // 纠错
    BBS("bbs"),
    MEMBER("member"), // 会员管理
    MEMBERYJ("memberyj"), // 阳江会员管理
    /**
     * 动作
     */
    INDEX("index"), // 首页
    COLUMN("column"), // 栏目页
    ACRTILE("article"), // 文章页
    SEARCH("search"), // 全文检索
    HIT("hit"), // 点击数，只针对文章操作
    PAGE("page"), // 上一篇下一篇，只针对文章操作
    SHOW("show"), // 详情
    TPL("tpl"), // 模板
    LABEL("label"), // 标签

    /**
     * 部门信箱
     */
    GOVMB("govmail"),

    /**
     * 会员处理动态页面
     */
    login("login"), // 信用投诉
    register("register"), // 信用投诉
    setPassword("setPassword"), // 密码修改
    center("center"),// 用户中心

    WAP("wap"); // 手机WAP访问

    // 值
    private String value;

    private HtmlEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 模块是否存在
     *
     * @param module
     * @return
     * @author fangtinghua
     */
    public static boolean exitModule(String module) {
        return GOVBBS.getValue().equals(module) || MEMBERYJ.getValue().equals(module) || SITE.getValue().equals(module) || CONTENT.getValue().equals(module) || PUBLIC.getValue().equals(module)
            || COMMENTS.getValue().equals(module) || MEMBER.getValue().equals(module) || SEARCH.getValue().equals(module) || WAP.getValue().equals(module);
    }

    public static boolean exitMemberYj(String module) {
        return login.getValue().equals(module) || center.getValue().equals(module) || register.getValue().equals(module)
            || setPassword.getValue().equals(module);
    }
}