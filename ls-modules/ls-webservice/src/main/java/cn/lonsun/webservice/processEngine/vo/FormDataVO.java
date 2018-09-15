package cn.lonsun.webservice.processEngine.vo;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by roc on 2016-1-13.
 */
public class FormDataVO implements Serializable {
    private Long formId;
    private Long elementId;
    private Long taskId;
    private Map<String,Object> data;
    public Long getFormId() {
        return formId;
    }
    public void setFormId(Long formId) {
        this.formId = formId;
    }
    public Long getElementId() {
        return elementId;
    }
    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Map<String, Object> getData() {
        return data;
    }
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
