package cn.lonsun.site.site.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 
 * @ClassName: WaterMarkConfig
 * @Description: 水印配置
 * @author Hewbing
 * @date 2015年10月14日 下午1:55:57
 *
 */
@Entity
@Table(name="CMS_WATERMARK_CONFIG")
public class WaterMarkConfigEO extends AMockEntity {

	private static final long serialVersionUID = 121212121223l;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Long id;
	
	//对应站点ID
	@Column(name="SITE_ID")
	private Long siteId;
	
	//对应栏目ID（保留字段）
	@Column(name="COLUMN_ID")
	private Long columnId;
	
	//类型 0：文字水印 1：图片水印
	@Column(name="TYPE")
	private Integer type=1;
	
	//文字内容
	@Column(name="WORD_CONTENT")
	private String wordContent;
	
	//文字颜色
	@Column(name="FONT_COLOR")
	private String fontColor;
	
	//文字字体
	@Column(name="FONT_FAMILY")
	private String fontFamily;
	
	//文字大小
	@Column(name="FONT_SIZE")
	private Integer fontSize;
	
	//0：不加粗 1：加粗
	@Column(name="IS_BOLD")
	private Integer isBold=0;
	
	//水印图片路径
	@Column(name="PIC_PATH")
	private String picPath;

	//水印图片路径
	@Column(name="PIC_Name")
	private String picName;

	//水印图片宽度
	@Column(name="WM_WIDTH")
	private Integer width;
	
	//水印图片高度
	@Column(name="WM_HEIGHT")
	private Integer height;
	
	//水印位置 1：左上；2：上中；3：右上；4：左中；5：中；6：右中；7：左下；8：下中；9：右下
	@Column(name="POSITION")
	private Integer position=0;
	
	//旋转角度
	@Column(name="ROTATE")
	private Integer rotate;
	
	//透明度
	@Column(name="TRANSPARENCY")
	private Float transparency;
	
	//启用状态 0：禁用；1：启用
	@Column(name="ENABLE_STATUS")
	private Integer enableStatus=0;

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

	public Long getColumnId() {
		return columnId;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getWordContent() {
		return wordContent;
	}

	public void setWordContent(String wordContent) {
		this.wordContent = wordContent;
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public Integer getIsBold() {
		return isBold;
	}

	public void setIsBold(Integer isBold) {
		this.isBold = isBold;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getRotate() {
		return rotate;
	}

	public void setRotate(Integer rotate) {
		this.rotate = rotate;
	}

	public Float getTransparency() {
		return transparency;
	}

	public void setTransparency(Float transparency) {
		this.transparency = transparency;
	}

	public Integer getEnableStatus() {
		return enableStatus;
	}

	public void setEnableStatus(Integer enableStatus) {
		this.enableStatus = enableStatus;
	}

	public String getPicName() {
		return picName;
	}

	public void setPicName(String picName) {
		this.picName = picName;
	}
}
