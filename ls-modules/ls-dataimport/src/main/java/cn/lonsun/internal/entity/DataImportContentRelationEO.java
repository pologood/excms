package cn.lonsun.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 综合信息内容对应关系
 * @author liuk
 */
@Entity
@Table(name = "DATAIMPORT_CONTENT_RELATION")
public class DataImportContentRelationEO extends AMockEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * 栏目配置关系ID
     */
    @Column(name = "COLUMN_RELATION_ID")
    private Long columnRelationId;
    /**
     * 老系统新闻id
     */
    @Column(name = "OLD_CONTENT_ID")
    private String oldContentId;
    /**
     * EX8新闻id
     */
    @Column(name = "NEW_CONTENT_ID")
    private Long newContentId;

    /**
     * 站点id
     */
    @Column(name = "SITE_ID")
    private Long siteId;

    /** 新闻事件 */
    @Column(name = "DATA_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date dataDate;

    /**
     * 备用字段
     */
    @Column(name = "COLUMN_13")
    private String column13;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getColumnRelationId() {
        return columnRelationId;
    }

    public void setColumnRelationId(Long columnRelationId) {
        this.columnRelationId = columnRelationId;
    }

    public String getOldContentId() {
        return oldContentId;
    }

    public void setOldContentId(String oldContentId) {
        this.oldContentId = oldContentId;
    }

    public Long getNewContentId() {
        return newContentId;
    }

    public void setNewContentId(Long newContentId) {
        this.newContentId = newContentId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }

    public String getColumn13() {
        return column13;
    }

    public void setColumn13(String column13) {
        this.column13 = column13;
    }
}
