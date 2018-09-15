package cn.lonsun.site.words.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_WORDS_EASYERR_CONF")
public class WordsEasyerrConfEO extends ABaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name="WORDS")
    private String words;

    @Column(name="REPLACE_WORDS")
    private String replaceWords;

    @Column(name="SITE_ID")
    private Long siteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getReplaceWords() {
        return replaceWords;
    }

    public void setReplaceWords(String replaceWords) {
        this.replaceWords = replaceWords;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
