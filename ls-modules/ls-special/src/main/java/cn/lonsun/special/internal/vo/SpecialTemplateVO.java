package cn.lonsun.special.internal.vo;

/**
 * @author Doocal
 * @title: ${todo}
 * @package cn.lonsun.special.internal.vo
 * @description: ${todo}
 * @date 2016/12/9
 */
public class SpecialTemplateVO {

    //数据库主键
    private Long id;

    //模板名称
    private String fileName;

    //模板CODE，区分关联项
    private String templateCode;

    //模板类型
    private String templateType;

    //模板内容
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
