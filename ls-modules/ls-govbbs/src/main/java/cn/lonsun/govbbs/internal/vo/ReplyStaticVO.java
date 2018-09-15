package cn.lonsun.govbbs.internal.vo;

import java.util.Date;
import java.util.List;

/**
 * Created by zhangchao on 2017/1/4.
 */
public class ReplyStaticVO implements  java.io.Serializable{

    private static final long serialVersionUID = 1L;

    private Long replyId;

    private String content;

    private Date createDate;

    private String ip;

    private Integer addType;

    //是否是办理回复
    private Integer isHandle;

    private Long handleUnitId;

    private String handleUnitName;

    //会员id
    private Long mId;
    //会员名称
    private String  mName;
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

    private Integer memberType;

    //楼层
    private String floor;

    private List<BbsFileStaticVO> files;

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getIsHandle() {
        return isHandle;
    }

    public void setIsHandle(Integer isHandle) {
        this.isHandle = isHandle;
    }

    public Long getHandleUnitId() {
        return handleUnitId;
    }

    public void setHandleUnitId(Long handleUnitId) {
        this.handleUnitId = handleUnitId;
    }

    public String getHandleUnitName() {
        return handleUnitName;
    }

    public void setHandleUnitName(String handleUnitName) {
        this.handleUnitName = handleUnitName;
    }

    public Long getmId() {
        return mId;
    }

    public void setmId(Long mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
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
