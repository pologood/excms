package cn.lonsun.lsrobot.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2016-2-17 14:07
 */
@Entity
@Table(name = "CMS_LS_ROBOT_SOURCES")
public class LsRobotSourcesEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "SORT_NUM")
    private Long sortNum;

    @Column(name = "SEQ_NUM")
    private String seqNum;//引导序号

    @Column(name = "TITLE")
    private String title; //标题

    @Column(name = "CONTENT")
    private String content; //内容

    @Column(name = "IF_ACTIVE")
    private String ifActive; //是否启用

    @Column(name = "IF_SHOW")
    private String ifShow; //是否启用

    @Column(name = "SITE_ID")
    private Long siteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSortNum() {
        return sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    public String getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIfActive() {
        return ifActive;
    }

    public void setIfActive(String ifActive) {
        this.ifActive = ifActive;
    }

    public String getIfShow() {
        return ifShow;
    }

    public void setIfShow(String ifShow) {
        this.ifShow = ifShow;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}