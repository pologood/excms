package cn.lonsun.govbbs.internal.vo;

/**
 * Created by zhangchao on 2016/12/15.
 */
public class PlateShowVO implements java.io.Serializable{


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long plateId;

    //(站点id)
    private Long siteId;

    private Long parentId;

    private String parentIds;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getPlateId() {
        return plateId;
    }

    public void setPlateId(Long plateId) {
        this.plateId = plateId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
