package cn.lonsun.msg.submit.hn.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name = "CMS_MSG_TO_USER_HN")
public class CmsMsgToUserHnEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //报送消息主键ID
    @Column(name = "MSG_ID")
    private Long msgId;

    //消息接收人单位ID
    @Column(name = "UNIT_ID")
    private Long unitId;

    //消息接收人部门ID
    @Column(name = "ORGAN_ID")
    private Long organId;

    //消息接收人用户ID
    @Column(name = "USER_ID")
    private Long userId;

    //消息接收人用户名称
    @Column(name = "NAME")
    private String name;

    //是否默认转发栏目 0:默认 1:转发
    @Column(name = "IS_DEFAULT")
    private Integer isDefault = 0;

    //是否已经阅读报送消息 0:未阅 1:已阅
    @Column(name = "MSG_READ_STATUS")
    private Integer msgReadStatus = 0;

    //站点ID
    @Column(name = "SITE_ID")
    private Long siteId;

    @Column(name = "CREATE_UNIT_ID")
    private Long createUnitId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getMsgReadStatus() {
        return msgReadStatus;
    }

    public void setMsgReadStatus(Integer msgReadStatus) {
        this.msgReadStatus = msgReadStatus;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getCreateUnitId() {
        return createUnitId;
    }

    public void setCreateUnitId(Long createUnitId) {
        this.createUnitId = createUnitId;
    }

}