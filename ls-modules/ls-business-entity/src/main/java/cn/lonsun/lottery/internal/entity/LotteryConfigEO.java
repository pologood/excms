package cn.lonsun.lottery.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * Created by lonsun on 2017-1-18.
 */
@Entity
@Table(name="LOTTERY_CONFIG")
public class LotteryConfigEO extends AMockEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="CONFIG_ID")
    private Long configId;

    @Column(name="CONFIG_SUM")
    private Double configSum=0d;

    @Column(name="DAY_SUM")
    private Double daySum=0d;

    @Column(name="LEFT_SUM")
    private Double leftDum=0d;

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public Double getConfigSum() {
        return configSum;
    }

    public void setConfigSum(Double configSum) {
        this.configSum = configSum;
    }

    public Double getDaySum() {
        return daySum;
    }

    public void setDaySum(Double daySum) {
        this.daySum = daySum;
    }

    public Double getLeftDum() {
        return leftDum;
    }

    public void setLeftDum(Double leftDum) {
        this.leftDum = leftDum;
    }
}
