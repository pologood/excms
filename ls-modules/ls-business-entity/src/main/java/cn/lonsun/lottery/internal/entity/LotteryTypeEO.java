package cn.lonsun.lottery.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by lonsun on 2017-1-9.
 */
@Entity
@Table(name = "LOTTERY_TYPE")
public class LotteryTypeEO extends AMockEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="TYPE_ID")
     private Long   typeId;
    @Column(name="TYPE_NAME")
     private String  typeName;
    @Column(name="CHANCE")
     private Double   chance;
    @Column(name="SITE_ID")
    private Long  siteId;



    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
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

    public Double getChance() {
        return chance;
    }

    public void setChance(Double chance) {
        this.chance = chance;
    }


}
