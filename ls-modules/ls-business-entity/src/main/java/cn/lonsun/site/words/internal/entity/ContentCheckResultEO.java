package cn.lonsun.site.words.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_CONTENT_CHECK_RESULT")
public class ContentCheckResultEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum checkType {
        sensitive, //敏感词
        easyerr  //易错词
    }

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name="CHECK_ID")
    private Long checkId;

    @Column(name="WORDS")
    private String words;

    @Column(name="REPLACE_WORDS")
    private String replaceWords;

    @Column(name="CHECK_TYPE")
    private String checkType;

    @Column(name="SITE_ID")
    private Long siteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCheckId() {
        return checkId;
    }

    public void setCheckId(Long checkId) {
        this.checkId = checkId;
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

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
