package cn.lonsun.delegatemgr.vo;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-8<br/>
 */
public class DelegateQueryVO {
    private Long pageIndex;
    private Integer pageSize;
    private String session;
    private String deleNum;
    private String name;
    private String delegation;

    private String leader;
    private String title;

    private Long siteId;
    private String deleGroup;

    public Long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getDeleNum() {
        return deleNum;
    }

    public void setDeleNum(String deleNum) {
        this.deleNum = deleNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDelegation() {
        return delegation;
    }

    public void setDelegation(String delegation) {
        this.delegation = delegation;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getDeleGroup() {
        return deleGroup;
    }

    public void setDeleGroup(String deleGroup) {
        this.deleGroup = deleGroup;
    }
}
