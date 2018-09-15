package cn.lonsun.monitor.config.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
/**
 * Created by lonsun on 2017-10-24.
 */
@Entity
@Table(name="MONITORED_ALARM_CONFIG")
public class MonitoredAlarmConfigEO extends AMockEntity {

    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ALARM_ID")
    private Long  alarmId ;
    @Column(name = "IS_VOTE")
    private Integer isVote=0;
    @Column(name = "IS_OUT")
    private Integer isOut=0;
    @Column(name = "RECEIVE_USER_ID")
    private String receiveUserId;
    @Column(name = "NAME")
    private String name;
    @Column(name = "SITE_ID")
    private Long siteId;
    @Column(name = "ALARM_TYPE_MESSAGE")
    private Integer alarmTypeMessage=0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Long alarmId) {
        this.alarmId = alarmId;
    }

    public Integer getIsOut() {
        return isOut;
    }

    public void setIsOut(Integer isOut) {
        this.isOut = isOut;
    }

    public Integer getIsVote() {
        return isVote;
    }

    public void setIsVote(Integer isVote) {
        this.isVote = isVote;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Integer getAlarmTypeMessage() {
        return alarmTypeMessage;
    }

    public void setAlarmTypeMessage(Integer alarmTypeMessage) {
        this.alarmTypeMessage = alarmTypeMessage;
    }

    public String getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(String receiveUserId) {
        this.receiveUserId = receiveUserId;
    }
}


