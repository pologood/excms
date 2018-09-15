package cn.lonsun.content.filedownload.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 文件下载<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-21<br/>
 */
@Entity
@Table(name="cms_file_download")
public class FileDownloadEO extends AMockEntity {
    private static final long serialVersionUID = -1300742296285581640L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;


    @Column(name = "add_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date addDate;

    @Column(name="count")
    private Long count=0L;

    @Column(name="filePath")
    private String filePath;

    @Column(name="fileName")
    private String fileName;

    @Column(name="contentId")
    private Long contentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
}
