
package cn.lonsun.process.vo;

/**
 * 文件编辑结果VO
 *@date 2014-12-11 9:54  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
public class FileEditVO {

    public static final String newFile  = "new";
    public static final String deleteFile = "delete";

    //文件ID
    private Long fileId;

    //文件编辑状态
    private String status;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
