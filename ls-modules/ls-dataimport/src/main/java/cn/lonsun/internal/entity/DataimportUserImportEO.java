package cn.lonsun.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**用户导入
 * Created by lonsun on 2018-2-22.
 */
@Entity
@Table(name = "DATAIMPORT_USER_IMPORT")
public class DataimportUserImportEO extends AMockEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     *  '新用户ID',
     */
    @Column(name = "NEW_USER_ID")
    private Long newUserId;
    /**
     * 新用户名称
     */
    @Column(name = "NEW_PERSON_NAME")
    private String newPersonName;
    /**
     * 老用户ID
     */
    @Column(name = "OLD_USER_ID")
    private  Long oldUserId;
    /**
     * 老用户名称
     */
    @Column(name = "OLD_PERSON_NAME")
    private String oldPersonName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNewUserId() {
        return newUserId;
    }

    public void setNewUserId(Long newUserId) {
        this.newUserId = newUserId;
    }

    public String getNewPersonName() {
        return newPersonName;
    }

    public void setNewPersonName(String newPersonName) {
        this.newPersonName = newPersonName;
    }

    public Long getOldUserId() {
        return oldUserId;
    }

    public void setOldUserId(Long oldUserId) {
        this.oldUserId = oldUserId;
    }

    public String getOldPersonName() {
        return oldPersonName;
    }

    public void setOldPersonName(String oldPersonName) {
        this.oldPersonName = oldPersonName;
    }
}
