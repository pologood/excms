package cn.lonsun.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.*;

/**
 * 信息公开单位对应关系
 * @author zhongjun
 */
@Entity
@Table(name="di_public_unit_relation")
public class PublicUnitRelationEO extends AMockEntity {

    public PublicUnitRelationEO() {
    }

    /**
     * @param newUnitId 新单位id
     * @param newUnitName 新单位名称
     * @param oldUnitId 老单位id
     * @param oldUnitName 老单位名称
     */
    public PublicUnitRelationEO(long newUnitId, String newUnitName, String oldUnitId, String oldUnitName) {
        this.newUnitId = newUnitId;
        this.newUnitName = newUnitName;
        this.oldUnitId = oldUnitId;
        this.oldUnitName = oldUnitName;
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "new_unit_id")
    private long newUnitId;

    @Column(name = "new_unit_name")
    private String newUnitName;

    @Column(name = "old_unit_id")
    private String oldUnitId;

    @Column(name = "old_unit_name")
    private String oldUnitName;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNewUnitId() {
        return newUnitId;
    }

    public void setNewUnitId(long newUnitId) {
        this.newUnitId = newUnitId;
    }

    public String getNewUnitName() {
        return newUnitName;
    }

    public void setNewUnitName(String newUnitName) {
        this.newUnitName = newUnitName;
    }

    public String getOldUnitId() {
        return oldUnitId;
    }

    public void setOldUnitId(String oldUnitId) {
        this.oldUnitId = oldUnitId;
    }

    public String getOldUnitName() {
        return oldUnitName;
    }

    public void setOldUnitName(String oldUnitName) {
        this.oldUnitName = oldUnitName;
    }
}
