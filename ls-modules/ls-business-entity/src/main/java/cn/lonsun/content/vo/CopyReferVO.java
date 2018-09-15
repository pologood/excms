/*
 * CopyReferVO.java         2016年5月27日 <br/>
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

package cn.lonsun.content.vo;

/**
 * 复制引用vo <br/>
 *
 * @date 2016年5月27日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class CopyReferVO {

    private Long publicSiteId;//信息公开站点id
    private String contentId;// 文章id
    private Long type;// 复制or引用 1为复制，其余为引用
    private Long source;// 来源 1为内容管理，2为信息公开
    private String synColumnIds;// 同步到栏目的ID
    private String synColumnIsPublishs;//同步到栏目是否发布
    private String synOrganCatIds;// 同步到单位目录的ID
    private String synOrganIsPublishs;//同步到单位是否发布
    private String synMsgCatIds;// 同步到消息分类的ID
    private boolean synWeixin;// 是否同步到微信素材
    private Integer isColumnOpt = 0;//是否是栏目复制或者引用0-否 1-是

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public String getSynColumnIds() {
        return synColumnIds;
    }

    public void setSynColumnIds(String synColumnIds) {
        this.synColumnIds = synColumnIds;
    }

    public String getSynColumnIsPublishs() {
        return synColumnIsPublishs;
    }

    public void setSynColumnIsPublishs(String synColumnIsPublishs) {
        this.synColumnIsPublishs = synColumnIsPublishs;
    }

    public String getSynOrganCatIds() {
        return synOrganCatIds;
    }

    public void setSynOrganCatIds(String synOrganCatIds) {
        this.synOrganCatIds = synOrganCatIds;
    }

    public String getSynOrganIsPublishs() {
        return synOrganIsPublishs;
    }

    public void setSynOrganIsPublishs(String synOrganIsPublishs) {
        this.synOrganIsPublishs = synOrganIsPublishs;
    }

    public String getSynMsgCatIds() {
        return synMsgCatIds;
    }

    public void setSynMsgCatIds(String synMsgCatIds) {
        this.synMsgCatIds = synMsgCatIds;
    }

    public boolean isSynWeixin() {
        return synWeixin;
    }

    public void setSynWeixin(boolean synWeixin) {
        this.synWeixin = synWeixin;
    }

    public Long getPublicSiteId() {
        return publicSiteId;
    }

    public void setPublicSiteId(Long publicSiteId) {
        this.publicSiteId = publicSiteId;
    }

    public Integer getIsColumnOpt() {
        return isColumnOpt;
    }

    public void setIsColumnOpt(Integer isColumnOpt) {
        this.isColumnOpt = isColumnOpt;
    }
}