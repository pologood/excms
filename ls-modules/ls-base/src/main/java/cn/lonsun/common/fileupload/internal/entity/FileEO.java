package cn.lonsun.common.fileupload.internal.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.lonsun.core.base.entity.ABaseEntity;
@Entity
@Table(name="SYSTEM_FILE")
public class FileEO extends ABaseEntity implements Serializable{


    private static final long serialVersionUID = -931391780842125158L;

    /**
     * 文件状态
     */
    public enum Status{
      UnUse(0),//未使用
      Used(1),//已使用
      Delete(2),//已删除
      Replace(3);//被覆盖

      private  Integer value;

      private Status(Integer value){
          this.value = value;
      }
      public Integer getValue(){
          return this.value;
      }
    }

    //主键
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="FILE_ID")
    private Long fileId;

    //文件所属实体类型
    @Column(name="CASE_TYPE")
    private String caseType;

    //文件所属实体主键
    @Column(name="CASE_ID")
    private Long caseId;

    //文件名称
    @Column(name="FILE_NAME")
    private String fileName;

    //文件后缀名
    @Column(name="SUFFIX")
    private String suffix;

    //文件单位
    @Column(name="UNIT")
    private String unit;

    //文件大小
    @Column(name="FILE_SIZE")
    private Double fileSize;

    //保存路径
    @Column(name="URI")
    private String uri;

    //排序号
    @Column(name="FILE_INDEX")
    private Integer fileIndex;

    // 版本号
    @Column(name="VERSION")
    private Integer version;

    // 文件状态(0:未启用 1:已启用 2:逻辑删除 3:被覆盖)
    @Column(name="STATUS")
    private Integer status;
    //状态更新时间
    @Column(name="STATUS_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date statusDate;

    //创建人姓名
    @Column(name="CREATE_PERSON_NAME")
    private String createPersonName;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
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

    public Double getFileSize() {
        return fileSize;
    }

    public void setFileSize(Double fileSize) {
        this.fileSize = fileSize;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(Integer fileIndex) {
        this.fileIndex = fileIndex;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public String getUnit() {
		return unit;
	}

    public String getCreatePersonName() {
        return createPersonName;
    }

    public void setCreatePersonName(String createPersonName) {
        this.createPersonName = createPersonName;
    }
}
