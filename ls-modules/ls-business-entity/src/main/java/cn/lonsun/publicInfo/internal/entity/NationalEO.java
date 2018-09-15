/*
 * NationalEO.java         2015年12月22日 <br/>
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

package cn.lonsun.publicInfo.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 公民信息 <br/>
 *
 * @date 2015年12月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Entity
@Table(name = "CMS_PUBLIC_NATIONAL")
public class NationalEO extends ABaseEntity {

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "CARD_TYPE")
    private String cardType;// 证件类型
    @Column(name = "CARD_NUM")
    private String cardNum;// 证件号码
    @Column(name = "ORGAN_NAME")
    private String organName;// 工作单位
    @Column(name = "ADDRESS")
    private String address;// 通信地址
    @Column(name = "POSTAL_NUM")
    private String postalNum;// 邮政编码
    @Column(name = "PHONE")
    private String phone;// 电话号码
    @Column(name = "MAIL")
    private String mail;// 电子信箱
    @Column(name = "FAX")
    private String fax;// 传真

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalNum() {
        return postalNum;
    }

    public void setPostalNum(String postalNum) {
        this.postalNum = postalNum;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
}