package cn.lonsun.monitor.internal.vo;

/**
 * 综合评分项-互动回应情况结果
 * @author liuk
 * @version 2017-11-28
 */
public class InteractInfoStatisVO {

    //总扣分
    private Long totalScore;

    //政务咨询扣分
    private Long zwzxScore;

    //扣分原因 0-当前栏目不存在，1-一年内没数据,2-开展活动少于规定次数 3-正常。不扣分
    private Integer zwzxStatus;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer zwzxLinkRelationStatus;

    //政务咨询环比上涨下跌率
    private Double zwzxLinkRelationRate;

    //调查征集扣分
    private Long dczjScore;

    //扣分原因 0-当前栏目不存在，1-一年内没数据,2-开展活动少于规定次数 3-正常。不扣分
    private Integer dczjStatus;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer dczjLinkRelationStatus;

    //调查征集环比上涨下跌率
    private Double dczjLinkRelationRate;

    //互动访谈扣分
    private Long hdftScore;

    //扣分原因 0-当前栏目不存在，1-一年内没数据,2-开展活动少于规定次数 3-正常。不扣分
    private Integer hdftStatus;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer hdftLinkRelationStatus;

    //互动访谈环比上涨下跌率
    private Double hdftLinkRelationRate;

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

    public Long getZwzxScore() {
        return zwzxScore;
    }

    public void setZwzxScore(Long zwzxScore) {
        this.zwzxScore = zwzxScore;
    }

    public Double getZwzxLinkRelationRate() {
        return zwzxLinkRelationRate;
    }

    public void setZwzxLinkRelationRate(Double zwzxLinkRelationRate) {
        this.zwzxLinkRelationRate = zwzxLinkRelationRate;
    }

    public Long getDczjScore() {
        return dczjScore;
    }

    public void setDczjScore(Long dczjScore) {
        this.dczjScore = dczjScore;
    }

    public Double getDczjLinkRelationRate() {
        return dczjLinkRelationRate;
    }

    public void setDczjLinkRelationRate(Double dczjLinkRelationRate) {
        this.dczjLinkRelationRate = dczjLinkRelationRate;
    }

    public Long getHdftScore() {
        return hdftScore;
    }

    public void setHdftScore(Long hdftScore) {
        this.hdftScore = hdftScore;
    }

    public Double getHdftLinkRelationRate() {
        return hdftLinkRelationRate;
    }

    public void setHdftLinkRelationRate(Double hdftLinkRelationRate) {
        this.hdftLinkRelationRate = hdftLinkRelationRate;
    }

    public Integer getZwzxStatus() {
        return zwzxStatus;
    }

    public void setZwzxStatus(Integer zwzxStatus) {
        this.zwzxStatus = zwzxStatus;
    }

    public Integer getDczjStatus() {
        return dczjStatus;
    }

    public void setDczjStatus(Integer dczjStatus) {
        this.dczjStatus = dczjStatus;
    }

    public Integer getHdftStatus() {
        return hdftStatus;
    }

    public void setHdftStatus(Integer hdftStatus) {
        this.hdftStatus = hdftStatus;
    }

    public Integer getZwzxLinkRelationStatus() {
        return zwzxLinkRelationStatus;
    }

    public void setZwzxLinkRelationStatus(Integer zwzxLinkRelationStatus) {
        this.zwzxLinkRelationStatus = zwzxLinkRelationStatus;
    }

    public Integer getDczjLinkRelationStatus() {
        return dczjLinkRelationStatus;
    }

    public void setDczjLinkRelationStatus(Integer dczjLinkRelationStatus) {
        this.dczjLinkRelationStatus = dczjLinkRelationStatus;
    }

    public Integer getHdftLinkRelationStatus() {
        return hdftLinkRelationStatus;
    }

    public void setHdftLinkRelationStatus(Integer hdftLinkRelationStatus) {
        this.hdftLinkRelationStatus = hdftLinkRelationStatus;
    }

    public Integer getCpiStatus() {
        return cpiStatus;
    }

    public void setCpiStatus(Integer cpiStatus) {
        this.cpiStatus = cpiStatus;
    }

    public Double getCpi() {
        return cpi;
    }

    public void setCpi(Double cpi) {
        this.cpi = cpi;
    }
}
