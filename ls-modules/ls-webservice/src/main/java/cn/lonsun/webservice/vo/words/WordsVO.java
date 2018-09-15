package cn.lonsun.webservice.vo.words;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author chen.chao
 * @version 2017-09-22 18:12
 */
public class WordsVO {

    private Long id;

    private String words;

    private String replaceWords;

    private Long siteId;

    private String siteName;


    /**是否入库 0 是 1 否*/
    private Integer isInto = 0;

    /**出处（来自云平台或站群）*/
    private String provenance;

    /*严重错误 0 是 1 否 */
    @Column(name="SERIOUS_ERR")
    private Integer seriousErr;

    /**推送时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date pushDate;

    /**推送时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

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

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getIsInto() {
        return isInto;
    }

    public void setIsInto(Integer isInto) {
        this.isInto = isInto;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public Integer getSeriousErr() {
        return seriousErr;
    }

    public void setSeriousErr(Integer seriousErr) {
        this.seriousErr = seriousErr;
    }

    public Date getPushDate() {
        return pushDate;
    }

    public void setPushDate(Date pushDate) {
        this.pushDate = pushDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
