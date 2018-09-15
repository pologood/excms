package cn.lonsun.govbbs.internal.vo;

import java.util.Date;

/**
 * Created by zhangchao on 2017/1/5.
 */
public class MemberInfoVO implements  java.io.Serializable{

    private static final long serialVersionUID = 1L;

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
    //会员类型
    private Integer memberType;

    //会员分组id
    private Long memberRoleId;

    //会员角色名称
    private String mRName;
    //会员角色星星
    private Integer mRStar;


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

    public Integer getMemberType() {
        return memberType;
    }

    public void setMemberType(Integer memberType) {
        this.memberType = memberType;
    }

    public Long getMemberRoleId() {
        return memberRoleId;
    }

    public void setMemberRoleId(Long memberRoleId) {
        this.memberRoleId = memberRoleId;
    }

    public Long getmReplys() {
        return mReplys;
    }

    public void setmReplys(Long mReplys) {
        this.mReplys = mReplys;
    }
}
