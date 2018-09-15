package cn.lonsun.special.internal.vo;

/**
 * @author Doocal
 * @title: ${todo}
 * @package cn.lonsun.special.internal.vo
 * @description: ${todo}
 * @date 2016/12/9
 */
public class SpecialModelVO {

    //数据库主键
    private Long id;

    //模型名称
    private String title;

    //模型类型
    private String columnType;

    //栏目模板
    private String navCode;

    //详细页模板
    private String newsTplCode;

    //模板类型，用于关系栏目使用
    private String modelCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getNavCode() {
        return navCode;
    }

    public void setNavCode(String navCode) {
        this.navCode = navCode;
    }

    public String getNewsTplCode() {
        return newsTplCode;
    }

    public void setNewsTplCode(String newsTplCode) {
        this.newsTplCode = newsTplCode;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }
}
