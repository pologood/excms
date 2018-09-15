package cn.lonsun.site.thirdLoginManage.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * @author liuk
 * @version 2018-4-23 15:43:02
 */
@Entity
@Table(name = "CMS_THIRD_LOGIN_MGR")
public class ThirdLoginMgrEO extends ABaseEntity {

    public enum Type{
        QQ,//qq登录
        WeChat,//微信登录
        WeiBo//腾讯微博登录
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "SITE_ID")
    private Long siteId;

    @Column(name = "TYPE_")
    private String type;//第三方登录分类  QQ，微信，微博

    @Column(name = "STATUS")
    private Integer status = 0; //0:不启用 1：启用 默认不启用

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "APP_SECRET")
    private String appSecret;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}