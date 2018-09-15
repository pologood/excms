package cn.lonsun.monitor.internal.vo;

/**
 * @author liuk
 * @version 2017-11-23 11:45
 */
public class SiteInfoStatisVO {



    //站点id
    private Long siteId;

    //站点名称
    private String siteName;

    //监测总栏目数
    private Long totalCounts;

    //不达标栏目数
    private Long failCount;

    //达标栏目数
    private Long passCount;

    private String url;

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Long getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(Long totalCounts) {
        this.totalCounts = totalCounts;
    }

    public Long getFailCount() {
        return failCount;
    }

    public void setFailCount(Long failCount) {
        this.failCount = failCount;
    }

    public Long getPassCount() {
        return passCount;
    }

    public void setPassCount(Long passCount) {
        this.passCount = passCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
