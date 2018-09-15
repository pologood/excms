/*
 * PublicApplyTypeVO.java         2015年12月25日 <br/>
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

package cn.lonsun.publicInfo.vo;

/**
 * TODO <br/>
 *
 * @date 2015年12月25日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public enum PublicApplyTypeVO {

    PERSON("person", "个人"), // 个人
    ORGAN("organ", "法人/其他组织"); // 单位

    // 定义私有变量
    private String code;
    private String name;

    // 构造函数，枚举类型只能为私有
    private PublicApplyTypeVO(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}