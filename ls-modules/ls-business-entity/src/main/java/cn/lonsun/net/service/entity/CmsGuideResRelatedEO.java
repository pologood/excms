package cn.lonsun.net.service.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name = "CMS_NET_GUIDE_RES_RELATED")
public class CmsGuideResRelatedEO extends ABaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum TYPE {
        TABLE, //表格资源关联类型
        RULE  //法规资源关联类型
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "G_ID")
    private Long guideId;

    @Column(name = "R_ID")
    private Long resId;

    @Column(name = "TYPE")
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGuideId() {
        return guideId;
    }

    public void setGuideId(Long guideId) {
        this.guideId = guideId;
    }

    public Long getResId() {
        return resId;
    }

    public void setResId(Long resId) {
        this.resId = resId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}