package cn.lonsun.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 信息公开目录关系对应表
 */
@Entity
@Table(name = "di_public_catalog_relation")
public class PublicCatalogRelationEO extends AMockEntity {

    public PublicCatalogRelationEO() {
    }

    public PublicCatalogRelationEO(long newId, String oldId) {
        this.newId = newId;
        this.oldId = oldId;
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "new_id")
    private long newId;

    @Column(name = "old_id")
    private String oldId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNewId() {
        return newId;
    }

    public void setNewId(long newId) {
        this.newId = newId;
    }

    public String getOldId() {
        return oldId;
    }

    public void setOldId(String oldId) {
        this.oldId = oldId;
    }

}
