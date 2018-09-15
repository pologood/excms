package cn.lonsun.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 导入失败的记录
 * @author zhongjun
 */
@Entity
@Table(name = "DI_ERROR_PUBLIC_CONTENT")
public class ErrorPublicContentEO extends AMockEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    /** 目录对应关系 */
    @Column(name = "CAT_RELATION_ID")
    private Long catRelationId;
    /** 内容id */
    @Column(name = "OLD_CONTENT_ID")
    private String oldContentId;
    /** 站点id*/
    @Column(name = "SITE_ID")
    private Long siteId;
    /** 内容标题*/
    @Column(name = "OLD_TITLE")
    private String oldTitle;
    /** 公开类型*/
    @Column(name = "PUBLIC_CONTENT_TYPE")
    private String publicContentType;
    /** 单位关系配置id */
    @Column(name = "UNIT_RELATION_ID")
    private Long unitRelationId;
    /** 错误说明 */
    @Column(name = "ERROR_REMARK")
    private String errorRemark;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Long getCatRelationId() {
        return catRelationId;
    }

    public void setCatRelationId(Long catRelationId) {
        this.catRelationId = catRelationId;
    }

    public String getPublicContentType() {
        return publicContentType;
    }

    public void setPublicContentType(String publicContentType) {
        this.publicContentType = publicContentType;
    }

    public Long getUnitRelationId() {
        return unitRelationId;
    }

    public void setUnitRelationId(Long unitRelationId) {
        this.unitRelationId = unitRelationId;
    }
}
