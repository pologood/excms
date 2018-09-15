package cn.lonsun.site.template.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name = "CMS_TEMPLATE_CONF")
public class TemplateConfEO extends ABaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Type {
        Real, //真实
        Special, //专题
        Virtual //虚拟
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "SITE_ID")
    private Long siteId;

    @Column(name = "PARENT_ID")
    private Long pid;

    @Column(name = "TEMP_NAME")
    private String name;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_PATH")
    private String filePath;

    @Column(name = "TEMP_TYPE")
    private String tempType;

    @Column(name = "LEAF")
    private Integer leaf = 0; // 0:是 1:否

    @Column(name = "TYPE")
    private String type;

    @Column(name = "SPECIAL_ID")
    private Long specialId;

    /**
     * 专题模板自定义样式
     */
    @Column(name = "SPECIAL_MODULE_SYTLE")
    private String specialModuleSytle;

    @Transient
    private String tempTypeName;

    @Transient
    private String tplContent;

    @Transient
    private boolean template = true; // 是否是模板

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTempType() {
        return tempType;
    }

    public void setTempType(String tempType) {
        this.tempType = tempType;
    }

    public Integer getLeaf() {
        return leaf;
    }

    public void setLeaf(Integer leaf) {
        this.leaf = leaf;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTempTypeName() {
        return tempTypeName;
    }

    public void setTempTypeName(String tempTypeName) {
        this.tempTypeName = tempTypeName;
    }

    public String getTplContent() {
        return tplContent;
    }

    public void setTplContent(String tplContent) {
        this.tplContent = tplContent;
    }

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public Long getSpecialId() {
        return specialId;
    }

    public void setSpecialId(Long specialId) {
        this.specialId = specialId;
    }

    public String getSpecialModuleSytle() {
        return specialModuleSytle;
    }

    public void setSpecialModuleSytle(String specialModuleSytle) {
        this.specialModuleSytle = specialModuleSytle;
    }
}