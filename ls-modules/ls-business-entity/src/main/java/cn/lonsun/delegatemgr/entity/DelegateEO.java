package cn.lonsun.delegatemgr.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-6<br/>
 */
@Entity
@Table(name = "cms_delegate")
public class DelegateEO extends AMockEntity {

    private static final long serialVersionUID = -1300742296285581640L;

    //主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    //代表证号
    @Column(name="dele_num")
    private String deleNum;

    //姓名
    @Column(name="name")
    private String name;

    //民族
    @Column(name="nation")
    private String nation;

    //籍贯
    @Column(name="birth_place")
    private String birthPlace;

    //性别
    @Column(name="sex")
    private String sex;

    //出生年份
    @Column(name="birth_year")
    private Integer birthYear;

    //入党月份
    @Column(name="birth_month")
    private Integer birthMonth;

    //入党日期
    @Column(name="birth_day")
    private Integer birthDay;

    //相片名称
    @Column(name="pic_name")
    private String picName;

    //相片路径
    @Column(name="pic_path")
    private String picPath;

    //代表团
    @Column(name="delegation")
    private String delegation;

    //代表小组
    @Column(name="dele_group")
    private String deleGroup;

    //届
    @Column(name="session_")
    private String session;

    //党派
    @Column(name="party")
    private String party;

    //入党年份
    @Column(name="party_year")
    private Integer partyYear;

    //入党月份
    @Column(name="party_month")
    private Integer partyMonth;

    //入党日期
    @Column(name="party_day")
    private Integer partyDay;

    //代表构成
    @Column(name="dele_comp")
    private String deleComp;

    //是否连任
    @Column(name="is_term")
    private Integer isTerm=0;

    //代表资格
    @Column(name="dele_qualify")
    private String deleQualify;

    //身份证号
    @Column(name="id_num")
    private String idNum;

    //行业分类
    @Column(name="industry_class")
    private String industryClass;

    //职业构成
    @Column(name="career_comp")
    private String careerComp;

    //增加方式
    @Column(name="add_type")
    private String addType;

    //工作单位
    @Column(name="work_unit")
    private String workUnit;

    //工作年份
    @Column(name="work_year")
    private Integer workYear;

    //工作月份
    @Column(name="work_month")
    private Integer workMonth;

    //工作日期
    @Column(name="work_day")
    private Integer workDay;

    //职称
    @Column(name="work_title")
    private String workTitle;

    //职务
    @Column(name="work")
    private String work;

    //健康状态
    @Column(name="health")
    private String health;

    //毕业院校
    @Column(name="grad_school")
    private String gradSchool;

    //专业
    @Column(name="study")
    private String study;

    //学历
    @Column(name="education")
    private String education;

    //手机号码
    @Column(name="phone_num")
    private Long phoneNum;

    //电话号码
    @Column(name="tel_num")
    private String telNum;

    //邮编
    @Column(name="zip_code")
    private String zipCode;

    //首页显示
    @Column(name="is_show")
    private Integer isShow=1;

    //通信地址
    @Column(name="address")
    private String address;

    //电子邮件
    @Column(name="email")
    private String email;

    //备注
    @Column(name="remark")
    private String remark;

    @Column(name="password")
    private String password="111111";

    @Column(name="site_id")
    private Long siteId;

    @Transient
    private Long count=0L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeleNum() {
        return deleNum;
    }

    public void setDeleNum(String deleNum) {
        this.deleNum = deleNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(Integer birthMonth) {
        this.birthMonth = birthMonth;
    }

    public Integer getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Integer birthDay) {
        this.birthDay = birthDay;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getDelegation() {
        return delegation;
    }

    public void setDelegation(String delegation) {
        this.delegation = delegation;
    }

    public String getDeleGroup() {
        return deleGroup;
    }

    public void setDeleGroup(String deleGroup) {
        this.deleGroup = deleGroup;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public Integer getPartyYear() {
        return partyYear;
    }

    public void setPartyYear(Integer partyYear) {
        this.partyYear = partyYear;
    }

    public Integer getPartyMonth() {
        return partyMonth;
    }

    public void setPartyMonth(Integer partyMonth) {
        this.partyMonth = partyMonth;
    }

    public Integer getPartyDay() {
        return partyDay;
    }

    public void setPartyDay(Integer partyDay) {
        this.partyDay = partyDay;
    }

    public String getDeleComp() {
        return deleComp;
    }

    public void setDeleComp(String deleComp) {
        this.deleComp = deleComp;
    }

    public Integer getIsTerm() {
        return isTerm;
    }

    public void setIsTerm(Integer isTerm) {
        this.isTerm = isTerm;
    }

    public String getDeleQualify() {
        return deleQualify;
    }

    public void setDeleQualify(String deleQualify) {
        this.deleQualify = deleQualify;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getIndustryClass() {
        return industryClass;
    }

    public void setIndustryClass(String industryClass) {
        this.industryClass = industryClass;
    }

    public String getCareerComp() {
        return careerComp;
    }

    public void setCareerComp(String careerComp) {
        this.careerComp = careerComp;
    }

    public String getAddType() {
        return addType;
    }

    public void setAddType(String addType) {
        this.addType = addType;
    }

    public String getWorkUnit() {
        return workUnit;
    }

    public void setWorkUnit(String workUnit) {
        this.workUnit = workUnit;
    }

    public Integer getWorkYear() {
        return workYear;
    }

    public void setWorkYear(Integer workYear) {
        this.workYear = workYear;
    }

    public Integer getWorkMonth() {
        return workMonth;
    }

    public void setWorkMonth(Integer workMonth) {
        this.workMonth = workMonth;
    }

    public Integer getWorkDay() {
        return workDay;
    }

    public void setWorkDay(Integer workDay) {
        this.workDay = workDay;
    }

    public String getWorkTitle() {
        return workTitle;
    }

    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getGradSchool() {
        return gradSchool;
    }

    public void setGradSchool(String gradSchool) {
        this.gradSchool = gradSchool;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public Long getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(Long phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
