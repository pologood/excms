package cn.lonsun.system.role.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-9-24 14:52
 */
@Entity
@Table(name="RBAC_ROLE_COLUMN_FUNCTION")
public class RbacRoleColumnFuncEO extends ABaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name="ROLE_ID")
    private Long roleId;

    @Column(name="COLUMN_ID")
    private Long columnId;

    @Column(name="FUNCTION")
    private String function;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
