package cn.lonsun.publicInfo.vo;

import cn.lonsun.common.vo.PageQueryVO;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by fth on 2017/6/9.
 */
public class PublicCatalogUpdateQueryVO extends PageQueryVO {

    private Long organId;
    private Long userId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date startLastPublishDate;//最后发布日期
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endLastPublishDate;//最后发布日期
    private String warningType;

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getStartLastPublishDate() {
        return startLastPublishDate;
    }

    public void setStartLastPublishDate(Date startLastPublishDate) {
        this.startLastPublishDate = startLastPublishDate;
    }

    public Date getEndLastPublishDate() {
        return endLastPublishDate;
    }

    public void setEndLastPublishDate(Date endLastPublishDate) {
        this.endLastPublishDate = endLastPublishDate;
    }

    public String getWarningType() {
        return warningType;
    }

    public void setWarningType(String warningType) {
        this.warningType = warningType;
    }
}
