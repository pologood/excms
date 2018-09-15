package cn.lonsun.source.dataexport.vo;

import cn.lonsun.engine.vo.ExportQueryVO;

/**
 * 综合信息导入数据查询条件
 * @author zhongjun
 */
public class ContentQueryVO extends ExportQueryVO {

    private String oldColumnId;
    /**新系统栏目id*/
    private Long columnId;
    /**新老栏目对应关系id*/
    private Long columnRelationId;

    public String getOldColumnId() {
        return oldColumnId;
    }

    public void setOldColumnId(String oldColumnId) {
        this.oldColumnId = oldColumnId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getColumnRelationId() {
        return columnRelationId;
    }

    public void setColumnRelationId(Long columnRelationId) {
        this.columnRelationId = columnRelationId;
    }
}
