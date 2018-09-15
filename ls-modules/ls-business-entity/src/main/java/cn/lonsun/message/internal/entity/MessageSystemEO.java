/*
 * MessageInfo.java         2015年11月30日 <br/>
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

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * 系统消息 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年11月30日 <br/>
 */
@Entity
@Table(name = "CMS_MESSAGE_SYSTEM")
public class MessageSystemEO extends ABaseEntity {

    public static final Long TIP = 1L;// 提示消息
    public static final Long HANDLE = 2L;// 需要处理
    public static final Long OTHER = 3L;// 其他

    public enum MessageStatus {
        success, error, warning, info
    }

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;// 主键
    @Column(name = "TITLE")
    private String title;// 消息标题
    @Column(name = "CONTENT")
    private String content;// 消息内容
    @Column(name = "LINK")
    private String link;// 消息链接
    @Column(name = "MODE_CODE")
    private String modeCode;// 模块编号
    @Column(name = "SITE_ID")
    private Long siteId;// 站点id
    @Column(name = "COLUMN_ID")
    private Long columnId;// 栏目id
    @Column(name = "RESOURCE_ID")
    private Long resourceId;// 消息具体内容id
    @Column(name = "MESSAGE_TYPE")
    private Long messageType;// 消息类型
    @Column(name = "MESSAGE_STATUS")
    private String messageStatus;// 消息状态，success,error
    @Transient
    private String recUserIds;// 消息接收人
    @Transient
    private String recOrganIds;// 消息接受单位
    @Transient
    private Object data;// 业务数据对象
    @Transient
    private boolean todb = true;// 是否插入数据库，默认入库
    @Transient
    private Long[] contentIds;// 文章id数组
    @Transient
    private Integer isPublish = 0;// 发布状态

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getModeCode() {
        return modeCode;
    }

    public void setModeCode(String modeCode) {
        this.modeCode = modeCode;
    }

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

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getMessageType() {
        return messageType;
    }

    public void setMessageType(Long messageType) {
        this.messageType = messageType;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getRecUserIds() {
        return recUserIds;
    }

    public void setRecUserIds(String recUserIds) {
        this.recUserIds = recUserIds;
    }

    public String getRecOrganIds() {
        return recOrganIds;
    }

    public void setRecOrganIds(String recOrganIds) {
        this.recOrganIds = recOrganIds;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isTodb() {
        return todb;
    }

    public void setTodb(boolean todb) {
        this.todb = todb;
    }

    public Long[] getContentIds() {
        return contentIds;
    }

    public void setContentIds(Long[] contentIds) {
        this.contentIds = contentIds;
    }

    public Integer getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }
}