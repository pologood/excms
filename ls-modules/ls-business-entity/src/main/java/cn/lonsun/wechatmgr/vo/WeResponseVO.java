package cn.lonsun.wechatmgr.vo;

import cn.lonsun.wechatmgr.internal.entity.WeChatTurnEO;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lonsun on 2016-10-9.
 */
public class WeResponseVO {
    private Long id;
    private Long createTime;
    private String headimgurl;
    private String nickname;
    private Integer sex;
    private String content;
    private String picUrl;
    private String mediaId;
    private Long repMsgId;
    private String repMsgContent;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date repMsgDate;
    private String originUserName;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    private List<WeChatTurnEO> weChatTurnEOs=new ArrayList<WeChatTurnEO>();

    private  String  judge;

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public Long getRepMsgId() {
        return repMsgId;
    }

    public void setRepMsgId(Long repMsgId) {
        this.repMsgId = repMsgId;
    }

    public String getRepMsgContent() {
        return repMsgContent;
    }

    public void setRepMsgContent(String repMsgContent) {
        this.repMsgContent = repMsgContent;
    }

    public Date getRepMsgDate() {
        return repMsgDate;
    }

    public void setRepMsgDate(Date repMsgDate) {
        this.repMsgDate = repMsgDate;
    }
    public String getOriginUserName() {
        return originUserName;
    }

    public void setOriginUserName(String originUserName) {
        this.originUserName = originUserName;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public List<WeChatTurnEO> getWeChatTurnEOs() {
        return weChatTurnEOs;
    }

    public void setWeChatTurnEOs(List<WeChatTurnEO> weChatTurnEOs) {
        this.weChatTurnEOs = weChatTurnEOs;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    public String getJudge() {
        return judge;
    }

    public void setJudge(String judge) {
        this.judge = judge;
    }
}
