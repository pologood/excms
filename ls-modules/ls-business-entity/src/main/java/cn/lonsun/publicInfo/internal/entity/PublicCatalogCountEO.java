package cn.lonsun.publicInfo.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * 信息公开目录发文数统计
 * Created by fth on 2017/5/31.
 */
@Entity
@Table(name = "CMS_PUBLIC_CATALOG_COUNT")
public class PublicCatalogCountEO extends ABaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;// 主键

    @Column(name = "ORGAN_ID")
    private Long organId;
    @Column(name = "CAT_ID")
    private Long catId;
    @Column(name = "PUBLISH_COUNT")
    private Long publishCount = 0L;//已发布文章数
    @Column(name = "UN_PUBLISH_COUNT")
    private Long unPublishCount = 0L;//未发布文章数

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public Long getPublishCount() {
        return publishCount;
    }

    public void setPublishCount(Long publishCount) {
        this.publishCount = publishCount;
    }

    public Long getUnPublishCount() {
        return unPublishCount;
    }

    public void setUnPublishCount(Long unPublishCount) {
        this.unPublishCount = unPublishCount;
    }
}
