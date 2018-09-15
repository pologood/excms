package cn.lonsun.nlp.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * 会员浏览关键词相关文章记录关系表
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 9:28
 */
@Entity
@Table(name="CMS_NLP_KEY_WORDS_MEMBER_REL")
public class NlpKeyWordsMemberRelEO extends ABaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "KEY_WORD_ID")
    private Long keyWordId;

    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "CONTENT_ID")
    private Long contentId;

    @Column(name = "SITE_ID")
    private Long siteId;

    @Column(name = "IP")
    private String ip;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKeyWordId() {
        return keyWordId;
    }

    public void setKeyWordId(Long keyWordId) {
        this.keyWordId = keyWordId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
