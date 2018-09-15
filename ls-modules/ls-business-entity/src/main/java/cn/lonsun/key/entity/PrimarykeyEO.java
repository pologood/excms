package cn.lonsun.key.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2016-2-17 14:07
 */
@Entity
@Table(name = "CMS_PRIMARYKEY")
public class PrimarykeyEO extends ABaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "NAME")
    private String name;

    @Column(name = "CODE")
    private Long code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }
}