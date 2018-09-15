package cn.lonsun.wechatmgr.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * Created by lonsun on 2016-10-10.
 */

@Entity
@Table(name="WE_CHAT_RECORD")
public class WeChatTurnEO extends AMockEntity {

    public enum TYPE{
        sub,//客户提交
        reply,//答复
        turn;//转办
        private String type;
        public String getType() {
            return type;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "SITE_ID")
    private Long siteId;
    @Column(name = "MSG_ID")
    private Long msgId;
    @Column(name = "OPERATE_UNIT_ID")
    private Long operateUnitId;
    @Column(name = "OPERATE_UNIT_NAME")
    private String operateUnitName;
    @Column(name = "OPERATE_USER_NAME")
    private String operateUserName;
    @Column(name = "TURN_UNIT_ID")
    private Long turnUnitId;
    @Column(name = "TURN_UNIT_NAME")
    private String turnUnitName;
    @Column(name = "IS_SITE_ADMIN")
    private Integer isSiteAdmin;
    @Column(name = "IS_SUPER_ADMIN")
    private Integer isSuperAdmin;
    @Column(name = "TYPE")
    private String type;
    @Transient
    private String details;
    @Transient
    private String nickname;
    @Column(name = "ORIGIN_USER_NAME")
    private String originUserName;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public Integer getIsSiteAdmin() {
        return isSiteAdmin;
    }

    public void setIsSiteAdmin(Integer isSiteAdmin) {
        this.isSiteAdmin = isSiteAdmin;
    }

    public Integer getIsSuperAdmin() {
        return isSuperAdmin;
    }

    public void setIsSuperAdmin(Integer isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getOperateUnitId() {
        return operateUnitId;
    }

    public void setOperateUnitId(Long operateUnitId) {
        this.operateUnitId = operateUnitId;
    }

    public String getOperateUnitName() {
        return operateUnitName;
    }

    public void setOperateUnitName(String operateUnitName) {
        this.operateUnitName = operateUnitName;
    }

    public Long getTurnUnitId() {
        return turnUnitId;
    }

    public void setTurnUnitId(Long turnUnitId) {
        this.turnUnitId = turnUnitId;
    }

    public String getTurnUnitName() {
        return turnUnitName;
    }

    public void setTurnUnitName(String turnUnitName) {
        this.turnUnitName = turnUnitName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getOriginUserName() {
        return originUserName;
    }

    public void setOriginUserName(String originUserName) {
        this.originUserName = originUserName;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }
}
