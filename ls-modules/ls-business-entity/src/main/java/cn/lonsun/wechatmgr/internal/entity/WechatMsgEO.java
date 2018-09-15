package cn.lonsun.wechatmgr.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author gu.fei
 * @version 2016-9-29 10:23
 */
@Entity
@Table(name="CMS_WECHAT_MSG")
public class WechatMsgEO extends AMockEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "TO_USER_NAME")
    private String toUserName;

    @Column(name = "ORIGIN_USER_NAME")
    private String originUserName;

    @Column(name = "CREATE_TIME")
    private Long createTime;

    @Column(name = "MSG_TYPE")
    private String msgType;

    @Column(name = "MSG_ID")
    private Long msgId;

    // 文本消息  
    @Column(name = "CONTENT")
    private String content;

    // 图片消息  
    @Column(name = "PIC_URL")
    private String picUrl;

    // 位置消息  
    @Column(name = "LOCATION_X")
    private String locationX;

    @Column(name = "LOCATION_Y")
    private String locationY;

    @Column(name = "SCALE")
    private Long scale;

    @Column(name = "LABEL")
    private String label;

    // 链接消息  
    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "URL")
    private String url;

    // 语音信息  
    @Column(name = "MEDIA_ID")
    private String mediaId;

    @Column(name = "FORMAT")
    private String format;

    @Column(name = "RECOGNITION")
    private String recognition;

    // 事件  
    @Column(name = "EVENT")
    private String event;

    @Column(name = "EVENT_KEY")
    private String eventKey;

    @Column(name = "TICKET")
    private String ticket;

    //是否已回复
    @Column(name = "IS_REP")
    private Integer isRep = 0; //0:未回复 1：已回复

    //回复消息ID
    @Column(name = "REP_MSG_ID")
    private Long repMsgId;

    //回复内容
    @Column(name = "REP_MSG_CONTENT")
    private String repMsgContent;

    @Column(name="REP_MSG_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date repMsgDate;

    @Column(name = "CHANGE_UNIT_ID")
    private Long changeUnitId;
    //转办单位
    @Column(name = "TURN_UNIT_ID")
    private Long turnUnitId;

    //回复消息ID
    @Column(name = "SITE_ID")
    private Long siteId;
    @Column(name = "IS_JUDGE")
    private  Integer isJudge = 0;
    @Column(name = "JUDGE")
    private  String  judge;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getOriginUserName() {
        return originUserName;
    }

    public void setOriginUserName(String originUserName) {
        this.originUserName = originUserName;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
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

    public String getLocationX() {
        return locationX;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }

    public Long getScale() {
        return scale;
    }

    public void setScale(Long scale) {
        this.scale = scale;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRecognition() {
        return recognition;
    }

    public void setRecognition(String recognition) {
        this.recognition = recognition;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Integer getIsRep() {
        return isRep;
    }

    public void setIsRep(Integer isRep) {
        this.isRep = isRep;
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

    public Long getChangeUnitId() {
        return changeUnitId;
    }

    public void setChangeUnitId(Long changeUnitId) {
        this.changeUnitId = changeUnitId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getTurnUnitId() {
        return turnUnitId;
    }

    public void setTurnUnitId(Long turnUnitId) {
        this.turnUnitId = turnUnitId;
    }

    public Integer getIsJudge() {
        return isJudge;
    }

    public void setIsJudge(Integer isJudge) {
        this.isJudge = isJudge;
    }

    public String getJudge() {
        return judge;
    }

    public void setJudge(String judge) {
        this.judge = judge;
    }
}