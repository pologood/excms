
package cn.lonsun.content.vo;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;

/**
 * 复制引用关联表查询vo
 * @Author: liuk
 * @Date: 2018-5-4 17:00:06
 */
public class ContentReferRelationPageVO extends PageQueryVO {
    //引用编码
    private String modelCode;

    //引用类型
    private String type= ContentReferRelationEO.TYPE.COPY.toString();

    //被引用的主键
    private Long causeById;

    //引用者的栏目
    private Long columnId;

    //引用者ID
    private Long referId;

    //引用者栏目名称
    private String referName;

    //引用者类型 默认内容管理
    private String referModelCode ;

    //信息公开目录id
    private Long catId;

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCauseById() {
        return causeById;
    }

    public void setCauseById(Long causeById) {
        this.causeById = causeById;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getReferId() {
        return referId;
    }

    public void setReferId(Long referId) {
        this.referId = referId;
    }

    public String getReferName() {
        return referName;
    }

    public void setReferName(String referName) {
        this.referName = referName;
    }

    public String getReferModelCode() {
        return referModelCode;
    }

    public void setReferModelCode(String referModelCode) {
        this.referModelCode = referModelCode;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }
}
