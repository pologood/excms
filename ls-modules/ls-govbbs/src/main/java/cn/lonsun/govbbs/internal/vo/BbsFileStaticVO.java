package cn.lonsun.govbbs.internal.vo;

/**
 * Created by zhangchao on 2017/1/10.
 */
public class BbsFileStaticVO implements java.io.Serializable{

    private static final long serialVersionUID = -1L;

    private Long id;

    //所属版块
    private Long plateId;

    //所属帖子
    private Long postId;
    //审核状态  0 未审核  1 已审核
    //文件名
    private String fileName;

    //文件后缀
    private String suffix;

    //文件大小
    private String fileSizeKb;

    //mongodb关联Id
    private String mongoId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getPlateId() {
        return plateId;
    }

    public void setPlateId(Long plateId) {
        this.plateId = plateId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFileSizeKb() {
        return fileSizeKb;
    }

    public void setFileSizeKb(String fileSizeKb) {
        this.fileSizeKb = fileSizeKb;
    }

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }
}
