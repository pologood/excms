package cn.lonsun.special.internal.vo;

/**
 * @author Doocal
 * @title: ${todo}
 * @package cn.lonsun.special.internal.vo
 * @description: ${todo}
 * @date 2016/12/9
 */
public class SpecialThumbVO {

    //缩略图名称
    private String name;

    //缩略图路径
    private String path;

    //是否默认
    private Boolean defaults;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getDefaults() {
        return defaults;
    }

    public void setDefaults(Boolean defaults) {
        this.defaults = defaults;
    }
}
