package cn.lonsun.statistics;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-2-16<br/>
 */

public class GuestListVO {
    private String organName;
    private Long organId;
    private Long recCount=0L;
    private Long dealCount=0L;
    private Long undoCount=0L;
    private Long unSatCount=0L;
    private Integer rate=0;

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getRecCount() {
        return recCount;
    }

    public void setRecCount(Long recCount) {
        this.recCount = recCount;
    }

    public Long getDealCount() {
        return dealCount;
    }

    public void setDealCount(Long dealCount) {
        this.dealCount = dealCount;
    }

    public Long getUndoCount() {
        return undoCount;
    }

    public void setUndoCount(Long undoCount) {
        this.undoCount = undoCount;
    }

    public Long getUnSatCount() {
        return unSatCount;
    }

    public void setUnSatCount(Long unSatCount) {
        this.unSatCount = unSatCount;
    }
}
