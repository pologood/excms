/*
 * 2014-12-13 <br/>
 * 
 */
package cn.lonsun.webservice.processEngine.vo;

/**
 * 流程引擎
 */
public class ModuleVO {
    /**
     * 主键
     */
    private Long moduleId;
    /**
     * 流程引擎名称
     */
    private String name;
    /**
     * 流程编码
     */
    private String code;
    /**
     *  表单类
     */
    private String formClass;
    /**
     * 表单类型
     */
    private Long formType;
    /**
     *  默认启动流程表单链接
     */
    private String formFirstHref;
    /**
     *  默认流程流转表单链接
     */
    private String formEditHref;

    public Long getModuleId() {
        return moduleId;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getFormClass() {
        return formClass;
    }

    public Long getFormType() {
        return formType;
    }

    public String getFormFirstHref() {
        return formFirstHref;
    }

    public String getFormEditHref() {
        return formEditHref;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setFormClass(String formClass) {
        this.formClass = formClass;
    }

    public void setFormType(Long formType) {
        this.formType = formType;
    }

    public void setFormFirstHref(String formFirstHref) {
        this.formFirstHref = formFirstHref;
    }

    public void setFormEditHref(String formEditHref) {
        this.formEditHref = formEditHref;
    }
}
