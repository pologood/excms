package cn.lonsun.content.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-21<br/>
 */
@Entity
@Table(name = "cms_video_news")
public class VideoNewsEO extends AMockEntity {
    private static final long serialVersionUID = -1300742296285581640L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "video_id")
    private Long videoId;

    //关联文章
    @Column(name = "CONTENT_ID")
    private Long contentId;

    //栏目ID
    @Column(name = "COLUMN_ID")
    private Long columnId;

    //站点ID
    @Column(name = "SITE_ID")
    private Long siteId;

    //视频地址
    @Column(name = "video_path")
    private String videoPath;

    //视频地址
    @Column(name = "video_name")
    private String videoName;

    //缩略图地址
    @Column(name = "image_name")
    private String imageName;

    @Column(name = "status")
    private Integer status = 0;

    @Column(name = "file_type")
    private String fileType;


    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
