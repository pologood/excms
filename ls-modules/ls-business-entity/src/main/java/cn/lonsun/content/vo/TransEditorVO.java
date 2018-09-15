package cn.lonsun.content.vo;

/**
 * Created by zhangchao on 2016/9/7.
 */
public class TransEditorVO implements java.io.Serializable{

    private Long id;

    private Long columnId;

    private Long siteId;

    private String title;

    private String editorText;

    private Long createUserId;

    private Long updateUserId;

    private Long createOrganId;

    public TransEditorVO(Long id, Long columnId, Long siteId, String title, String editorText, Long createUserId, Long createOrganId) {
        this.id = id;
        this.columnId = columnId;
        this.siteId = siteId;
        this.title = title;
        this.editorText = editorText;
        this.createUserId = createUserId;
        this.createOrganId = createOrganId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
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

    public String getEditorText() {
        return editorText;
    }

    public void setEditorText(String editorText) {
        this.editorText = editorText;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getCreateOrganId() {
        return createOrganId;
    }

    public void setCreateOrganId(Long createOrganId) {
        this.createOrganId = createOrganId;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }
}
