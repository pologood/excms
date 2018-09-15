package cn.lonsun.monitor.words.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author chen.chao
 * @version 2017-09-21 17:22
 */
@Entity
@Table(name="MONITOR_WORDS_SENSITIVE_CONF")
public class WordsSensitiveEO extends ABaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Provenance{
        Cloud,//云平台
        Other//其他（站群）
    }

    //推送状态
    public enum  PushStatus{
        Pushed,//已推
        UnPush//未推
    }

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


    @Column(name="SITE_NAME")
    private String siteName;

    /**是否入库 0 是 1 否*/
    @Column(name="IS_INTO")
    private Integer isInto = 0;

    /**出处（来自云平台或站群）*/
    @Column(name="PROVENANCE")
    private String provenance = Provenance.Other.toString();

    /*严重错误 0 是 1 否 */
    @Column(name="SERIOUS_ERR")
    private Integer seriousErr;

    /*是否推送 0 是 1 否 */
    @Column(name="WHETHER_PUSH")
    private Integer whetherPush;

    /*推送状态*/
    @Column(name="PUSH_STATUS")
    private String pushStatus = PushStatus.UnPush.toString();

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

    public Integer getWhetherPush() {
        return whetherPush;
    }

    public void setWhetherPush(Integer whetherPush) {
        this.whetherPush = whetherPush;
    }

    public String getPushStatus() {
        return pushStatus;
    }

    public void setPushStatus(String pushStatus) {
        this.pushStatus = pushStatus;
    }
}
