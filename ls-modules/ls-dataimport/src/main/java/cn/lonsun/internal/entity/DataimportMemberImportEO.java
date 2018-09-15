package cn.lonsun.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * Created by lonsun on 2018-2-22.
 */
@Entity
@Table(name = "DATAIMPORT_MEMBER_IMPORT")
public class DataimportMemberImportEO extends AMockEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * EX8会员ID
     */
    @Column(name = "NEW_MEMBER_ID")
    private Long newMemberId;
    /**
     * EX8会员名称
     */
    @Column(name = "NEW_MEMBER_NAME")
    private String newMemberName;
    /**
     * 老网站会员ID
     */
    @Column(name = "OLD_MEMBER_ID")
    private Long oldMemberId;
    /**
     * 老网站会员名称
     */
    @Column(name = "OLD_MEMBER_NAME")
    private String oldMemberName;
    @Column(name = "MUID")
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNewMemberId() {
        return newMemberId;
    }

    public void setNewMemberId(Long newMemberId) {
        this.newMemberId = newMemberId;
    }

    public String getNewMemberName() {
        return newMemberName;
    }

    public void setNewMemberName(String newMemberName) {
        this.newMemberName = newMemberName;
    }

    public Long getOldMemberId() {
        return oldMemberId;
    }

    public void setOldMemberId(Long oldMemberId) {
        this.oldMemberId = oldMemberId;
    }

    public String getOldMemberName() {
        return oldMemberName;
    }

    public void setOldMemberName(String oldMemberName) {
        this.oldMemberName = oldMemberName;
    }
}
