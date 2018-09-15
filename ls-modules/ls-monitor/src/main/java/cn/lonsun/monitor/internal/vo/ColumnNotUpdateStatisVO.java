package cn.lonsun.monitor.internal.vo;

/**
 * 单项否决项-栏目不更新结果
 * @author liuk
 * @version 2017-11-28
 */
public class ColumnNotUpdateStatisVO {

    //总未更新栏目数
    private Long totalCount;

    //动态要闻未更新数
    private Long dtywCounts;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer dtywLinkRelationStatus;

    //环比上涨下跌率
    private Double dtywLinkRelationRate;

    //通知政策未更新数
    private Long tzzcCounts;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer tzzcLinkRelationStatus;

    //环比上涨下跌率
    private Double tzzcLinkRelationRate;

    //应更新栏目未更新数
    private Long updateCounts;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer updateLinkRelationStatus;

    //环比上涨下跌率
    private Double updateLinkRelationRate;

    //空白栏目数
    private Long blankCounts;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer blankLinkRelationStatus;

    //环比上涨下跌率
    private Double blankLinkRelationRate;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer linkRelationStatus;

    //环比上涨下跌率
    private Double linkRelationRate;

    //是否合格
    private Integer isOk;

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

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getDtywCounts() {
        return dtywCounts;
    }

    public void setDtywCounts(Long dtywCounts) {
        this.dtywCounts = dtywCounts;
    }

    public Long getTzzcCounts() {
        return tzzcCounts;
    }

    public void setTzzcCounts(Long tzzcCounts) {
        this.tzzcCounts = tzzcCounts;
    }

    public Long getUpdateCounts() {
        return updateCounts;
    }

    public void setUpdateCounts(Long updateCounts) {
        this.updateCounts = updateCounts;
    }

    public Long getBlankCounts() {
        return blankCounts;
    }

    public void setBlankCounts(Long blankCounts) {
        this.blankCounts = blankCounts;
    }

    public Integer getDtywLinkRelationStatus() {
        return dtywLinkRelationStatus;
    }

    public void setDtywLinkRelationStatus(Integer dtywLinkRelationStatus) {
        this.dtywLinkRelationStatus = dtywLinkRelationStatus;
    }

    public Double getDtywLinkRelationRate() {
        return dtywLinkRelationRate;
    }

    public void setDtywLinkRelationRate(Double dtywLinkRelationRate) {
        this.dtywLinkRelationRate = dtywLinkRelationRate;
    }

    public Integer getTzzcLinkRelationStatus() {
        return tzzcLinkRelationStatus;
    }

    public void setTzzcLinkRelationStatus(Integer tzzcLinkRelationStatus) {
        this.tzzcLinkRelationStatus = tzzcLinkRelationStatus;
    }

    public Double getTzzcLinkRelationRate() {
        return tzzcLinkRelationRate;
    }

    public void setTzzcLinkRelationRate(Double tzzcLinkRelationRate) {
        this.tzzcLinkRelationRate = tzzcLinkRelationRate;
    }

    public Integer getUpdateLinkRelationStatus() {
        return updateLinkRelationStatus;
    }

    public void setUpdateLinkRelationStatus(Integer updateLinkRelationStatus) {
        this.updateLinkRelationStatus = updateLinkRelationStatus;
    }

    public Double getUpdateLinkRelationRate() {
        return updateLinkRelationRate;
    }

    public void setUpdateLinkRelationRate(Double updateLinkRelationRate) {
        this.updateLinkRelationRate = updateLinkRelationRate;
    }

    public Integer getBlankLinkRelationStatus() {
        return blankLinkRelationStatus;
    }

    public void setBlankLinkRelationStatus(Integer blankLinkRelationStatus) {
        this.blankLinkRelationStatus = blankLinkRelationStatus;
    }

    public Double getBlankLinkRelationRate() {
        return blankLinkRelationRate;
    }

    public void setBlankLinkRelationRate(Double blankLinkRelationRate) {
        this.blankLinkRelationRate = blankLinkRelationRate;
    }

    public Integer getLinkRelationStatus() {
        return linkRelationStatus;
    }

    public void setLinkRelationStatus(Integer linkRelationStatus) {
        this.linkRelationStatus = linkRelationStatus;
    }
}
