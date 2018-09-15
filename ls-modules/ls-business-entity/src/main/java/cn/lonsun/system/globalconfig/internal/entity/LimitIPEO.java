package cn.lonsun.system.globalconfig.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Doocal
 * @ClassName: LimitIPEO
 * @Description: 限制IP类
 */
@Entity
@Table(name = "cms_limit_ip")
public class LimitIPEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 4857349989564882716L;

    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    //IP地址
    @Column(name = "IP")
    private String ip;

    @Column(name = "RULES")
    private Integer rules;

    @Column(name = "DESCRIPTION")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getRules() {
        return rules;
    }

    public void setRules(Integer rules) {
        this.rules = rules;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
