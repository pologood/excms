package cn.lonsun.govbbs.internal.vo;

/**
 * Created by zhangchao on 2017/1/12.
 */
public class PermissionVO implements  java.io.Serializable{

    private static final long serialVersionUID = 1L;

    private Integer isPost = 0;//是否允许发帖

    private Integer isReply = 0;//是否允许回帖

    private Integer fastPost = 0;//是否显示快递发帖

    private Integer fastReply = 0;//是否显示快递回帖

    private Integer download = 0;//是否允许下载

    private Integer addFile = 0;//是否允许上次附件

    private Integer isShowUnits = 0;//是否显示办理单位

    public Integer getIsPost() {
        return isPost;
    }

    public void setIsPost(Integer isPost) {
        this.isPost = isPost;
    }

    public Integer getFastPost() {
        return fastPost;
    }

    public void setFastPost(Integer fastPost) {
        this.fastPost = fastPost;
    }

    public Integer getAddFile() {
        return addFile;
    }

    public void setAddFile(Integer addFile) {
        this.addFile = addFile;
    }

    public Integer getDownload() {
        return download;
    }

    public void setDownload(Integer download) {
        this.download = download;
    }

    public Integer getIsReply() {
        return isReply;
    }

    public void setIsReply(Integer isReply) {
        this.isReply = isReply;
    }

    public Integer getFastReply() {
        return fastReply;
    }

    public void setFastReply(Integer fastReply) {
        this.fastReply = fastReply;
    }

    public Integer getIsShowUnits() {
        return isShowUnits;
    }

    public void setIsShowUnits(Integer isShowUnits) {
        this.isShowUnits = isShowUnits;
    }
}
