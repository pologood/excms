package cn.lonsun.publicInfo.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 单位领导 <br/>
 * 
 * @date 2016年09月19日 <br/>
 * @author liukun <br/>
 * @version v1.0 <br/>
 */
@Entity
@Table(name = "CMS_PUBLIC_LEADERS")
public class PublicLeadersEO extends AMockEntity {

    public enum Status {
        Normal, Removed;
    }

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "LEARDERS_ID")
    private Long leadersId;// 主键
    @Column(name = "SITE_ID")
    private Long siteId;// 站点id
    @Column(name = "ORGAN_NAME")
    private String organName;// 单位名称
    @Column(name = "ORGAN_ID")
    private Long organId;// 单位id
    @Column(name = "LEAGERS_NAME")
    private String leadersName;// 领导姓名
    @Column(name = "LEADERS_NUM")
    private String leadersNum;// 领导编号
    @Column(name = "IMAGE_LINK")
    private String imageLink;// 头像 图片路径
    @Column(name = "POST")
    private String post;// 职务
    @Column(name = "WORK")
    private String work;// 分工
    @Column(name = "SORT_NUM")
    private Long sortNum;
    @Column(name = "STATUS")
    private String status; //是否启用

    @Transient
    private String experience;// 工作简历/工作流程

    public Long getLeadersId() {
        return leadersId;
    }

    public void setLeadersId(Long leadersId) {
        this.leadersId = leadersId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getLeadersName() {
        return leadersName;
    }

    public void setLeadersName(String leadersName) {
        this.leadersName = leadersName;
    }

    public String getLeadersNum() {
        return leadersNum;
    }

    public void setLeadersNum(String leadersNum) {
        this.leadersNum = leadersNum;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public Long getSortNum() {
        return sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}