package cn.lonsun.weibo.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author gu.fei
 * @version 2015-12-24 11:21
 */
@Entity
@Table(name="CMS_SINA_COMMENT_BYME")
public class SinaWeiboCommentByMeEO extends AMockEntity {

    //微博具体内容
    @Id
    @Column(name="COMMENT_ID")
    private String commentId;                                   //status id

    @Column(name="CREATED_AT_COMMENT")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createdAtComment;

    @Column(name="COMMENT_TEXT")
    private String commentText;
    //微博来源

    @Column(name="USER_ID")
    private String userId;

    @Column(name="SCREEN_NAME")
    private String screenName;

    @Column(name="NAME")
    private String name;

    @Column(name="URL")
    private String url;

    @Column(name="PROFILE_IMAGE_URL")
    private String profileImageUrl;

    @Column(name="USER_DOMAIN")
    private String userDomain;

    @Column(name="GENDER")
    private String gender;

    @Column(name="REP_COMMENT_ID")
    private String repCommentId;

    @Column(name="REP_CREATED_AT_COMMENT")
    private Date repCreatedAtComment;

    @Column(name="REP_COMMENT_TEXT")
    private String repCommentText;

    @Column(name="REP_USER_ID")
    private String repUserId;

    @Column(name="REP_SCREEN_NAME")
    private String repScreenName;

    @Column(name="REP_NAME")
    private String repName;

    @Column(name="REP_URL")
    private String repUrl;

    //微博发布用户信息
    @Column(name="REP_PROFILE_IMAGE_URL")
    private String repProfileImageUrl;

    @Column(name="REP_USER_DOMAIN")
    private String repUserDomain;

    @Column(name="REP_GENDER")
    private String repGender;

    @Column(name="WEIBO_ID")
    private String weiboId;

    @Column(name="TEXT")
    private String text;

    @Column(name="THUMBNAIL_PIC")
    private String thumbnailPic;

    @Column(name="BMIDDLE_PIC")
    private String bmiddlePic;

    @Column(name="ORGINAL_PIC")
    private String orginalPic;

    @Column(name="IS_RETWEETED")
    private String isRetweeted;

    @Column(name="SITE_ID")
    private Long siteId;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Date getCreatedAtComment() {
        return createdAtComment;
    }

    public void setCreatedAtComment(Date createdAtComment) {
        this.createdAtComment = createdAtComment;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserDomain() {
        return userDomain;
    }

    public void setUserDomain(String userDomain) {
        this.userDomain = userDomain;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRepCommentId() {
        return repCommentId;
    }

    public void setRepCommentId(String repCommentId) {
        this.repCommentId = repCommentId;
    }

    public Date getRepCreatedAtComment() {
        return repCreatedAtComment;
    }

    public void setRepCreatedAtComment(Date repCreatedAtComment) {
        this.repCreatedAtComment = repCreatedAtComment;
    }

    public String getRepCommentText() {
        return repCommentText;
    }

    public void setRepCommentText(String repCommentText) {
        this.repCommentText = repCommentText;
    }

    public String getRepUserId() {
        return repUserId;
    }

    public void setRepUserId(String repUserId) {
        this.repUserId = repUserId;
    }

    public String getRepScreenName() {
        return repScreenName;
    }

    public void setRepScreenName(String repScreenName) {
        this.repScreenName = repScreenName;
    }

    public String getRepName() {
        return repName;
    }

    public void setRepName(String repName) {
        this.repName = repName;
    }

    public String getRepUrl() {
        return repUrl;
    }

    public void setRepUrl(String repUrl) {
        this.repUrl = repUrl;
    }

    public String getRepProfileImageUrl() {
        return repProfileImageUrl;
    }

    public void setRepProfileImageUrl(String repProfileImageUrl) {
        this.repProfileImageUrl = repProfileImageUrl;
    }

    public String getRepUserDomain() {
        return repUserDomain;
    }

    public void setRepUserDomain(String repUserDomain) {
        this.repUserDomain = repUserDomain;
    }

    public String getRepGender() {
        return repGender;
    }

    public void setRepGender(String repGender) {
        this.repGender = repGender;
    }

    public String getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(String weiboId) {
        this.weiboId = weiboId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getThumbnailPic() {
        return thumbnailPic;
    }

    public void setThumbnailPic(String thumbnailPic) {
        this.thumbnailPic = thumbnailPic;
    }

    public String getBmiddlePic() {
        return bmiddlePic;
    }

    public void setBmiddlePic(String bmiddlePic) {
        this.bmiddlePic = bmiddlePic;
    }

    public String getOrginalPic() {
        return orginalPic;
    }

    public void setOrginalPic(String orginalPic) {
        this.orginalPic = orginalPic;
    }

    public String getIsRetweeted() {
        return isRetweeted;
    }

    public void setIsRetweeted(String isRetweeted) {
        this.isRetweeted = isRetweeted;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
