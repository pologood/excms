/*
 * 2014-10-28 <br/>
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
package cn.lonsun.webservice.processEngine.vo;


import cn.lonsun.webservice.processEngine.enums.EOperationsType;
import cn.lonsun.webservice.processEngine.enums.EWindowType;

public class Button {
    /**唯一键值*/
    private Long operId;
    /** 编码 */
    private String code;
    /** 名称 */
    private String name;
    /** 图标 */
    private String icon = "";
    /** 链接 */
    private String href;
    /** 描述 */
    private String desc;
    /** 脚本描述 */
    private String scripts;
    /** 类型button-按扭;post-提交按扭;get提交按扭 */
    private EOperationsType type = EOperationsType.button;
    /** 窗体类型(0-弹窗;1-跳转) */
    private Long winType = 0L;
    /** 窗体大小类型 defalut, customize */
    private EWindowType winSize = EWindowType.defalut;
    /** 窗体宽 */
    private String winWidth;
    /** 窗体高 */
    private String winHeight;

    public Button(){}

    public Button (String code,String name){
        this.code  =  code;
        this.name  =  name;
    }

    @Override
    public boolean equals(Object obj) {
        Button button=(Button)obj;
        return null != button ? (code.equals(button.getCode())):false;
    }

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getScripts() {
        return scripts;
    }

    public void setScripts(String scripts) {
        this.scripts = scripts;
    }

    public EOperationsType getType() {
        return type;
    }

    public void setType(EOperationsType type) {
        this.type = type;
    }

    public Long getWinType() {
        return winType;
    }

    public void setWinType(Long winType) {
        this.winType = winType;
    }

    public EWindowType getWinSize() {
        return winSize;
    }

    public void setWinSize(EWindowType winSize) {
        this.winSize = winSize;
    }

    public String getWinWidth() {
        return winWidth;
    }

    public void setWinWidth(String winWidth) {
        this.winWidth = winWidth;
    }

    public String getWinHeight() {
        return winHeight;
    }

    public void setWinHeight(String winHeight) {
        this.winHeight = winHeight;
    }
}
