package cn.lonsun.monitor.internal.vo;

import cn.lonsun.common.vo.PageQueryVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: MonitorDetailQueryVO
 * @Description: 日常监测更新详细查询VO
 * @author liuk
 * @date 2017年9月27日
 *
 */
public class MonitorDetailQueryVO extends PageQueryVO{

    //监测任务id(主表id)
    private Long monitorId;

    private List<Long> monitorIds;

    //所属栏目
    private Long columnId;

    //栏目类型
    private List<String> columnType;

    /**
     * 信息类型  新闻、信息公开
     */
    private String infoType;

    //栏目更新数量
    private Long updateCount;

    //未回复数量
    private Long unreplyCount;

	private Long createUserId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date startDate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date endDate;

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public List<String> getColumnType() {
        return columnType;
    }

    public void setColumnType(List<String> columnType) {
        this.columnType = columnType;
    }

    public Long getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(Long updateCount) {
        this.updateCount = updateCount;
    }

    public Long getUnreplyCount() {
        return unreplyCount;
    }

    public void setUnreplyCount(Long unreplyCount) {
        this.unreplyCount = unreplyCount;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public List<Long> getMonitorIds() {
        return monitorIds;
    }

    public void setMonitorIds(List<Long> monitorIds) {
        this.monitorIds = monitorIds;
    }
}
