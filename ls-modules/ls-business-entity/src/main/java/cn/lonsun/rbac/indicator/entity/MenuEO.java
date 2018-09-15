/*
 * MenuEo.java         2015年8月25日 <br/>
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

package cn.lonsun.rbac.indicator.entity;

import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.tree.TreeNode;

import java.util.List;

/**
 * TODO <br/>
 *
 * @date 2015年8月25日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class MenuEO extends TreeNode<MenuEO> {

    private String name;
    private String textIcon;
    private String uri;

    private String isEnable;
    private Integer sortNum;
    private boolean checked;
    private Boolean isTop;

    private List<IndicatorEO> rights;

    private String menuCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextIcon() {
        return textIcon;
    }

    public void setTextIcon(String textIcon) {
        this.textIcon = textIcon;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<IndicatorEO> getRights() {
        return rights;
    }

    public void setRights(List<IndicatorEO> rights) {
        this.rights = rights;
    }

    public Boolean getIsTop() {
        return isTop;
    }

    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }
}