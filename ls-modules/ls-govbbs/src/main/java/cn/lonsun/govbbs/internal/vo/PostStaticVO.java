package cn.lonsun.govbbs.internal.vo;

import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * Created by zhangchao on 2017/1/5.
 */
public class PostStaticVO implements  java.io.Serializable{

    private static final long serialVersionUID = 1L;

    private Long postId;

    //(站点id)
    private Long siteId;

    //(版块id)
    private Long plateId;

    //(版块名称)
    private String plateName;

    //(标题)
    private String title;

    //(内容)
    private String content;

    //(浏览次数)
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

    //单位时间戳
    private Long handleTimes;

    //(受理单位id)
    private Long acceptUnitId;

    //(受理单位名称)
    private String acceptUnitName;

    //(单位发送时间)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date acceptTime;

    //(会员id)
    private Long memberId;

    //(联系人)
    private String linkman;

    //(发帖人)
    private String memberName;

    //(手机号)
    private String memberPhone;

    //(邮箱)
    private String memberEmail;

    //(地址)
    private String memberAddress;

    // 0 系统管理员   1 会员  2游客
    private Integer addType;

    private Integer memberType;

    //(发帖ip)
    private String ip;

    //(审核回复id)
    private Long replyId;

    //火
    private Integer hot = 0;

    //1 附件  2图片 3 都有
    private Integer hasFile = 0;

    //支持
    private Integer support = 0;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    private Integer isTimeOut;

    private Integer redCard;

    private Integer yellowCard;

    private Integer isNew;

    //时候支持了或者反对了  1 已经投票
    private Integer isVote = 0;

    //支持数量
    private Integer supportCount;

    //反对数
    private Integer againstCount;

    //会员头像
    private String  mImg;
    //会员积分
    private Long mPoints = 0L;
    //会员帖子数
    private Long mPosts = 0L;
    //会员回复数
    private Long mReplys = 0L;
    //会员创建时间
    private Date mCTime;
    //会员角色名称
    private String mRName;
    //会员角色星星
    private Integer mRStar;

    //楼层
    private String floor;

    //附件
    private List<BbsFileStaticVO> files;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Long getHandleTimes() {
        return handleTimes;
    }

    public void setHandleTimes(Long handleTimes) {
        this.handleTimes = handleTimes;
    }

    public Long getAcceptUnitId() {
        return acceptUnitId;
    }

    public void setAcceptUnitId(Long acceptUnitId) {
        this.acceptUnitId = acceptUnitId;
    }

    public String getAcceptUnitName() {
        return acceptUnitName;
    }

    public void setAcceptUnitName(String acceptUnitName) {
        this.acceptUnitName = acceptUnitName;
    }

    public Date getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime) {
        this.acceptTime = acceptTime;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberPhone() {
        return memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getMemberAddress() {
        return memberAddress;
    }

    public void setMemberAddress(String memberAddress) {
        this.memberAddress = memberAddress;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
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

    public Integer getIsTimeOut() {
        return isTimeOut;
    }

    public void setIsTimeOut(Integer isTimeOut) {
        this.isTimeOut = isTimeOut;
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

    public Integer getIsNew() {
        return isNew;
    }

    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }

    public String getmImg() {
        return mImg;
    }

    public void setmImg(String mImg) {
        this.mImg = mImg;
    }

    public Long getmPoints() {
        return mPoints;
    }

    public void setmPoints(Long mPoints) {
        this.mPoints = mPoints;
    }

    public Long getmPosts() {
        return mPosts;
    }

    public void setmPosts(Long mPosts) {
        this.mPosts = mPosts;
    }

    public Date getmCTime() {
        return mCTime;
    }

    public void setmCTime(Date mCTime) {
        this.mCTime = mCTime;
    }

    public String getmRName() {
        return mRName;
    }

    public void setmRName(String mRName) {
        this.mRName = mRName;
    }

    public Integer getmRStar() {
        return mRStar;
    }

    public void setmRStar(Integer mRStar) {
        this.mRStar = mRStar;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getAgainstCount() {
        return againstCount;
    }

    public void setAgainstCount(Integer againstCount) {
        this.againstCount = againstCount;
    }

    public Integer getSupportCount() {
        return supportCount;
    }

    public void setSupportCount(Integer supportCount) {
        this.supportCount = supportCount;
    }

    public Integer getIsVote() {
        return isVote;
    }

    public void setIsVote(Integer isVote) {
        this.isVote = isVote;
    }


    public Integer getAddType() {
        return addType;
    }

    public void setAddType(Integer addType) {
        this.addType = addType;
    }

    public Integer getMemberType() {
        return memberType;
    }

    public void setMemberType(Integer memberType) {
        this.memberType = memberType;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public List<BbsFileStaticVO> getFiles() {
        return files;
    }

    public void setFiles(List<BbsFileStaticVO> files) {
        this.files = files;
    }

    public Long getmReplys() {
        return mReplys;
    }

    public void setmReplys(Long mReplys) {
        this.mReplys = mReplys;
    }
}
