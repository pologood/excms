package cn.lonsun.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * Created by lonsun on 2018-3-9.
 */
@Entity
@Table(name = "DATAIMPORT_ORGAN_RELATION")
public class DataimportOrganRelationEO extends AMockEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "NEW_ORGAN_ID")
    private Long newOrganId;
    @Column(name="NEW_ORGAN_NAME")
    private String  newOrganName;
    @Column(name = "OLD_ORGAN_ID")
    private Long   oldOrganId;
    @Column(name ="OLD_ORGAN_NAME")
    private String  oldOrganName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNewOrganId() {
        return newOrganId;
    }

    public void setNewOrganId(Long newOrganId) {
        this.newOrganId = newOrganId;
    }

    public String getNewOrganName() {
        return newOrganName;
    }

    public void setNewOrganName(String newOrganName) {
        this.newOrganName = newOrganName;
    }

    public Long getOldOrganId() {
        return oldOrganId;
    }

    public void setOldOrganId(Long oldOrganId) {
        this.oldOrganId = oldOrganId;
    }

    public String getOldOrganName() {
        return oldOrganName;
    }

    public void setOldOrganName(String oldOrganName) {
        this.oldOrganName = oldOrganName;
    }
}
