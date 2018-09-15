package cn.lonsun.site.words.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_CONTENT_CHECK")
public class ContentCheckEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum contentType {
        articleNews,//文章新闻
        pictureNews,//图片新闻
        videoNews//视频新闻
    }

    public enum checkType {
        SENSITIVE, //敏感词
        EASYERR  //易错词
    }

    public enum Type {
        title, //标题检测
        subTitle,//副标题
        remarks,//摘要
        content,  //内容检测
        guest, //留言
        repGuest //留言回复
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "CONTENT_ID")
    private Long contentId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "SUB_TITLE")
    private String subTitle;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "CONTENT_TYPE")
    private String contentType;

    @Column(name = "CHECK_TYPE")
    private String checkType;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "GUEST")
    private String guest;

    @Column(name = "REP_GUEST")
    private String repGuest;

    @Column(name="WORDS")
    private String words;

    @Column(name="REPLACE_WORDS")
    private String replaceWords;

    @Column(name = "SITE_ID")
    private Long siteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getRepGuest() {
        return repGuest;
    }

    public void setRepGuest(String repGuest) {
        this.repGuest = repGuest;
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