/*
 * MenuVO.java         2014年10月8日 <br/>
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

package cn.lonsun.common.vo;

import java.io.Serializable;
import java.util.List;


/**
 * 菜单、按钮VO
 *
 * @author zhusy
 * @date 2014年10月8日
 * @version v1.0
 */
public class MenuVO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7573905744894698413L;

	/**
     * 菜单ID
     */
    public Long indicatorId;

    /**
     * 菜单名称
     */
    public String name;

    /**
     * 是否激活
     */
    public boolean isShowSons;

    /**
     * 链接
     */
    public String uri;

    /**
     * 文字图标
     */
    public String textIcon;

    /**
     * 菜单上显示的数字
     */
    public Integer count;

    /**
     * 子菜单
     */
    public List<MenuVO> items;

    public List<MenuVO> getItems() {
        return items;
    }

    public void setItems(List<MenuVO> items) {
        this.items = items;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setShowSons(boolean isShowSons) {
        this.isShowSons = isShowSons;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsShowSons() {
        return isShowSons;
    }

    public void setIsShowSons(boolean isShowSons) {
        this.isShowSons = isShowSons;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTextIcon() {
        return textIcon;
    }

    public void setTextIcon(String textIcon) {
        this.textIcon = textIcon;
    }

}

