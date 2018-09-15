package cn.lonsun.lottery.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * Created by lonsun on 2017-1-9.
 */

@Entity
@Table(name = "LOTTERY_REWARD")
public class LotteryRewardEO extends AMockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="REWARD_ID")
    private Long   rewardId;
    @Column(name="REWARD_NAME")
    private String  rewardName;
    @Column(name="REWARD_IMAGE")
    private String  rewardImage;
    @Column(name="REWARD_PRICE")
    private Double   rewardPrice;
    @Column(name="COUNT")
    private Long count;
    @Column(name="TYPE_ID")
    private Long   typeId;
    @Column(name="TYPE_NAME")
    private String  typeName;
    @Column(name="SITE_ID")
    private Long  siteId;

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

    public String getRewardImage() {
        return rewardImage;
    }

    public void setRewardImage(String rewardImage) {
        this.rewardImage = rewardImage;
    }

    public Double getRewardPrice() {
        return rewardPrice;
    }

    public void setRewardPrice(Double rewardPrice) {
        this.rewardPrice = rewardPrice;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
