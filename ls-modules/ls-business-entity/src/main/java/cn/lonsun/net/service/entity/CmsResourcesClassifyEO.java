package cn.lonsun.net.service.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name = "CMS_NET_RESOURCES_CLASSIFY")
public class CmsResourcesClassifyEO extends ABaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Type{
        GUIDE,//办事指南
        TABLE, //表格资源库类型
        RULE   //相关法规
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "P_ID")
    private Long pId;

    @Column(name = "C_ID")
    private Long cId;

    @Column(name = "TYPE")
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public Long getcId() {
        return cId;
    }

    public void setcId(Long cId) {
        this.cId = cId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}