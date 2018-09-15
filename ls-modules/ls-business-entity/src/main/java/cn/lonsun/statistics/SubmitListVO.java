package cn.lonsun.statistics;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-2-17<br/>
 */

public class SubmitListVO {
    private String organName;
    private Long organId;
    private Long count=0L;
    private Long employCount=0L;
    private Long unEmployCount=0L;
    private Integer rate=0;
    private Long userId;
    private String personName;

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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getEmployCount() {
        return employCount;
    }

    public void setEmployCount(Long employCount) {
        this.employCount = employCount;
    }

    public Long getUnEmployCount() {
        return unEmployCount;
    }

    public void setUnEmployCount(Long unEmployCount) {
        this.unEmployCount = unEmployCount;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
