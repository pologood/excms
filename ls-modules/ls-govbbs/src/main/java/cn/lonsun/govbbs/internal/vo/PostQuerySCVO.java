package cn.lonsun.govbbs.internal.vo;

/**
 * 网站查询参数
 * Created by zhangchao on 2017/1/3.
 */
public class PostQuerySCVO implements java.io.Serializable{


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //版块id
    private Long id;

    private Long organId;

    //信息状态
    private  String infoKey;

    //信息状态
    private Integer isAccept;

    //排序
    private String sortKey;

    private Integer level;

    private Integer isTop;

    private Integer isEssence;

    private Integer isRead;

    private  Integer isReply;

    public PostQuerySCVO() {
        super();
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getInfoKey() {
        return infoKey;
    }

    public void setInfoKey(String infoKey) {
        this.infoKey = infoKey;
    }

    public Integer getIsAccept() {
        return isAccept;
    }

    public void setIsAccept(Integer isAccept) {
        this.isAccept = isAccept;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getIsTop() {
        return isTop;
    }

    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    public Integer getIsEssence() {
        return isEssence;
    }

    public void setIsEssence(Integer isEssence) {
        this.isEssence = isEssence;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Integer getIsReply() {
        return isReply;
    }

    public void setIsReply(Integer isReply) {
        this.isReply = isReply;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
