package cn.lonsun.site.site.vo;


import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-25 <br/>
 */
public class SiteVO {
    private Long siteConfigId;//配置表ID

    private String siteTitle;//站点全称
    private String keyWords;//关键词

    private String description;//描述

    private String videoTransUrl;//视频转换地址

    private Integer isVideoTrans=0;//是否启用视频转换


    private Long indicatorId;//主表ID

    private String name;//站点名称

    private String uri;//域名访问地址

    private Long parentId;//父节点

    private Integer sortNum;//排序

    private String unitIds;//绑定的单位Id

    private String unitNames;//绑定的单位名称

    private Integer isParent = Integer.valueOf(0);//是否为父节点

    private String recordStatus= AMockEntity.RecordStatus.Normal.toString();//记录状态

    private Boolean open= Boolean.FALSE;

    private String icon;//图标

    private String type;//类型

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createDate = new Date();//创建时间

    private boolean isLeaf;

    private List<FunctionEO> functions;

    private String opt = "super";

    private boolean checked;

    private Long indexTempId;//首页模板

    private Long commentTempId;//评论模板

    private Long errorTempId;//纠错模板

    private Long publicTempId;//信息公开模板

    private Long searchTempId;//站点搜索模板

    private Long memberId;//会员中心模板

    private String stationId;//站长管理的站点Id

    private String stationPwd;//查询密码

    private Long comColumnId;//绑定公共栏目

    public Long getSiteConfigId() {
        return siteConfigId;
    }

    public void setSiteConfigId(Long siteConfigId) {
        this.siteConfigId = siteConfigId;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getVideoTransUrl() {
        return videoTransUrl;
    }

    public void setVideoTransUrl(String videoTransUrl) {
        this.videoTransUrl = videoTransUrl;
    }


    public Integer getIsVideoTrans() {
        return isVideoTrans;
    }

    public void setIsVideoTrans(Integer isVideoTrans) {
        this.isVideoTrans = isVideoTrans;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Integer getIsParent() {
        return isParent;
    }

    public void setIsParent(Integer isParent) {
        this.isParent = isParent;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public List<FunctionEO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionEO> functions) {
        this.functions = functions;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(String unitIds) {
        this.unitIds = unitIds;
    }

    public String getUnitNames() {
        return unitNames;
    }

    public void setUnitNames(String unitNames) {
        this.unitNames = unitNames;
    }

    public Long getIndexTempId() {
        return indexTempId;
    }

    public void setIndexTempId(Long indexTempId) {
        this.indexTempId = indexTempId;
    }

    public Long getCommentTempId() {
        return commentTempId;
    }

    public void setCommentTempId(Long commentTempId) {
        this.commentTempId = commentTempId;
    }

    public Long getErrorTempId() {
        return errorTempId;
    }

    public void setErrorTempId(Long errorTempId) {
        this.errorTempId = errorTempId;
    }

    public Long getPublicTempId() {
        return publicTempId;
    }

    public Long getSearchTempId() {
        return searchTempId;
    }

    public void setSearchTempId(Long searchTempId) {
        this.searchTempId = searchTempId;
    }

    public void setPublicTempId(Long publicTempId) {
        this.publicTempId = publicTempId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationPwd() {
        return stationPwd;
    }

    public void setStationPwd(String stationPwd) {
        this.stationPwd = stationPwd;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getSiteTitle() {
        return siteTitle;
    }

    public void setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
    }

    public Long getComColumnId() {
        return comColumnId;
    }

    public void setComColumnId(Long comColumnId) {
        this.comColumnId = comColumnId;
    }
}
