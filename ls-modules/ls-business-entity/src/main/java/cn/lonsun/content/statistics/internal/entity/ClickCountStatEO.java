package cn.lonsun.content.statistics.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * 点击数统计
 * Created by zhushouyong on 2016-8-12.
 */

@Entity
@Table(name="CMS_CLICK_COUNT_STAT")
public class ClickCountStatEO extends ABaseEntity {


    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="STAT_ID")
    private Long statId;

    /**
     * 站点ID
     */
    @Column(name="SITE_ID")
    private Long siteId;

    /**
     * 点击内容所属栏目ID
     */
    @Column(name="COLUMN_ID")
    private Long columnId;

    /**
     * 点击内容ID
     */
    @Column(name="CONTENT_ID")
    private Long contentId;

    /**
     * 点击数
     */
    @Column(name="COUNT")
    private Long count;

    /**
     * 统计当天日期
     */
    @Column(name="STAT_DAY")
    private Date statDay;


    public Date getStatDay() {
        return statDay;
    }

    public void setStatDay(Date statDay) {
        this.statDay = statDay;
    }

    public Long getStatId() {
        return statId;
    }

    public void setStatId(Long statId) {
        this.statId = statId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
