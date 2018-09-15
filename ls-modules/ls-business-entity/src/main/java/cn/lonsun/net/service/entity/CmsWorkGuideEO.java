package cn.lonsun.net.service.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name = "CMS_NET_WORK_GUIDE")
public class CmsWorkGuideEO extends ABaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "CONTENT_ID")
    private Long contentId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LINK_TYPE")
    private String linkType;

    @Column(name = "LINK_URL")
    private String linkUrl;

    @Column(name = "ORGAN_ID")
    private String organId;

    @Column(name = "JOIN_DATE")
    private String joinDate;

    @Column(name = "TRUN_LINK")
    private String turnLink;

    @Column(name = "PUBLISH")
    private Integer publish = 0;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "ZX_LINK")
    private String zxLink;

    @Column(name = "TS_LINK")
    private String tsLink;

    @Column(name = "SB_LINK")
    private String sbLink;

    @Column(name = "MIN_LOCAL_TIME")
    private Long minLocalTime;

    @Column(name = "SET_ACCORD")
    private String setAccord;

    @Column(name = "APPLY_CONDITION")
    private String applyCondition;

    @Column(name = "HANDLE_DATA")
    private String handleData;

    @Column(name = "HANDLE_PROCESS")
    private String handleProcess;

    @Column(name = "HANDLE_ADDRESS")
    private String handleAddress;

    @Column(name = "HANDLE_DATE")
    private String handleDate;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "SITE_ID")
    private Long siteId;

    @Column(name = "TYPE_CODE")
    private String typeCode;

    @Column(name = "CLICKS")
    private Long clicks = 0L;

    @Column(name = "HANDLE_LIMIT")
    private String handleLimit;

    @Column(name = "FEE_STANDARD")
    private String feeStandard;


    @Transient
    private Long columnId;

    @Transient
    private String columnName;

    @Transient
    private String tableIds;

    @Transient
    private String tableNames;

    @Transient
    private String ruleIds;

    @Transient
    private String ruleNames;

    @Transient
    private String organName;

    @Transient
    private Date publishDate;

    @Transient
    private String uri;

    @Transient
    private String relateTablesUrl;

    @Transient
    private String relateRulesUrl;

    @Transient
    private List<CmsTableResourcesEO> tableResourcesEOs = new ArrayList<CmsTableResourcesEO>();

    @Transient
    private List<CmsRelatedRuleEO> relatedRuleEOs = new ArrayList<CmsRelatedRuleEO>();

    public String getHandleLimit() {
        return handleLimit;
    }

    public void setHandleLimit(String handleLimit) {
        this.handleLimit = handleLimit;
    }

    public String getFeeStandard() {
        return feeStandard;
    }

    public void setFeeStandard(String feeStandard) {
        this.feeStandard = feeStandard;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getTurnLink() {
        return turnLink;
    }

    public void setTurnLink(String turnLink) {
        this.turnLink = turnLink;
    }

    public Integer getPublish() {
        return publish;
    }

    public void setPublish(Integer publish) {
        this.publish = publish;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getZxLink() {
        return zxLink;
    }

    public void setZxLink(String zxLink) {
        this.zxLink = zxLink;
    }

    public String getTsLink() {
        return tsLink;
    }

    public void setTsLink(String tsLink) {
        this.tsLink = tsLink;
    }

    public String getSbLink() {
        return sbLink;
    }

    public void setSbLink(String sbLink) {
        this.sbLink = sbLink;
    }

    public Long getMinLocalTime() {
        return minLocalTime;
    }

    public void setMinLocalTime(Long minLocalTime) {
        this.minLocalTime = minLocalTime;
    }

    public String getSetAccord() {
        return setAccord;
    }

    public void setSetAccord(String setAccord) {
        this.setAccord = setAccord;
    }

    public String getApplyCondition() {
        return applyCondition;
    }

    public void setApplyCondition(String applyCondition) {
        this.applyCondition = applyCondition;
    }

    public String getHandleData() {
        return handleData;
    }

    public void setHandleData(String handleData) {
        this.handleData = handleData;
    }

    public String getHandleProcess() {
        return handleProcess;
    }

    public void setHandleProcess(String handleProcess) {
        this.handleProcess = handleProcess;
    }

    public String getHandleAddress() {
        return handleAddress;
    }

    public void setHandleAddress(String handleAddress) {
        this.handleAddress = handleAddress;
    }

    public String getHandleDate() {
        return handleDate;
    }

    public void setHandleDate(String handleDate) {
        this.handleDate = handleDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTableIds() {
        return tableIds;
    }

    public void setTableIds(String tableIds) {
        this.tableIds = tableIds;
    }

    public String getTableNames() {
        return tableNames;
    }

    public void setTableNames(String tableNames) {
        this.tableNames = tableNames;
    }

    public String getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(String ruleIds) {
        this.ruleIds = ruleIds;
    }

    public String getRuleNames() {
        return ruleNames;
    }

    public void setRuleNames(String ruleNames) {
        this.ruleNames = ruleNames;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public List<CmsTableResourcesEO> getTableResourcesEOs() {
        return tableResourcesEOs;
    }

    public void setTableResourcesEOs(List<CmsTableResourcesEO> tableResourcesEOs) {
        this.tableResourcesEOs = tableResourcesEOs;
    }

    public List<CmsRelatedRuleEO> getRelatedRuleEOs() {
        return relatedRuleEOs;
    }

    public void setRelatedRuleEOs(List<CmsRelatedRuleEO> relatedRuleEOs) {
        this.relatedRuleEOs = relatedRuleEOs;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRelateTablesUrl() {
        return relateTablesUrl;
    }

    public void setRelateTablesUrl(String relateTablesUrl) {
        this.relateTablesUrl = relateTablesUrl;
    }

    public String getRelateRulesUrl() {
        return relateRulesUrl;
    }

    public void setRelateRulesUrl(String relateRulesUrl) {
        this.relateRulesUrl = relateRulesUrl;
    }
}