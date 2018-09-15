
package cn.lonsun.process.vo;


import cn.lonsun.common.util.AppUtil;
import cn.lonsun.webservice.processEngine.enums.EFieldControlType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 表单字段控制VO
 *@date 2014-12-23 9:17  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
public class ActivityFieldControlVO {

    private String writableFields;

    private String protectFields;

    private String commentFields;

    private boolean hasProtectFields = false;//是否有受保护字段


    public String getWritableFields() {
        return writableFields;
    }

    public void setWritableFields(String writableFields) {
        this.writableFields = writableFields;
    }

    public String getProtectFields() {
        return protectFields;
    }

    public void setProtectFields(String protectFields) {
        this.protectFields = protectFields;
    }

    public String getCommentFields() {
        return commentFields;
    }

    public void setCommentFields(String commentFields) {
        this.commentFields = commentFields;
    }


    public boolean isHasProtectFields() {
        return !AppUtil.isEmpty(protectFields);
    }

}
