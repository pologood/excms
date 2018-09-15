package cn.lonsun.special.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 专题素材
 */
@Entity
@Table(name="CMS_SPECIAL_MATERIAL")
public class SpecialMaterialEO extends AMockEntity implements Serializable{

	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SITE_ID")
	private Long siteId;

	/** 名称 */
	@Column(name="NAME")
	private String name;

	/** 宽度 */
	@Column(name="WIDTH")
	private Long width;

	/** 高度 */
	@Column(name="HEIGHT")
	private Long height;

	/** 图片路径 */
	@Column(name="IMG_PATH")
	private String imgPath;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public void setName(String Name){
		this.name = Name;
	}

	public String getName(){
		 return name;
	}

	public Long getWidth() {
		return width;
	}

	public void setWidth(Long width) {
		this.width = width;
	}

	public void setHeight(Long Height){
		this.height = Height;
	}

	public Long getHeight(){
		 return height;
	}

	public void setImgPath(String ImgPath){
		this.imgPath = ImgPath;
	}

	public String getImgPath(){
		 return imgPath;
	}


}
