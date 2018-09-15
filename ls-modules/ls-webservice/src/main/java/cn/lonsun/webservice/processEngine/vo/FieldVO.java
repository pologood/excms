package cn.lonsun.webservice.processEngine.vo;

import cn.lonsun.webservice.processEngine.enums.EFieldControlType;

/**
 * Created by lonsun on 2014/12/17.
 */
public class FieldVO {
    /**
     * 字段描述（中文名称）
     */
    private String fieldDesc;
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 控制类型
     */
    private EFieldControlType controlType;
    public FieldVO(){}
    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public EFieldControlType getControlType() {
        return controlType;
    }

    public void setControlType(EFieldControlType controlType) {
        this.controlType = controlType;
    }
}
