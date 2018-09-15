/*
 * WebServiceEO.java         2016年1月12日 <br/>
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

package cn.lonsun.webservice.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * WebService实体
 * 
 * @author xujh
 * @date 2014年10月23日 下午1:41:58
 * @version V1.0
 */
@Entity
@Table(name = "rbac_webservice")
public class WebServiceEO extends ABaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5829873809633613890L;
    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "web_service_id")
    private Long webServiceId;
    // 系统编码
    @Column(name = "system_code")
    private String systemCode;
    // 所属系统名称
    @Column(name = "system_name")
    private String systemName;
    // 对象唯一标识符
    @Column(name = "code_")
    private String code;
    // 去除服务器地址和端口后的地址
    @Column(name = "uri_")
    private String uri;
    // 服务命名空间
    @Column(name = "name_space")
    private String nameSpace;
    // 方法
    @Column(name = "method_")
    private String method;
    // 描述
    @Column(name = "description_")
    private String description;

    public Long getWebServiceId() {
        return webServiceId;
    }

    public void setWebServiceId(Long webServiceId) {
        this.webServiceId = webServiceId;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}
