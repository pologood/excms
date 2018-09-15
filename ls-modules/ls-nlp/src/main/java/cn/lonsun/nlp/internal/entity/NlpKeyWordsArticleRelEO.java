package cn.lonsun.nlp.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * 关键词与文章对应关系表
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 9:28
 */
@Entity
@Table(name="CMS_NLP_KEY_WORDS_ARTICLE_REL")
public class NlpKeyWordsArticleRelEO extends ABaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "KEY_WORD_ID")
    private Long keyWordId;

    @Column(name = "CONTENT_ID")
    private Long contentId;

    @Column(name = "SITE_ID")
    private Long siteId;

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

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
