package cn.lonsun.govbbs.internal.vo;

import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by zhangchao on 2017/2/27.
 */
public class PostVO implements java.io.Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private Long postId;

    private Long plateId;

    private String plateName;

    private String title;

    private Integer isHeadTop = BbsPostEO.Status.No.getStatus();

    //(是否置顶)
    private Integer isTop = BbsPostEO.Status.No.getStatus();

    //(是否精华、推荐)
    private Integer isEssence  = BbsPostEO.Status.No.getStatus();

    //(是否锁定)
    private Integer isLock = BbsPostEO.Status.No.getStatus();

    //(是否受理)
    private Integer isAccept;

    private Long acceptUnitId;

    private String acceptUnitName;

    //(审核人)
    private String auditUserName;

    //(审核时间)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    private String memberName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    private Integer isPublish = BbsPostEO.IsPublish.TO_AUDIT.getIsPublish();

    //红牌时间戳
    private Long yellowTimes;

    //红牌时间戳
    private Long redTimes;


    private Integer redCard;

    private Integer yellowCard;

    private String time;


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

    public String getPlateName() {
        return plateName;
    }

    public void setPlateName(String plateName) {
        this.plateName = plateName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Integer getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getAcceptUnitName() {
        return acceptUnitName;
    }

    public void setAcceptUnitName(String acceptUnitName) {
        this.acceptUnitName = acceptUnitName;
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

    public Long getAcceptUnitId() {
        return acceptUnitId;
    }

    public void setAcceptUnitId(Long acceptUnitId) {
        this.acceptUnitId = acceptUnitId;
    }

    public Integer getRedCard() {
        return redCard;
    }

    public void setRedCard(Integer redCard) {
        this.redCard = redCard;
    }

    public Integer getYellowCard() {
        return yellowCard;
    }

    public void setYellowCard(Integer yellowCard) {
        this.yellowCard = yellowCard;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
