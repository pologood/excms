package cn.lonsun.govbbs.internal.vo;

import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by zhangchao on 2017/2/17.
 */
public class BbsPostPageVO implements java.io.Serializable{

    private static final long serialVersionUID = 1L;


    private Long postId;

    private Long plateId;

    private String title;

    private Integer viewCount = 0;

    //(回复次数)
    private Integer replyCount = 0;

    //(是否总置顶)
    private Integer isHeadTop = BbsPostEO.Status.No.getStatus();

    //(是否置顶)
    private Integer isTop = BbsPostEO.Status.No.getStatus();

    //(是否精华、推荐)
    private Integer isEssence  = BbsPostEO.Status.No.getStatus();

    //(是否锁定)
    private Integer isLock = BbsPostEO.Status.No.getStatus();

    //(是否受理)
    private Integer isAccept;

    private Long memberId;

    //(发帖人)
    private String memberName;

    private String memberImg;

    //红牌时间戳
    private Long yellowTimes;

    //红牌时间戳
    private Long redTimes;

    private Long lastMemberId;

    //(审核人)
    private String lastMemberName;

    //(审核时间)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date lastTime;

    private Integer hasFile = 0;

    //支持
    private Integer support = 0;

    private Integer redCard;

    private Integer yellowCard;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    private Integer isNew;

    private Integer hot = 0;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getPlateId() {
        return plateId;
    }

    public void setPlateId(Long plateId) {
        this.plateId = plateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public Integer getIsHeadTop() {
        return isHeadTop;
    }

    public void setIsHeadTop(Integer isHeadTop) {
        this.isHeadTop = isHeadTop;
    }

    public Integer getIsTop() {
        return isTop;
    }

    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    public Integer getIsEssence() {
        return isEssence;
    }

    public void setIsEssence(Integer isEssence) {
        this.isEssence = isEssence;
    }

    public Integer getIsLock() {
        return isLock;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

    public Integer getIsAccept() {
        return isAccept;
    }

    public void setIsAccept(Integer isAccept) {
        this.isAccept = isAccept;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Long getYellowTimes() {
        return yellowTimes;
    }

    public void setYellowTimes(Long yellowTimes) {
        this.yellowTimes = yellowTimes;
    }

    public Long getRedTimes() {
        return redTimes;
    }

    public void setRedTimes(Long redTimes) {
        this.redTimes = redTimes;
    }

    public Long getLastMemberId() {
        return lastMemberId;
    }

    public void setLastMemberId(Long lastMemberId) {
        this.lastMemberId = lastMemberId;
    }

    public String getLastMemberName() {
        return lastMemberName;
    }

    public void setLastMemberName(String lastMemberName) {
        this.lastMemberName = lastMemberName;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getHasFile() {
        return hasFile;
    }

    public void setHasFile(Integer hasFile) {
        this.hasFile = hasFile;
    }

    public Integer getSupport() {
        return support;
    }

    public void setSupport(Integer support) {
        this.support = support;
    }

    public Integer getYellowCard() {
        return yellowCard;
    }

    public void setYellowCard(Integer yellowCard) {
        this.yellowCard = yellowCard;
    }

    public Integer getRedCard() {
        return redCard;
    }

    public void setRedCard(Integer redCard) {
        this.redCard = redCard;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

    public Integer getIsNew() {
        return isNew;
    }

    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }

    public String getMemberImg() {
        return memberImg;
    }

    public void setMemberImg(String memberImg) {
        this.memberImg = memberImg;
    }
}
