/*
 * PublicContentQueryVO.java         2015年12月16日 <br/>
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

import cn.lonsun.common.vo.PageQueryVO;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 查询对象参数 <br/>
 *
 * @date 2016年9月19日 <br/>
 * @author liukun <br/>
 * @version v1.0 <br/>
 */
public class PublicLeadersQueryVO extends PageQueryVO {

    private Long siteId;// 站点id

    private Long organId;// 单位id

    private String leadersName;// 领导姓名

    private String leadersNum;// 领导编号

    private String post;// 职务

    private String status; //是否启用


    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getLeadersName() {
        return leadersName;
    }

    public void setLeadersName(String leadersName) {
        this.leadersName = leadersName;
    }

    public String getLeadersNum() {
        return leadersNum;
    }

    public void setLeadersNum(String leadersNum) {
        this.leadersNum = leadersNum;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}