/*
 * MessageStatic.java         2015年11月30日 <br/>
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

import java.io.Serializable;

/**
 * 生成静态EO <br/>
 *
 * @date 2015年11月30日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class MessageStaticEO implements Serializable {

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     * Creates a new instance of MessageStaticEO.
     *
     */
    public MessageStaticEO() {

    }

    /**
     * Creates a new instance of MessageStaticEO.
     *
     * @param siteId
     * @param columnId
     * @param contentIds
     */
    public MessageStaticEO(Long siteId, Long columnId, Long[] contentIds) {
        super();
        this.siteId = siteId;
        this.columnId = columnId;
        this.contentIds = contentIds;
        // 默认是内容协同
        this.source = MessageEnum.CONTENTINFO.value();
    }

    private Long siteId;// 站点id
    private Long columnId;// 栏目id
    private Long[] contentIds;// 文章内容id数组

    private Long scope;// 1.首页 2.栏目页 3.文章页 针对全站生成情况
    private Long type;// 1.发布 0.取消发布 2.终止 3.重新生成
    private Long source;// 来源 1.内容协同 2.信息公开

    private Long userId;// 用户id
    private boolean todb = false;// 是否入库，当任务使用，默认不入库
    private Long taskId;// 任务id，用来重新生成

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long[] getContentIds() {
        return contentIds;
    }

    public void setContentIds(Long[] contentIds) {
        this.contentIds = contentIds;
    }

    public Long getScope() {
        return scope;
    }

    public MessageStaticEO setScope(Long scope) {
        this.scope = scope;
        return this;
    }

    public Long getType() {
        return type;
    }

    public MessageStaticEO setType(Long type) {
        this.type = type;
        return this;
    }

    public Long getSource() {
        return source;
    }

    public MessageStaticEO setSource(Long source) {
        this.source = source;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public MessageStaticEO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public boolean isTodb() {
        return todb;
    }

    public MessageStaticEO setTodb(boolean todb) {
        this.todb = todb;
        return this;
    }

    public Long getTaskId() {
        return taskId;
    }

    public MessageStaticEO setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }
}