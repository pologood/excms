package cn.lonsun.nlp.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 会员关注会员标签关系表
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 9:28
 */
@Entity
@Table(name="CMS_NLP_MEMBER_LABEL")
public class NlpMemberLabelRelEO extends AMockEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MEMBER_ID")
    private Long memberId;

    /**
     * 标签id，即对应的关键词id
     */
    @Column(name = "LABEL_ID")
    private Long labelId;

    @Column(name = "SITE_ID")
    private Long siteId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
