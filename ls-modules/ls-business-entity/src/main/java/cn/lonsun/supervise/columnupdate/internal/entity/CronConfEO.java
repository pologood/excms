package cn.lonsun.supervise.columnupdate.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_SUPERVISE_CRON_CONF")
public class CronConfEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum TimeMode {
        day,
        auto
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //主键ID

    @Column(name = "TIME_MODE")
    private String timeMode; //定时方式 day、week、month

    @Column(name = "CRON_EXPRESS")
    private String cronExpress; //cron表达式

    @Column(name = "START_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date startDate; //开始时间

    @Column(name = "SPACE_OF_DAY")
    private Integer spaceOfDay; //间隔天数

    @Transient
    private String cronDesc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTimeMode() {
        return timeMode;
    }

    public void setTimeMode(String timeMode) {
        this.timeMode = timeMode;
    }

    public String getCronExpress() {
        return cronExpress;
    }

    public void setCronExpress(String cronExpress) {
        this.cronExpress = cronExpress;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getSpaceOfDay() {
        return spaceOfDay;
    }

    public void setSpaceOfDay(Integer spaceOfDay) {
        this.spaceOfDay = spaceOfDay;
    }

    public String getCronDesc() {
        return cronDesc;
    }

    public void setCronDesc(String cronDesc) {
        this.cronDesc = cronDesc;
    }
}