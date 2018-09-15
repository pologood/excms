package cn.lonsun.content.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by fth on 2017/6/9.
 */
@Entity
@Table(name = "CMS_BASE_CONTENT_UPDATE")
public class BaseContentUpdateEO extends ABaseEntity {

    /*消息警示类型*/
    public enum WarningType {
        YELLOW_CARD_WARNING("YELLOW_CARD_WARNING", "黄牌警示"),
        RED_CARD_WARNING("RED_CARD_WARNING", "红牌警示");

        private String type;
        private String typeName;

        WarningType(String type, String typeName) {
            this.type = type;
            this.typeName = typeName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;// 主键
    @Column(name = "COLUMN_ID")
    private Long columnId;
    @Column(name = "SITE_ID")
    private Long siteId;
    @Column(name = "LAST_PUBLISH_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date lastPublishDate;//最后发布日期
    @Column(name = "WARNING_TYPE")
    private String warningType;
    @Column(name = "REC_USER_ID")
    private String recUserIds;// 消息接收人
    @Column(name = "MESSAGE")
    private String message;
    @Transient
    private String columnName;//栏目名称
    @Transient
    private String warningTypeName;//警示类型名称

    public BaseContentUpdateEO() {
    }

    /**
     * 给hibernate统计使用
     *
     * @param columnId
     * @param siteId
     * @param lastPublishDate
     */
    public BaseContentUpdateEO(Long columnId, Long siteId, Date lastPublishDate) {
        this.columnId = columnId;
        this.siteId = siteId;
        this.lastPublishDate = lastPublishDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Date getLastPublishDate() {
        return lastPublishDate;
    }

    public void setLastPublishDate(Date lastPublishDate) {
        this.lastPublishDate = lastPublishDate;
    }

    public String getWarningType() {
        return warningType;
    }

    public void setWarningType(String warningType) {
        this.warningType = warningType;
    }

    public String getRecUserIds() {
        return recUserIds;
    }

    public void setRecUserIds(String recUserIds) {
        this.recUserIds = recUserIds;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getWarningTypeName() {
        return warningTypeName;
    }

    public void setWarningTypeName(String warningTypeName) {
        this.warningTypeName = warningTypeName;
    }
}
