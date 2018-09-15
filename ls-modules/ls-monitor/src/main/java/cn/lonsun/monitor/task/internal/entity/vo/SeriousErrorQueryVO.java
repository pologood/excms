package cn.lonsun.monitor.task.internal.entity.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * @author gu.fei
 * @version 2017-10-24 14:07
 */
public class SeriousErrorQueryVO extends PageQueryVO {


    //任务ID
    private Long taskId;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}
