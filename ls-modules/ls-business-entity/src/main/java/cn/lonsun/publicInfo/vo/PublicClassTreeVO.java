/*
 * PublicClassTreeVO.java         2016年1月5日 <br/>
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

import cn.lonsun.tree.TreeNode;

/**
 * 分类属性结构 <br/>
 *
 * @date 2016年1月5日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class PublicClassTreeVO extends TreeNode<PublicClassTreeVO> {

    private Long siteId;
    private String name;

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}