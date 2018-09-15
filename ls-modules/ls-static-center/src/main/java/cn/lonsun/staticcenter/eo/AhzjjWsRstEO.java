package cn.lonsun.staticcenter.eo;

/**
 * @author gu.fei
 * @version 2016-12-15 11:48
 */
public class AhzjjWsRstEO {

    private String assignee;

    private String name; //名称

    private String createDate; //创建日期

    private String unit; //名称单位

    private String stats; //状态

    private String dealRst; //审批结果

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStats() {
        return stats;
    }

    public void setStats(String stats) {
        this.stats = stats;
    }

    public String getDealRst() {
        return dealRst;
    }

    public void setDealRst(String dealRst) {
        this.dealRst = dealRst;
    }
}
