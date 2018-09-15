package cn.lonsun.weibo.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_WB_CONF")
public class WeiboConfEO extends ABaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Type {
        //腾讯
        Tencent,
        //新浪
        Sina
    }

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name="URL")
    private String url;

    @Column(name="NAME")
    private String name;

    @Column(name="ACCOUNT")
    private String account;

    @Column(name="QR_CODE")
    private String qrCode;

    @Column(name="VALID_URL")
    private String validUrl;

    @Column(name="APP_KEY")
    private String appKey;

    @Column(name="APP_SECRET")
    private String appSecret;

    @Column(name="OPEN_ID")
    private String openID;

    @Column(name="TOKEN")
    private String token;

    @Column(name="TYPE")
    private String type;

    @Column(name="SITE_ID")
    private Long siteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getValidUrl() {
        return validUrl;
    }

    public void setValidUrl(String validUrl) {
        this.validUrl = validUrl;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getOpenID() {
        return openID;
    }

    public void setOpenID(String openID) {
        this.openID = openID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
