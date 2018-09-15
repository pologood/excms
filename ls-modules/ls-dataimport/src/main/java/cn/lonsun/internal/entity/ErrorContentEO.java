package cn.lonsun.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 导入失败的记录
 * @author zhongjun
 */
@Entity
@Table(name = "DATAIMPORT_ERROR_CONTENT")
public class ErrorContentEO extends AMockEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "COLUMN_RELATION_ID")
    private Long columnRelationId;
    @Column(name = "OLD_CONTENT_ID")
    private String oldContentId;
    @Column(name = "OLD_TITLE")
    private String oldTitle;
    @Column(name = "SITE_ID")
    private Long siteId;
    /** 错误说明 */
    @Column(name = "ERROR_REMARK")
    private String errorRemark;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getColumnRelationId() {
        return columnRelationId;
    }

    public void setColumnRelationId(Long columnRelationId) {
        this.columnRelationId = columnRelationId;
    }

    public String getOldContentId() {
        return oldContentId;
    }

    public void setOldContentId(String oldContentId) {
        this.oldContentId = oldContentId;
    }

    public String getOldTitle() {
        return oldTitle;
    }

    public void setOldTitle(String oldTitle) {
        this.oldTitle = oldTitle;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getErrorRemark() {
        return errorRemark;
    }

    public void setErrorRemark(String errorRemark) {
        this.errorRemark = errorRemark;
    }

}
