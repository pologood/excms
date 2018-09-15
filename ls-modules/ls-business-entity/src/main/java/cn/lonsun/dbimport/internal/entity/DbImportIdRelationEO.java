package cn.lonsun.dbimport.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "db_import_id_relation")
public class DbImportIdRelationEO extends ABaseEntity {

    public DbImportIdRelationEO(Long newId, Long oldId, String type) {
        this.newId = newId;
        this.oldId = oldId;
        this.type = type;
    }

    public DbImportIdRelationEO() {
    }

    //主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="new_id")
    private Long newId;

    @Column(name="old_id")
    private Long oldId;

    @Column(name="type")
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNewId() {
        return newId;
    }

    public void setNewId(Long newId) {
        this.newId = newId;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
