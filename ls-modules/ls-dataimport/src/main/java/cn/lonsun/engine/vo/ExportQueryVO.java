package cn.lonsun.engine.vo;

import cn.lonsun.internal.metadata.ImportType;;

import java.util.Date;
import java.util.Map;

/**
 * 结果查询基础参数类
 * @author zhongjun
 */
public class ExportQueryVO {

    private Long siteId;

    private Date beginTime;

    private Integer pageSize = 10000;

    private Long pageIndex;

    /**
     * 默认全量导入，会清空库中已有数据
     */
    private ImportType importType = ImportType.all;

    /**
     * 如果该字段不为空，则会根据字段的sql直接查询数据库，不会调用相应模块的导出实现类的方法
     */
    private String sqlConfig;
    /**
     * 导入指定的数据时传入该字段
     */
    private String[] oldContentIds;

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public ImportType getImportType() {
        return importType;
    }

    public void setImportType(ImportType importType) {
        this.importType = importType;
    }

    public String getSqlConfig() {
        return sqlConfig;
    }

    public void setSqlConfig(String sqlConfig) {
        this.sqlConfig = sqlConfig;
    }

    public String[] getOldContentIds() {
        return oldContentIds;
    }

    public void setOldContentIds(String[] oldContentIds) {
        this.oldContentIds = oldContentIds;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
