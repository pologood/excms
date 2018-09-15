package cn.lonsun.govbbs.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;
import cn.lonsun.core.base.entity.AMockEntity;
import javax.persistence.*;
/**
 *
 * @ClassName: BbsFileEO
 * @Description: 文件管理
 * @author Guiyang
 * @date 2016年12月12日 上午14:06:13
 *
 */
@Entity
@Table(name="CMS_BBS_FILE")
public class BbsFileEO extends ABaseEntity {

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
		Govbbs;//论坛信息
	}


	public enum RecordStatus {
		Normal, Removed;
	}


	//主键
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Long id;

	//所属站点
	@Column(name="SITE_ID")
	private Long siteId;

	//所属栏目
	@Column(name="COLUMN_ID")
	private Long columnId;

	//所属版块
	@Column(name="PLATE_ID")
	private Long plateId;

	//所属帖子
	@Column(name="POST_ID")
	private Long postId;

	//所属内容Id
	@Column(name="CASE_ID")
	private Long caseId;

	//审核状态  0 未审核  1 已审核
	@Column(name="AUDIT_STATUS")
	private Integer auditStatus = 0;

	//文件名
	@Column(name="FILE_NAME")
	private String fileName;

	//文件后缀
	@Column(name="SUFFIX")
	private String suffix;

	//类型
	@Column(name="TYPE")
	private String type= Type.NotDefined.toString();

	@Column(name="CODE")
	private String code= Code.Default.toString();

	//状态  0:未引用   1:引用
	@Column(name="STATUS")
	private Integer status=0;

	//文件大小
	@Column(name="FILE_SIZE")
	private Long fileSize;

	//文件大小
	@Column(name="FILE_SIZE_KB")
	private String fileSizeKb;

	//MD5码
	@Column(name="MD5_CODE")
	private String md5;

	//mongodb关联Id
	@Column(name="MONGO_ID")
	private String mongoId;

	//upload ip
	@Column(name="IP")
	private String ip;


	@Column(name="description")
	private String desc;

	@Column(name="create_User_Name")
	private String createUserName;


	/**
	 * 记录状态：正常：Normal,已删除:Removed
	 * */
	@Column(name = "RECORD_STATUS")
	private String recordStatus = AMockEntity.RecordStatus.Normal.toString();

	//回复Id
	@Column(name="REPLY_ID")
	private Long replyId;

	@Column(name="old_Id")
	private String oldId;

	public Long getReplyId() {
		return replyId;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}


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

	public Long getPlateId() {
		return plateId;
	}

	public void setPlateId(Long plateId) {
		this.plateId = plateId;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileSizeKb() {
		return fileSizeKb;
	}

	public void setFileSizeKb(String fileSizeKb) {
		this.fileSizeKb = fileSizeKb;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public Integer getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}
}
