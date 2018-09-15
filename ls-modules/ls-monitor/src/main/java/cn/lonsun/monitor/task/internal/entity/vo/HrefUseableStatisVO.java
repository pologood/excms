package cn.lonsun.monitor.task.internal.entity.vo;

/**
 * @author gu.fei
 * @version 2017-10-24 10:01
 */
public class HrefUseableStatisVO {
    //总数量
    private Long total;

    //首页数量
    private Long indexCount = 0L;

    //其他数量
    private Long otherCount = 0L;

    //链接访问扣分
    private Double linkScore;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer indexLinkRelationStatus;

    //环比上涨下跌率
    private Double indexLinkRelationRate;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer otherLinkRelationStatus;

    //环比上涨下跌率
    private Double otherLinkRelationRate;

    //站点访问成功数
    private Long siteVisitSuccess;

    //站点访问失败数
    private Long siteVisitFail;

    //首页不可访问占比
    private Double siteVisitFailRate;

    //站点访问扣分
    private Double siteVisitScore;

    //总扣分
    private Double totalScore;

    //cpi状态
    private Integer cpiStatus;

    //综合指数
    private Double cpi;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getIndexCount() {
        return indexCount;
    }

    public void setIndexCount(Long indexCount) {
        this.indexCount = indexCount;
    }

    public Long getOtherCount() {
        return otherCount;
    }

    public void setOtherCount(Long otherCount) {
        this.otherCount = otherCount;
    }

    public Double getLinkScore() {
        return linkScore;
    }

    public void setLinkScore(Double linkScore) {
        this.linkScore = linkScore;
    }

    public Integer getIndexLinkRelationStatus() {
        return indexLinkRelationStatus;
    }

    public void setIndexLinkRelationStatus(Integer indexLinkRelationStatus) {
        this.indexLinkRelationStatus = indexLinkRelationStatus;
    }

    public Double getIndexLinkRelationRate() {
        return indexLinkRelationRate;
    }

    public void setIndexLinkRelationRate(Double indexLinkRelationRate) {
        this.indexLinkRelationRate = indexLinkRelationRate;
    }

    public Integer getOtherLinkRelationStatus() {
        return otherLinkRelationStatus;
    }

    public void setOtherLinkRelationStatus(Integer otherLinkRelationStatus) {
        this.otherLinkRelationStatus = otherLinkRelationStatus;
    }

    public Double getOtherLinkRelationRate() {
        return otherLinkRelationRate;
    }

    public void setOtherLinkRelationRate(Double otherLinkRelationRate) {
        this.otherLinkRelationRate = otherLinkRelationRate;
    }

    public Long getSiteVisitSuccess() {
        return siteVisitSuccess;
    }

    public void setSiteVisitSuccess(Long siteVisitSuccess) {
        this.siteVisitSuccess = siteVisitSuccess;
    }

    public Long getSiteVisitFail() {
        return siteVisitFail;
    }

    public void setSiteVisitFail(Long siteVisitFail) {
        this.siteVisitFail = siteVisitFail;
    }

    public Double getSiteVisitScore() {
        return siteVisitScore;
    }

    public void setSiteVisitScore(Double siteVisitScore) {
        this.siteVisitScore = siteVisitScore;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Double getCpi() {
        return cpi;
    }

    public void setCpi(Double cpi) {
        this.cpi = cpi;
    }

    public Double getSiteVisitFailRate() {
        return siteVisitFailRate;
    }

    public void setSiteVisitFailRate(Double siteVisitFailRate) {
        this.siteVisitFailRate = siteVisitFailRate;
    }

    public Integer getCpiStatus() {
        return cpiStatus;
    }

    public void setCpiStatus(Integer cpiStatus) {
        this.cpiStatus = cpiStatus;
    }
}
