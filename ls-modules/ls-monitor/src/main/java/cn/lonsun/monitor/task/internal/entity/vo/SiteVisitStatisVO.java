package cn.lonsun.monitor.task.internal.entity.vo;

/**
 * @author gu.fei
 * @version 2017-10-24 10:01
 */
public class SiteVisitStatisVO {

    //总访问次数
    private Long total;

    //成功次数
    private Long success = 0L;

    //失败次数
    private Long fail = 0L;

    //成功率
    private Double successRate;

    //失败率
    private Double failRate;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer linkRelationStatus;

    //环比上涨下跌率
    private Double linkRelationRate;

    //是否合格 0:不合格 1:合格
    private Integer isOk = 0;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSuccess() {
        return success;
    }

    public void setSuccess(Long success) {
        this.success = success;
    }

    public Long getFail() {
        return fail;
    }

    public void setFail(Long fail) {
        this.fail = fail;
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    public Double getFailRate() {
        return failRate;
    }

    public void setFailRate(Double failRate) {
        this.failRate = failRate;
    }

    public Integer getLinkRelationStatus() {
        return linkRelationStatus;
    }

    public void setLinkRelationStatus(Integer linkRelationStatus) {
        this.linkRelationStatus = linkRelationStatus;
    }

    public Double getLinkRelationRate() {
        return linkRelationRate;
    }

    public void setLinkRelationRate(Double linkRelationRate) {
        this.linkRelationRate = linkRelationRate;
    }

    public Integer getIsOk() {
        return isOk;
    }

    public void setIsOk(Integer isOk) {
        this.isOk = isOk;
    }
}
