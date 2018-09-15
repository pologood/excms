package cn.lonsun.pagestyle.internal.vo;

public class PageStyleVO {

    private Long id;
    /**名称*/
    private String name;
    /**页面宽度*/
    private String width;
    /**样式*/
    private String style;
    /**是否为基础样式*/
    private Integer isBase = 0;
    /**是否启用*/
    private Integer useAble = 1;

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
}
