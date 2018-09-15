package cn.lonsun.wechatmgr.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhangchao on 2016/10/9.
 */
@Entity
@Table(name = "CMS_WECHAT_LOG")
public class WeChatLogEO extends AMockEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    // 关注用户的openID
    @Column(name = "OPENID")
    private String openid;

    @Column(name = "SITE_ID")
    private Long siteId;

    // 昵称
    @Column(name = "NICK_NAME")
    private String nickname;

    // 头像
    @Column(name = "HEAD_IMGURL")
    private String headimgurl;

    //接受类型
    @Column(name = "MSG_TYPE")
    private String msgType;

    //内容
    @Column(name = "CONTENT")
    private String content;

    // 国
    @Column(name = "COUNTRY")
    private String country;
    // 省
    @Column(name = "PROVINCE")
    private String province;
    // 市
    @Column(name = "CITY")
    private String city;

    @Column(name = "CREATE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public WeChatLogEO() {
        super();
    }

    public WeChatLogEO(String fromUserName, Long siteId, String msgType, Date date, String content) {
        super();
        this.openid = fromUserName;
        this.siteId = siteId;
        this.msgType = msgType;
        this.createTime = date;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
