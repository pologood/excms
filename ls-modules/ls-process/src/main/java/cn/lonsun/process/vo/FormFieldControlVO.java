
package cn.lonsun.process.vo;


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
public class FormFieldControlVO {

    private String writeable = EFieldControlType.writeable.toString();
    private String protectable = EFieldControlType.protectable.toString();
    private String comment = EFieldControlType.comment.toString();

    public Map<String,String> editableFields = new HashMap<String, String>();//可编辑字段

    public Map<String,String> commentFields = new HashMap<String, String>();//批示意见字段

    private boolean hasProtectFields = false;//是否有受保护字段

    public String getWriteable() {
        return writeable;
    }

    public void setWriteable(String writeable) {
        this.writeable = writeable;
    }

    public String getProtectable() {
        return protectable;
    }

    public void setProtectable(String protectable) {
        this.protectable = protectable;
    }

    public Map<String, String> getEditableFields() {
        return editableFields;
    }

    public void setEditableFields(Map<String, String> editableFields) {
        this.editableFields = editableFields;
    }

    public boolean isHasProtectFields() {
        return hasProtectFields;
    }

    public void setHasProtectFields(boolean hasProtectFields) {
        this.hasProtectFields = hasProtectFields;
    }

    public Map<String, String> getCommentFields() {
        return commentFields;
    }

    public void setCommentFields(Map<String, String> commentFields) {
        this.commentFields = commentFields;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 获取批示意见字段
     * @return
     */
    public String getCommentField(){
        String field = "";
        if(null != commentFields && commentFields.size() > 0){
            Set<String> keySet = commentFields.keySet();
            for(String key : keySet){
                field = key;
            }
        }
        return field;
    }
}
