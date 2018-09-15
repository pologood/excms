package cn.lonsun.content.onlineDeclaration.vo;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.onlineDeclaration.internal.entity.OnlineDeclarationEO;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-12<br/>
 */

public class OnlineDeclarationVO extends OnlineDeclarationEO{

    private String title;

    private Long columnId;

    private Long siteId;

    private Integer isPublish=0;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date publishDate;

    private String typeCode= BaseContentEO.TypeCode.onlineDeclaration.toString();

    private String link;

    private Integer recType;

    private Integer workFlowStatus;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Integer getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getRecType() {
        return recType;
    }

    public void setRecType(Integer recType) {
        this.recType = recType;
    }

    public Integer getWorkFlowStatus() {
        return workFlowStatus;
    }

    public void setWorkFlowStatus(Integer workFlowStatus) {
        this.workFlowStatus = workFlowStatus;
    }
}
