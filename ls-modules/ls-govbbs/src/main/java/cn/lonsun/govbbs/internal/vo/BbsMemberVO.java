package cn.lonsun.govbbs.internal.vo;

import cn.lonsun.system.member.internal.entity.MemberEO;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhangchao on 2017/1/5.
 */
public class BbsMemberVO implements Serializable {


    public enum MemberType {
        WEB(0), // 网站会员
        ORGAN(1),// 部门会员
        TOURIST(2);//游客
        private Integer mt;
        private MemberType(Integer mt){
            this.mt=mt;
        }
        public Integer getMemberType(){
            return mt;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private Long id;

    private Long siteId;

    //登录名称
    private String uid;

    //头像
    private String img;

    //昵称
    private String name;

    //邮箱
    private String email;

    //手机号
    private String phone;

    private String address;

    private String idCard;

    //性别
    private Integer sex = MemberEO.Sex.Man.getSex();

    //积分
    private Long memberPoints = 0L;

    //会员类型
    private Integer memberType = MemberType.TOURIST.getMemberType();

    //会员角色Id
    private Long memberRoleId;

    //会员单位id
    private Long unitId;

    //会员单位名称
    private String unitName;

    private String question;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date lastLoginDate;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Long getMemberPoints() {
        return memberPoints;
    }

    public void setMemberPoints(Long memberPoints) {
        this.memberPoints = memberPoints;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getMemberType() {
        return memberType;
    }

    public void setMemberType(Integer memberType) {
        this.memberType = memberType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getMemberRoleId() {
        return memberRoleId;
    }

    public void setMemberRoleId(Long memberRoleId) {
        this.memberRoleId = memberRoleId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
}
