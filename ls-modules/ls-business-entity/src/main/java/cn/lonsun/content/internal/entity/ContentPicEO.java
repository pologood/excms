package cn.lonsun.content.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 
 * @ClassName: ContentPicEO
 * @Description: 图片新闻的图片附件
 * @author Hewbing
 * @date 2015年9月17日 下午5:43:16
 *
 */
@Entity
@Table(name = "CMS_CONTENT_PIC")
public class ContentPicEO extends AMockEntity {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -5481855027933233955L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PIC_ID")
    private Long picId;
    //图片标题
    @Column(name="PIC_TITLE")
    private String picTitle;
    //图片作者
    @Column(name="PIC_AUTHOR")
    private String picAuthor;
    //描述
    @Column(name="DESCRIPTION")
    private String description;
    //排序
    @Column(name="SORT_NUM")
    private Integer sortNum;
    //关联文章
    @Column(name="CONTENT_ID")
    private Long contentId;
    //图片地址
    @Column(name="PATH")
    private String path;
    //缩略图
    @Column(name="THUMB_PATH")
    private String thumbPath;
    //站点ID
    @Column(name="SITE_ID")
    private Long siteId;
    //栏目ID
    @Column(name="COLUMN_ID")
    private Long columnId;

	//轮播图排序
	@Column(name="NUM")
	private  Long num;

	public Long getPicId() {
		return picId;
	}

	public void setPicId(Long picId) {
		this.picId = picId;
	}

	public String getPicTitle() {
		return picTitle;
	}

	public void setPicTitle(String picTitle) {
		this.picTitle = picTitle;
	}

	public String getPicAuthor() {
		return picAuthor;
	}

	public void setPicAuthor(String picAuthor) {
		this.picAuthor = picAuthor;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getThumbPath() {
		return thumbPath;
	}

	public void setThumbPath(String thumbPath) {
		this.thumbPath = thumbPath;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long getColumnId() {
		return columnId;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}
}
