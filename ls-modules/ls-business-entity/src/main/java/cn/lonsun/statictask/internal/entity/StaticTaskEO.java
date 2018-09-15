package cn.lonsun.statictask.internal.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 生成静态任务实体类 <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-3-3<br/>
 */
@Entity
@Table(name = "cms_static_task")
public class StaticTaskEO extends AMockEntity {

    public static final Long INIT = 1L;// 初始化
    public static final Long DOING = 2L;// 正在进行
    public static final Long COMPLETE = 3L;// 完成
    public static final Long OVER = 4L;// 终止
    public static final Long EXCEPTION = 5L;// 异常

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2964053661110225063L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "column_id")
    private Long columnId;

    @Column(name = "count")
    private Long count;

    @Column(name = "done_count")
    private Long doneCount;

    @Column(name = "fail_count")
    private Long failCount;

    @Column(name = "time")
    private Long time;// 耗时

    @Column(name = "title")
    private String title;

    @Column(name = "status")
    private Long status;

    @Column(name = "type")
    private Long type;

    @Transient
    private String percent;

    @Transient
    private Boolean active;

    @Column(name = "json")
    private String json;// 任务字段json数据

    @Column(name = "site_id")
    private Long siteId;
    @Column(name = "source")
    private Long source;// 来源 1.内容协同 2.信息公开
    @Column(name = "scope")
    private Long scope;// 1.首页 2.栏目页 3.文章页 针对全站生成情况

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(Long doneCount) {
        this.doneCount = doneCount;
    }

    public Long getFailCount() {
        return failCount;
    }

    public void setFailCount(Long failCount) {
        this.failCount = failCount;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getPercent() {
        if (COMPLETE.equals(status)) {
            return "100%";
        }
        if (OVER.equals(status) && null != doneCount && null != count && count > 0L) {
            BigDecimal b1 = new BigDecimal(doneCount * 100);
            BigDecimal b2 = new BigDecimal(count);
            return b1.divide(b2, 2, BigDecimal.ROUND_HALF_DOWN).toString() + "%";
        }
        return percent;
    }

    public void percent(String percent) {
        this.percent = percent;
    }

    public Boolean getActive() {
        return DOING.equals(status);
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public Long getScope() {
        return scope;
    }

    public void setScope(Long scope) {
        this.scope = scope;
    }
}