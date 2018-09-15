package cn.lonsun.statistics;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-1-29<br/>
 */

public class ChartVO {
    private String chartsTime;
    private Long count;

    public String getChartsTime() {
        return chartsTime;
    }

    public void setChartsTime(String chartsTime) {
        this.chartsTime = chartsTime;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
    }
}
