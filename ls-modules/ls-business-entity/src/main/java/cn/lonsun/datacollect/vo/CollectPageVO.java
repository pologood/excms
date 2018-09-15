package cn.lonsun.datacollect.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * @author gu.fei
 * @version 2016-1-21 14:48
 */
public class CollectPageVO extends PageQueryVO {

    private String keys;

    private String keyValue;

    private Long taskId;

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}
