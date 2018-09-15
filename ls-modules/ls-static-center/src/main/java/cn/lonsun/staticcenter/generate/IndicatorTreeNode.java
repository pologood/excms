package cn.lonsun.staticcenter.generate;

/**
 * @author gu.fei
 * @version 2017-02-17 14:55
 */
public class IndicatorTreeNode {

    private Long lev; //树层级
    private Long indicatorId;//栏目ID
    private String name;//栏目名称
    private Long parentId;//父节点ID
    private Integer isParent;//是否父节点 0:否 1:是
    private Integer isStartUrl;//是否启动跳转 0:否 1:是
    private String transUrl;//跳转地址
    private Integer transWindow;//跳转方式 0:新窗口 1:本页面
    private Integer isShow;//是否显示栏目 0:隐藏 1:显示

    public Long getLev() {
        return lev;
    }

    public void setLev(Long lev) {
        this.lev = lev;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getIsParent() {
        return isParent;
    }

    public void setIsParent(Integer isParent) {
        this.isParent = isParent;
    }

    public Integer getIsStartUrl() {
        return isStartUrl;
    }

    public void setIsStartUrl(Integer isStartUrl) {
        this.isStartUrl = isStartUrl;
    }

    public String getTransUrl() {
        return transUrl;
    }

    public void setTransUrl(String transUrl) {
        this.transUrl = transUrl;
    }

    public Integer getTransWindow() {
        return transWindow;
    }

    public void setTransWindow(Integer transWindow) {
        this.transWindow = transWindow;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }
}
