package cn.lonsun.site.contentModel.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 模型模板关联实体类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-8<br/>
 */
@Entity
@Table(name="CMS_MODEL_TEMPLATE")
public class ModelTemplateEO extends AMockEntity {
    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long tplId;

    //内容模型id
    @Column(name="model_id")
    private Long modelId;

    //二级栏目模板
    @Column(name="COLUMN_TEMP_ID")
    private Long columnTempId;

    //栏目类型Code
    @Column(name="model_type_code")
    private String modelTypeCode;

    //绑定流程id
    @Column(name="process_id")
    private Long processId;

    //绑定流程name
    @Column(name="process_name")
    private String processName;

    //文章页模板Id
    @Column(name="artical_temp_Id")
    private Long articalTempId;

    //web文章页模板Id
    @Column(name="WAPARTICAL_TEMP_ID")
    private Long wapArticalTempId;

    //web二级栏目模板
    @Column(name="WAPCOLUMN_TEMP_ID")
    private Long wapColumnTempId;

    //是否为主模板配置：0：否,1：是
    @Column(name="type")
    private Integer type=0;

    public Long getTplId() {
        return tplId;
    }

    public void setTplId(Long tplId) {
        this.tplId = tplId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getModelTypeCode() {
        return modelTypeCode;
    }

    public void setModelTypeCode( String modelTypeCode) {
        this.modelTypeCode = modelTypeCode;
    }

    public Long getArticalTempId() {
        return articalTempId;
    }

    public void setArticalTempId(Long articalTempId) {
        this.articalTempId = articalTempId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getColumnTempId() {
        return columnTempId;
    }

    public void setColumnTempId(Long columnTempId) {
        this.columnTempId = columnTempId;
    }

    public Long getWapArticalTempId() {
        return wapArticalTempId;
    }

    public void setWapArticalTempId(Long wapArticalTempId) {
        this.wapArticalTempId = wapArticalTempId;
    }

    public Long getWapColumnTempId() {
        return wapColumnTempId;
    }

    public void setWapColumnTempId(Long wapColumnTempId) {
        this.wapColumnTempId = wapColumnTempId;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
