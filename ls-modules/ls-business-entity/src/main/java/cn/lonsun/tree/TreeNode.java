/*
 * TreeNode.java         2015年8月25日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved. <br/>
 *
 * This software is the confidential and proprietary information of AnHui   <br/>
 * LonSun, Inc. ("Confidential Information").  You shall not    <br/>
 * disclose such Confidential Information and shall use it only in  <br/>
 * accordance with the terms of the license agreement you entered into  <br/>
 * with Sun. <br/>
 */
package cn.lonsun.tree;

import java.util.List;

/**
 * 树节点 <br/>
 *
 * @date: 2015年8月25日 上午11:31:28 <br/>
 * @author fangtinghua
 */
public class TreeNode<T extends TreeNode> {

    private Long id;// id
    private Long pId;// 父id
    private List<Long> path;// 树节点完整路径id，有序，含自己的节点
    private List<T> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public List<Long> getPath() {
        return path;
    }

    public void setPath(List<Long> path) {
        this.path = path;
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }
}