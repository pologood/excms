package cn.lonsun.system.sitechart.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * Created by hu on 2016/8/15.
 */
public class MyCollectionVO extends PageQueryVO {
    private Long myCollectionId;
    private Long siteId;
    private String name;
    private String link;
    private String remark;
    private Long memberId;
    private String memberName;
    private String createTime;
    private String dateFormat;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getMyCollectionId() {
        return myCollectionId;
    }

    public void setMyCollectionId(Long myCollectionId) {
        this.myCollectionId = myCollectionId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
