package cn.lonsun.common.vo;

/**
 * 用于分页查询参数传递的基本VO对象
 * @author Dzl
 * @date 2014年9月26日
 * @version 1.0
 */
public class PageQueryVO {
     /**
     * 页码 从0开始
     */
    private Long pageIndex = 0L;
    /**
     * 每页记录数 默认为15
     */
    private Integer pageSize = 15;
    /**
     * 排序字段名称
     */
    private String sortField;
    /**
     * 排序形式 asc  desc
     */
    private String sortOrder="desc";
    
    /**
     * 显示正常数据, Normal 正常数据  Removed 逻辑删除
     */
    private String recordStatus="Normal";


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

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }
    
    
}
