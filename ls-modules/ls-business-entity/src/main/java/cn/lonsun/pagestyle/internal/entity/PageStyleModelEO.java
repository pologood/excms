package cn.lonsun.pagestyle.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * 界面样式和内容模型关联
 * @author zhongjun
 * @createtime 2017-11-20
 */
@Entity
@Table(name = "cms_page_style_model")
public class PageStyleModelEO extends ABaseEntity {

    public PageStyleModelEO() {
    }

    public PageStyleModelEO(Long styleId, String modelCode) {
        this.styleId = styleId;
        this.modelCode = modelCode;
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**名称*/
    @Column(name = "style_id")
    private Long styleId;
    /**页面宽度*/
    @Column(name = "model_code")
    private String modelCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStyleId() {
        return styleId;
    }

    public void setStyleId(Long styleId) {
        this.styleId = styleId;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }
}
