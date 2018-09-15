package cn.lonsun.monitor.task.internal.entity.vo;

import cn.lonsun.common.vo.PageQueryVO;

import java.util.Date;

/**
 * @author gu.fei
 * @version 2017-10-24 14:07
 */
public class HrefUseableQueryVO extends PageQueryVO {

    //任务ID
    private Long taskId;

    private Date date;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
