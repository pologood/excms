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
@Table(name="CMS_SINA_WEIBO_CONTENT_S")
public class SinaWeiboContentSEO extends AMockEntity {

    @Column(name="CREATED_AT_WEIBO")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createdAtWeibo;                              //status创建时间

    //微博具体内容
    @Id
    @Column(name="WEIBO_ID")
    private String weiboId;                                   //status id

    @Column(name="MID")
    private String mid;                                  //微博MID

    @Column(name="TEXT")
    private String text;                                 //微博内容
    //微博来源

    @Column(name="SOURCE_URL")
    private String sourceUrl;                               //来源连接

    @Column(name="RELATION_SHIP")
    private String relationShip;                               //微博来源

    @Column(name="SOURCE_NAME")
    private String sourceName;                               //来源文案名称

    @Column(name="FAVORITED")
    private String favorited;                           //是否已收藏

    @Column(name="THUMBNAIL_PIC")
    private String thumbnailPic;                         //微博内容中的图片的缩略地址

    @Column(name="BMIDDLE_PIC")
    private String bmiddlePic;                           //中型图片

    @Column(name="ORGINAL_PIC")
    private String originalPic;                          //原始图片

    @Column(name="GEO")
    private String geo;                                  //地理信息，保存经纬度，没有时不返回此字段

    @Column(name="REPOSTS_COUNT")
    private Integer repostsCount;                            //转发数

    @Column(name="COMMENTS_COUNT")
    private Integer commentsCount;                           //评论数

    @Column(name="ANNOTATIONS")
    private String annotations;                          //元数据，没有时不返回此字段

    @Column(name="MLEVEL")
    private Integer mlevel;

    @Column(name="IS_RETWEETED")
    private String isRetweeted = "false";                  //是否转发 默认否

    @Column(name="AUTH")
    private String auth;                                  //self：自己发布的微博 all:关注和自己微博 mention:@我的 favorite：我收藏的

    //微博发布用户信息
    @Column(name="USER_ID")
    private String userId;                      //用户UID

    @Column(name="SCREEN_NAME")
    private String screenName;            //微博昵称

    @Column(name="NAME")
    private String name;                  //显示名称，如Bill Gates,名称中间的空格正常显示(此特性暂不支持)

    @Column(name="PROVINCE")
    private Integer province;                 //省份编码（参考省份编码表）

    @Column(name="CITY")
    private Integer city;                     //城市编码（参考城市编码表）

    @Column(name="LOCATION")
    private String location;              //地址

    @Column(name="DESCRIPTION")
    private String description;           //个人描述

    @Column(name="URL")
    private String url;                   //用户博客地址

    @Column(name="PROFILE_IMAGE_URL")
    private String profileImageUrl;       //自定义图像

    @Column(name="USER_DOMAIN")
    private String userDomain;            //用户个性化URL

    @Column(name="GENDER")
    private String gender;                //性别,m--男，f--女,n--未知

    @Column(name="FOLLOWERS_COUNT")
    private Integer followersCount;           //粉丝数

    @Column(name="FRIENDS_COUNT")
    private Integer friendsCount;             //关注数

    @Column(name="STATUSES_COUNT")
    private Integer statusesCount;            //微博数

    @Column(name="FAVOURITES_COUNT")
    private Integer favouritesCount;          //收藏数

    @Column(name="CREATED_AT_USER")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createdAtUser;               //创建时间

    @Column(name="VERIFIED")
    private String verified;             //加V标示，是否微博认证用户

    @Column(name="VERIFIED_TYPE")
    private Integer verifiedType;             //认证类型

    @Column(name="ALLOW_ALL_ACT_MSG")
    private String allowAllActMsg;       //是否允许所有人给我发私信

    @Column(name="ALLOW_ALL_COMMENT")
    private String allowAllComment;      //是否允许所有人对我的微博进行评论

    @Column(name="FOLLOE_ME")
    private String followMe;             //此用户是否关注我

    @Column(name="AVATAR_LARGE")
    private String avatarLarge;           //大头像地址

    @Column(name="ONLINE_STATUS")
    private Integer onlineStatus;             //用户在线状态

    @Column(name="BI_FOLLOWERS_COUNT")
    private Integer biFollowersCount;         //互粉数

    @Column(name="REMARK")
    private String remark;                //备注信息，在查询用户关系时提供此字段。

    @Column(name="LANG")
    private String lang;                  //用户语言版本

    @Column(name="VERIFIED_REASON")
    private String verifiedReason;		  //认证原因

    @Column(name="WEIHAO")
    private String weihao;				  //微號

    @Column(name="SITE_ID")
    private Long siteId;

    public String getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(String weiboId) {
        this.weiboId = weiboId;
    }

    public Date getCreatedAtWeibo() {
        return createdAtWeibo;
    }

    public void setCreatedAtWeibo(Date createdAtWeibo) {
        this.createdAtWeibo = createdAtWeibo;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(String relationShip) {
        this.relationShip = relationShip;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getFavorited() {
        return favorited;
    }

    public void setFavorited(String favorited) {
        this.favorited = favorited;
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

    public String getOriginalPic() {
        return originalPic;
    }

    public void setOriginalPic(String originalPic) {
        this.originalPic = originalPic;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public Integer getRepostsCount() {
        return repostsCount;
    }

    public void setRepostsCount(Integer repostsCount) {
        this.repostsCount = repostsCount;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getAnnotations() {
        return annotations;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations;
    }

    public Integer getMlevel() {
        return mlevel;
    }

    public void setMlevel(Integer mlevel) {
        this.mlevel = mlevel;
    }

    public String getIsRetweeted() {
        return isRetweeted;
    }

    public void setIsRetweeted(String isRetweeted) {
        this.isRetweeted = isRetweeted;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
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

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(Integer friendsCount) {
        this.friendsCount = friendsCount;
    }

    public Integer getStatusesCount() {
        return statusesCount;
    }

    public void setStatusesCount(Integer statusesCount) {
        this.statusesCount = statusesCount;
    }

    public Integer getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(Integer favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public Date getCreatedAtUser() {
        return createdAtUser;
    }

    public void setCreatedAtUser(Date createdAtUser) {
        this.createdAtUser = createdAtUser;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public Integer getVerifiedType() {
        return verifiedType;
    }

    public void setVerifiedType(Integer verifiedType) {
        this.verifiedType = verifiedType;
    }

    public String getAllowAllActMsg() {
        return allowAllActMsg;
    }

    public void setAllowAllActMsg(String allowAllActMsg) {
        this.allowAllActMsg = allowAllActMsg;
    }

    public String getAllowAllComment() {
        return allowAllComment;
    }

    public void setAllowAllComment(String allowAllComment) {
        this.allowAllComment = allowAllComment;
    }

    public String getFollowMe() {
        return followMe;
    }

    public void setFollowMe(String followMe) {
        this.followMe = followMe;
    }

    public String getAvatarLarge() {
        return avatarLarge;
    }

    public void setAvatarLarge(String avatarLarge) {
        this.avatarLarge = avatarLarge;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Integer getBiFollowersCount() {
        return biFollowersCount;
    }

    public void setBiFollowersCount(Integer biFollowersCount) {
        this.biFollowersCount = biFollowersCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getVerifiedReason() {
        return verifiedReason;
    }

    public void setVerifiedReason(String verifiedReason) {
        this.verifiedReason = verifiedReason;
    }

    public String getWeihao() {
        return weihao;
    }

    public void setWeihao(String weihao) {
        this.weihao = weihao;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
