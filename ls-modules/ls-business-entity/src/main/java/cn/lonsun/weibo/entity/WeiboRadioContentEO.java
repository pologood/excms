package cn.lonsun.weibo.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_WB_RADIO_CONTENT")
public class WeiboRadioContentEO extends ABaseEntity implements Serializable {

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

    @Column(name="WEIBO_ID")
    private String weiboId;

    @Column(name="CONTENT")
    private String content;

    @Column(name="TYPE")
    private String type;

    @Column(name="SITE_ID")
    private Long siteId;

    @Column(name="PIC_URL")
    private String picUrl;

    @Column(name="STATUS")
    private Integer status = 0; // 0:未审核 1:已审核并发布

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(String weiboId) {
        this.weiboId = weiboId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
