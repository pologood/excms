package cn.lonsun.special.internal.vo;

/**
 * @author Doocal
 * @title: ${todo}
 * @package cn.lonsun.special.internal.vo
 * @description: ${todo}
 * @date 2016/12/9
 */
public class SpecialColumnVO {

    //数据库主键
    private Long id;

    //站点ID
    private Long siteId;

    //模板名称
    private String title;

    //关联模型CODE
    private String modelCode;

    //栏目类型
    private String columnCode;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }
}
