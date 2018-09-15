package cn.lonsun.lottery.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * Created by lonsun on 2017-1-11.
 */
@Entity
@Table(name = "LOTTERY_REWARD_RECORD")
public class LotteryRewardRecordEO extends AMockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ID")
    private Long   id;
    @Column(name="NAME")
    private String   name;
    @Column(name="PHONE")
    private String   phone;
    @Column(name="REWARD_ID")
    private Long   rewardId;
    @Column(name="REWARD_NAME")
    private String  rewardName;
    @Column(name="REWARD_PRICE")
    private Double   rewardPrice;
    @Column(name="SITE_ID")
    private Long  siteId;
    @Column(name="STATUS")
    private Integer status=0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getRewardId() {
        return rewardId;
    }

    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public Double getRewardPrice() {
        return rewardPrice;
    }

    public void setRewardPrice(Double rewardPrice) {
        this.rewardPrice = rewardPrice;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
