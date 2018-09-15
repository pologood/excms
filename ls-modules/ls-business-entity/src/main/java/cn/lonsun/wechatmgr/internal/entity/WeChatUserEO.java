package cn.lonsun.wechatmgr.internal.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 
 * @ClassName: WeChatUserEO
 * @Description: 关注用户表
 * @author Hewbing
 * @date 2015年12月22日 下午8:20:05
 *
 */
@Entity
@Table(name = "CMS_WECHAT_USER")
public class WeChatUserEO extends AMockEntity {

    /**
	 * 
	 */
    private static final long serialVersionUID = -7306048457955654495L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SITE_ID")
    private Long siteId;
    // 关注标识
    @Column(name = "SUBSCRIBE")
    private String subscribe;
    // 关注用户的openID
    @Column(name = "OPENID")
    private String openid;
    // 昵称
    @Column(name = "NICKNAME")
    private String nickname;
    // 性别
    @Column(name = "SEX")
    private Integer sex;
    // 国
    @Column(name = "COUNTRY")
    private String country;
    // 省
    @Column(name = "PROVINCE")
    private String province;
    // 市
    @Column(name = "CITY")
    private String city;
    // 语言
    @Column(name = "LANGUAGE")
    private String language;
    // 头像
    @Column(name = "HEADIMGURL")
    private String headimgurl;
    //
    @Column(name = "SUBSCRIBE_TIME")
    private Long subscribe_time;

    @Column(name = "UNIONID")
    private String unionid;

    @Column(name = "REMARK")
    private String remark;
    // 分组ID
    @Column(name = "GROUPID")
    private Long groupid;
    // 用户被打上的标签ID列表
    @Transient
    private List<Long> tagid_list;
    @Transient
    private String groupName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public Long getSubscribe_time() {
        return subscribe_time;
    }

    public void setSubscribe_time(Long subscribe_time) {
        this.subscribe_time = subscribe_time;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getGroupid() {
        return groupid;
    }

    public void setGroupid(Long groupid) {
        this.groupid = groupid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Long> getTagid_list() {
        return tagid_list;
    }

    public void setTagid_list(List<Long> tagid_list) {
        this.tagid_list = tagid_list;
    }
}