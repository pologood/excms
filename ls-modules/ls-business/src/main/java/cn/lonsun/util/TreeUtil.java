/*
 * TreeUtil.java         2015年8月25日 <br/>
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
package cn.lonsun.util;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.lonsun.tree.TreeNode;

/**
 * 
 * 构造树工具类 ADD REASON. <br/>
 *
 * @date: 2015年8月25日 上午11:34:21 <br/>
 * @author fangtinghua
 */
public class TreeUtil {

    /**
     * 构造树型结构
     * 
     * @param treeNodes
     * @return 所有根节点
     */
    public static <T extends TreeNode> List<T> buildTree(List<T> treeNodes) {
        List<T> rootList = new LinkedList<T>();

        if (null == treeNodes || treeNodes.isEmpty()) {
            return rootList;
        }

        // 收集树节点信息
        Map<Long, T> idTreeNodeMap = new LinkedHashMap<Long, T>();// id-node
        for (T t : treeNodes) {
            idTreeNodeMap.put(t.getId(), t);
        }

        // 找出所有非树干节点（设备等）所对应的树干id
        Set<Long> pathSet = new HashSet<Long>();
        for (T t : treeNodes) {
            List<Long> path = getPath(t, idTreeNodeMap, null);
            t.setPath(path);
            pathSet.addAll(path);
        }

        // 构造树
        for (T treeNode : treeNodes) {
            if (!pathSet.contains(treeNode.getId())) {// 如果不是非树干节点所必需的树干，跳过
                continue;
            }

            T pNode = idTreeNodeMap.get(treeNode.getpId());
            if (pNode == null) {
                rootList.add(treeNode);
            } else {
                List<TreeNode> children = pNode.getChildren();
                if (children == null) {
                    children = new LinkedList<TreeNode>();
                    pNode.setChildren(children);
                }
                children.add(treeNode);
            }
        }
        return rootList;
    }

    /**
     * 
     * 获取树节点路径
     *
     * @author fangtinghua
     * @param treeNode
     * @param idTreeNodeMap
     * @param path
     * @return
     */
    private static <T extends TreeNode> LinkedList<Long> getPath(T t, Map<Long, T> idTreeNodeMap, LinkedList<Long> path) {
        if (path == null) {
            path = new LinkedList<Long>();
        }
        path.addFirst(t.getId());

        T parentNode = idTreeNodeMap.get(t.getpId());
        if (parentNode != null) {
            getPath(parentNode, idTreeNodeMap, path);
        }
        return path;
    }
}