package cn.lonsun.msg.submit.hn.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name = "CMS_MSG_TO_COLUMN_HN")
public class CmsMsgToColumnHnEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //报送消息主键ID
    @Column(name = "MSG_ID")
    private Long msgId;

    //栏目ID
    @Column(name = "COLUMN_ID")
    private Long columnId;

    //栏目ID对应的主键ID
    @Column(name = "COLUMN_SITE_ID")
    private Long columnSiteId;

    //栏目名称
    @Column(name = "COLUMN_NAME")
    private String columnName;

    //信息公开单位ID
    @Column(name = "ORGAN_ID")
    private Long organId;

    //信息公开编码
    @Column(name = "CODE")
    private String code;

    //消息采用后的主键ID
    @Column(name = "CONTENT_ID")
    private Long contentId;

    //栏目对应的类型
    @Column(name = "COLUMN_TYPE_CODE")
    private String columnTypeCode;

    //是否默认转发栏目 0:默认 1:转发
    @Column(name = "IS_DEFAULT")
    private Integer isDefault = 0;

    //站点ID
    @Column(name = "SITE_ID")
    private Long siteId;

    //站点名称
    @Column(name = "SITE_NAME")
    private String siteName;

    @Column(name = "CREATE_UNIT_ID")
    private Long createUnitId;

    @Column(name = "STATUS")
    private Integer status = 0; //0：未发布 1：已发布

    @Column(name = "PUSH_WEIBO_STATUS")
    private Integer pushWeiboStatus = 0; //0：未推送 1：已推送

    @Column(name = "PUSH_WEIXIN_STATUS")
    private Integer pushWeixinStatus = 0; //0：未推送 1：已推送

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getColumnSiteId() {
        return columnSiteId;
    }

    public void setColumnSiteId(Long columnSiteId) {
        this.columnSiteId = columnSiteId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getColumnTypeCode() {
        return columnTypeCode;
    }

    public void setColumnTypeCode(String columnTypeCode) {
        this.columnTypeCode = columnTypeCode;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getCreateUnitId() {
        return createUnitId;
    }

    public void setCreateUnitId(Long createUnitId) {
        this.createUnitId = createUnitId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPushWeiboStatus() {
        return pushWeiboStatus;
    }

    public void setPushWeiboStatus(Integer pushWeiboStatus) {
        this.pushWeiboStatus = pushWeiboStatus;
    }

    public Integer getPushWeixinStatus() {
        return pushWeixinStatus;
    }

    public void setPushWeixinStatus(Integer pushWeixinStatus) {
        this.pushWeixinStatus = pushWeixinStatus;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}