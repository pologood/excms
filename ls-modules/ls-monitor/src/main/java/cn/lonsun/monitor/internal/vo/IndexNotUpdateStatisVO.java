package cn.lonsun.monitor.internal.vo;

/**
 * 单项否决项-网站不更新结果
 * @author liuk
 * @version 2017-11-28
 */
public class IndexNotUpdateStatisVO {

    //首页未更新栏目数
    private Long count;

    //环比状态 0:-,1:/,2:向上箭头,3:向下箭头,4:有环比值（取linkRelationRate值）
    private Integer linkRelationStatus;

    //环比上涨下跌率
    private Double linkRelationRate;

    //是否合格
    private Integer isOk;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
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

    public Integer getLinkRelationStatus() {
        return linkRelationStatus;
    }

    public void setLinkRelationStatus(Integer linkRelationStatus) {
        this.linkRelationStatus = linkRelationStatus;
    }
}
