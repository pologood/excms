package cn.lonsun.govbbs.internal.vo;

/**
 * Created by zhangchao on 2016/12/30.
 */
public class OrganCountVO implements java.io.Serializable{

    private Long organId;

    private Long plateId;

    private Long total  = 0L;

    private Long todayTotal = 0L;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getPlateId() {
        return plateId;
    }

    public void setPlateId(Long plateId) {
        this.plateId = plateId;
    }

    public Long getTodayTotal() {
        return todayTotal;
    }

    public void setTodayTotal(Long todayTotal) {
        this.todayTotal = todayTotal;
    }
}
