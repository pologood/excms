package cn.lonsun.msg.submit.hn.entity.vo;

/**
 * @author gu.fei
 * @version 2017-02-10 15:28
 */
public class CmsMsgToColumnHnVO{

    //栏目ID
    private Long id;

    //站点ID
    private Long siteId;

    //站点名称
    private String siteName;

    //栏目名称
    private String name;

    //栏目类型
    private String type;

    //信息公开单位ID
    private Long organId;

    //信息公开编码
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
