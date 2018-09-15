package cn.lonsun.common.upload.internal.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.lonsun.core.base.entity.ABaseEntity;

@Entity
@Table(name="SYSTEM_ATTACHMENT")
public class AttachmentEO extends ABaseEntity implements Serializable{

	
	/**
	 * serialVersionUID:TODO.
	 */
	private static final long serialVersionUID = 179226142996763383L;

	/**上传附件文件状态*/
	public enum FileStatus{
	    /** 未使用状态 */
	    Unused,
	    /** 已锁定状态 */
	    Locked
	}

	/** (附件的主键) */
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ATTACHMENT_ID")
	private Long attachmentId;

	/** (附件的唯一标识) */
	@Column(name="ATTACHMENT_UUID")
	private String attachmentUuid;

	/** (附件名称) */
	@Column(name="FILE_NAME")
	private String fileName;

	/** (附件的路径) */
	@Column(name="FILE_PATH")
	private String filePath;

	/** (文件类型) */
	@Column(name="FILE_TYLE")
	private String fileTyle;

	/** (文件大小) */
	@Column(name="FILE_SIZE")
	private Long fileSize;
	
	/** (文件大小显示文本) */
	@Transient
	private String sizeText;
	
	/** (文件状态) */
	@Column(name="FILE_STATUS")
	private String fileStatus = FileStatus.Unused.toString();

	public String getFileStatus() {
        return fileStatus;
    }

	/** (文件储存位置 0:文件服务器(默认) 1:应用服务器) */
	@Column(name="STORAGE_LOCATION")
	private Integer storageLocation = 0;

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

	public void setAttachmentId(Long AttachmentId){
		this.attachmentId = AttachmentId;
	}

	public Long getAttachmentId(){
		 return attachmentId;
	}

	public void setAttachmentUuid(String AttachmentUuid){
		this.attachmentUuid = AttachmentUuid;
	}

	public String getAttachmentUuid(){
		 return attachmentUuid;
	}

	public void setFileName(String FileName){
		this.fileName = FileName;
	}

	public String getFileName(){
		 return fileName;
	}

	public void setFilePath(String FilePath){
		this.filePath = FilePath;
	}

	public String getFilePath(){
		 return filePath;
	}

	public void setFileTyle(String FileTyle){
		this.fileTyle = FileTyle;
	}

	public String getFileTyle(){
		 return fileTyle;
	}

	public void setFileSize(Long FileSize){
		this.fileSize = FileSize;
	}

	public Long getFileSize(){
		 return fileSize;
	}

	public String getSizeText() {
		return sizeText;
	}

	public void setSizeText(String sizeText) {
		this.sizeText = sizeText;
	}

	public Integer getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(Integer storageLocation) {
		this.storageLocation = storageLocation;
	}
}
