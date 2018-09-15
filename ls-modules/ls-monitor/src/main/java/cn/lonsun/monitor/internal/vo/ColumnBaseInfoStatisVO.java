package cn.lonsun.monitor.internal.vo;

/**
 * 综合评分项-栏目不更新结果
 * @author liuk
 * @version 2017-11-28
 */
public class ColumnBaseInfoStatisVO {

    //总扣分
    private Long totalScore;

    //未更新栏目总数
    private Long totalCount;

    //动态要闻扣分
    private Long dtywScore;

    //动态要闻未更新栏目数
    private Long dtywCount;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer dtywLinkRelationStatus;

    //环比上涨下跌率
    private Double dtywLinkRelationRate;

    //通知政策扣分
    private Long tzzcScore;

    //通知政策未更新栏目数
    private Long tzzcCount;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer tzzcLinkRelationStatus;

    //环比上涨下跌率
    private Double tzzcLinkRelationRate;

    //人事规划扣分
    private Long rsghScore;

    //人事规划未更新栏目数
    private Long rsghCount;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer rsghLinkRelationStatus;

    //环比上涨下跌率
    private Double rsghLinkRelationRate;


    //环比上涨下跌率
    private Double linkRelationRate;

    //cpi状态
    private Integer cpiStatus;

    //环比上涨下跌率
    private Double cpi;

    public Long getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getDtywScore() {
        return dtywScore;
    }

    public void setDtywScore(Long dtywScore) {
        this.dtywScore = dtywScore;
    }

    public Long getDtywCount() {
        return dtywCount;
    }

    public void setDtywCount(Long dtywCount) {
        this.dtywCount = dtywCount;
    }

    public Long getTzzcScore() {
        return tzzcScore;
    }

    public void setTzzcScore(Long tzzcScore) {
        this.tzzcScore = tzzcScore;
    }

    public Long getTzzcCount() {
        return tzzcCount;
    }

    public void setTzzcCount(Long tzzcCount) {
        this.tzzcCount = tzzcCount;
    }

    public Long getRsghScore() {
        return rsghScore;
    }

    public void setRsghScore(Long rsghScore) {
        this.rsghScore = rsghScore;
    }

    public Long getRsghCount() {
        return rsghCount;
    }

    public void setRsghCount(Long rsghCount) {
        this.rsghCount = rsghCount;
    }

    public Double getLinkRelationRate() {
        return linkRelationRate;
    }

    public void setLinkRelationRate(Double linkRelationRate) {
        this.linkRelationRate = linkRelationRate;
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

    public Integer getRsghLinkRelationStatus() {
        return rsghLinkRelationStatus;
    }

    public void setRsghLinkRelationStatus(Integer rsghLinkRelationStatus) {
        this.rsghLinkRelationStatus = rsghLinkRelationStatus;
    }

    public Double getRsghLinkRelationRate() {
        return rsghLinkRelationRate;
    }

    public void setRsghLinkRelationRate(Double rsghLinkRelationRate) {
        this.rsghLinkRelationRate = rsghLinkRelationRate;
    }

    public Double getCpi() {
        return cpi;
    }

    public void setCpi(Double cpi) {
        this.cpi = cpi;
    }

    public Integer getCpiStatus() {
        return cpiStatus;
    }

    public void setCpiStatus(Integer cpiStatus) {
        this.cpiStatus = cpiStatus;
    }
}
