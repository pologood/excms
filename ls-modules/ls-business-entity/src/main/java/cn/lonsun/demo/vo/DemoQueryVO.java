package cn.lonsun.demo.vo;

/**
 * <br/>
 *
 * @author wangshibao <br/>
 * @version v1.0 <br/>
 * @date 2018-8-2<br/>
 */
public class DemoQueryVO {
    private Long pageIndex;
    private Integer pageSize;
    private String code;
    private String name;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
