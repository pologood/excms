/*
 * MessageReceiveEO.java         2015年11月30日 <br/>
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 消息接受对象 <br/>
 *
 * @date 2015年11月30日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Entity
@Table(name = "CMS_MESSAGE_RECEIVE")
public class MessageReceiveEO extends ABaseEntity {

    public static final Long NO_READ = 1L;// 未读
    public static final Long READ = 2L;// 已读
    public static final Long HAS_HANDLE = 3L;// 已办

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;// 主键
    @Column(name = "MESSAGE_ID")
    private Long messageId;// 消息id
    @Column(name = "REC_USER_ID")
    private Long recUserId;// 消息接收人
    @Column(name = "REC_ORGAN_ID")
    private Long recOrganId;// 消息接受单位
    @Column(name = "MODE_CODE")
    private String modeCode;// 模块编号
    @Transient
    private String modeName;// 模块名称
    @Column(name = "MESSAGE_STATUS")
    private Long messageStatus;// 消息状态
    @Column(name = "MESSAGE_TYPE")
    private Long messageType;// 消息类型

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MESSAGE_ID", updatable = false, insertable = false)
    private MessageSystemEO messageSystemEO;// 关联消息信息
    @Transient
    private String dateDiff;// 时间差

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getRecUserId() {
        return recUserId;
    }

    public void setRecUserId(Long recUserId) {
        this.recUserId = recUserId;
    }

    public Long getRecOrganId() {
        return recOrganId;
    }

    public void setRecOrganId(Long recOrganId) {
        this.recOrganId = recOrganId;
    }

    public String getModeCode() {
        return modeCode;
    }

    public void setModeCode(String modeCode) {
        this.modeCode = modeCode;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public Long getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Long messageStatus) {
        this.messageStatus = messageStatus;
    }

    public Long getMessageType() {
        return messageType;
    }

    public void setMessageType(Long messageType) {
        this.messageType = messageType;
    }

    public MessageSystemEO getMessageSystemEO() {
        return messageSystemEO;
    }

    public void setMessageSystemEO(MessageSystemEO messageSystemEO) {
        this.messageSystemEO = messageSystemEO;
    }

    public String getDateDiff() {
        return dateDiff;
    }

    public void setDateDiff(String dateDiff) {
        this.dateDiff = dateDiff;
    }
}