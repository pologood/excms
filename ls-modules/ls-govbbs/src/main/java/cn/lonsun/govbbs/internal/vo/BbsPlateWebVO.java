package cn.lonsun.govbbs.internal.vo;

/**
 * Created by zhangchao on 2016/12/30.
 */
public class BbsPlateWebVO implements java.io.Serializable{


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long plateId;

    private Long parentId;

    private String name;

    private String parentIds;

    private Long total;

    private Long todayTotal;

    private Integer canThread;

    public Long getPlateId() {
        return plateId;
    }

    public void setPlateId(Long plateId) {
        this.plateId = plateId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

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

    public Long getTodayTotal() {
        return todayTotal;
    }

    public void setTodayTotal(Long todayTotal) {
        this.todayTotal = todayTotal;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getCanThread() {
        return canThread;
    }

    public void setCanThread(Integer canThread) {
        this.canThread = canThread;
    }
}
