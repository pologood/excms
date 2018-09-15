package cn.lonsun.monitor.task.internal.entity.vo;

/**
 * @author gu.fei
 * @version 2017-12-07 8:54
 */
public class SiteVisitDateStatisVO {

    //成功数
    private Long success;

    //总访问数
    private Long total;

    //监测日期
    private String mdate;

    public Long getSuccess() {
        return success;
    }

    public void setSuccess(Long success) {
        this.success = success;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }
}
