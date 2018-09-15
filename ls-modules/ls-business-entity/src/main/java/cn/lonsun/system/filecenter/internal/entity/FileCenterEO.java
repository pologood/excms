package cn.lonsun.system.filecenter.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 
 * @ClassName: FileCenterEO
 * @Description: 文件管理
 * @author Hewbing
 * @date 2015年11月18日 上午8:53:13
 *
 */
@Entity
@Table(name="cms_file_center")
public class FileCenterEO extends AMockEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -121L;

	//文件分类，可自行添加，请在数据字典内添加
    public enum Type{
    	NotDefined,//未定义
    	EditorUpload,//编辑器上传
    	Image,//图片类型
    	Txt,//文本类型
		WorkOnLine,
		Video//视频类型
    }
	
    //上传编码，每个调用上传方法都定义一个唯一编码
    public enum Code{
    	Default,//默认上传
    	EditorAttach,//编辑器上传标识
    	ThumbUpload,  //缩略图上传的标识码
    	PicNewsUpload,  //图片新闻图片上传的标识码
		VideoNewsUpload,//视频新闻图片上传的标识码
		FileDownload,//文件下载
		WeChatUpload,//微信相关上传
		CollectPic, //网页采集的图片
		interviewQuestion, //访谈内容图片
		interviewReply //访谈回复图片
    }
	
	//主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ID")
    private Long id;
    
    //文件名
    @Column(name="FILE_NAME")
    private String fileName;
    
    //文件后缀
    @Column(name="SUFFIX")
    private String suffix;
    
    //类型
    @Column(name="TYPE")
    private String type=Type.NotDefined.toString();
    
    @Column(name="CODE")
    private String code=Code.Default.toString();
    
    //状态  0:未引用   1:引用
    @Column(name="STATUS")
    private Integer status=0;
    
    //文件大小
    @Column(name="FILE_SIZE")
    private Long fileSize;
    
    //MD5码
    @Column(name="MD5_CODE")
    private String md5;
    
    //mongodb关联Id
    @Column(name="MONGO_ID")
    private String mongoId;

	//mongodb关联name
	@Column(name="MONGO_NAME")
	private String mongoName;

    //upload ip
    @Column(name="IP")
    private String ip;
    
    //所属站点
    @Column(name="SITE_ID")
    private Long siteId;
    
    //所属栏目
    @Column(name="COLUMN_ID")
    private Long columnId;
    
    //所属内容Id
    @Column(name="CONTENT_ID")
    private Long contentId;

    @Column(name="description")
    private String desc;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMongoId() {
		return mongoId;
	}

	public void setMongoId(String mongoId) {
		this.mongoId = mongoId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getMongoName() {
		return mongoName;
	}

	public void setMongoName(String mongoName) {
		this.mongoName = mongoName;
	}
}
