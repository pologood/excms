package cn.lonsun.source.dataexport.vo;


import cn.lonsun.engine.vo.ExportQueryVO;
import cn.lonsun.internal.metadata.ImportType;

/**
 * 信息公开老数据查询参数类
 * @author zhongjun
 */
public class PublicContentQueryVO extends ExportQueryVO {

    public PublicContentQueryVO(ImportType importType, String oldOrganId, String oldCatId, Long newCatId, Long newOrganId, String... oldIds) {
        this.oldOrganId = oldOrganId;
        this.oldCatId = oldCatId;
        this.newCatId = newCatId;
        this.newOrganId = newOrganId;
        super.setImportType(importType);
        super.setOldContentIds(oldIds);
    }

    /**
     * 老库中的单位id
     */
    private String oldOrganId;

    /**
     * 老库中的目录id
     */
    private String oldCatId;

    private Long newCatId;

    private Long newOrganId;

    public String getOldOrganId() {
        return oldOrganId;
    }

    public void setOldOrganId(String oldOrganId) {
        this.oldOrganId = oldOrganId;
    }

    public String getOldCatId() {
        return oldCatId;
    }

    public void setOldCatId(String oldCatId) {
        this.oldCatId = oldCatId;
    }

    public Long getNewCatId() {
        return newCatId;
    }

    public void setNewCatId(Long newCatId) {
        this.newCatId = newCatId;
    }

    public Long getNewOrganId() {
        return newOrganId;
    }

    public void setNewOrganId(Long newOrganId) {
        this.newOrganId = newOrganId;
    }
}
