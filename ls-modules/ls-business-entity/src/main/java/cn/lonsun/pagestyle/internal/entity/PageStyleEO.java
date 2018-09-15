package cn.lonsun.pagestyle.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;
import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 界面样式
 * @author zhongjun
 * @createtime 2017-11-20
 */
@Entity
@Table(name = "cms_page_style")
public class PageStyleEO extends AMockEntity {

    public static PageStyleEO getInstance(){
        PageStyleEO ins = new PageStyleEO();
        ins.setStyle("");
        ins.setWidth("");
        ins.setName("");
        ins.setDefaultValue();
        return ins;
    }

    public void setDefaultValue(){
        if(isBase == null){
            isBase =0;
        }
        if(useAble == null){
            isBase = 1;
        }
        if(styleModelConfig == null){
            styleModelConfig = new ArrayList<String>();
        }
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**名称*/
    @Column(name = "name")
    private String name;
    /**页面宽度*/
    @Column(name = "width")
    private String width;
    /**样式*/
    @Column(name = "style")
    private String style;
    /**是否为基础样式*/
    @Column(name = "is_base")
    private Integer isBase = 0;
    /**是否启用*/
    @Column(name = "use_able")
    private Integer useAble = 1;

    @Transient
    private String modelCode;

    @Transient
    private List<String> styleModelConfig;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Integer getIsBase() {
        return isBase;
    }

    public void setIsBase(Integer isBase) {
        this.isBase = isBase;
    }

    public Integer getUseAble() {
        return useAble;
    }

    public void setUseAble(Integer useAble) {
        this.useAble = useAble;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public List<String> getStyleModelConfig() {
        return styleModelConfig;
    }

    public void setStyleModelConfig(List<String> styleModelConfig) {
        this.styleModelConfig = styleModelConfig;
    }
}
