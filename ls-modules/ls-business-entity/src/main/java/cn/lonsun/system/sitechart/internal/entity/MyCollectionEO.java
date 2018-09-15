package cn.lonsun.system.sitechart.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * Created by hu on 2016/8/15.
 */
@Entity
@Table(name="CMS_MY_COLLECTION")
public class MyCollectionEO extends ABaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID")
    private Long myCollectionId;

    @Column(name="SITE_ID")
    private Long siteId;
    //名称
    @Column(name="NAME")
    private String name;
    //链接
    @Column(name="LINK")
    private String link;
    //备注
    @Column(name="REMARK")
    private String remark;
    //会员id
    @Column(name="MEMBER_ID")
    private Long memberId;
    //会员名称
    @Column(name="MEMBER_NAME")
    private String memberName;

    public Long getMyCollectionId() {
        return myCollectionId;
    }

    public void setMyCollectionId(Long myCollectionId) {
        this.myCollectionId = myCollectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
