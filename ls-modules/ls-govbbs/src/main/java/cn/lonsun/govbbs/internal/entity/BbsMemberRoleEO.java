package cn.lonsun.govbbs.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import javax.persistence.*;
/**
 * 会员角色组
 */
@Entity
@Table(name="CMS_BBS_MEMBER_ROLE")
public class BbsMemberRoleEO extends AMockEntity {

	private static final long serialVersionUID = 1L;


	//(主键id)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Long id;

	//(站点id)
	@Column(name="SITE_ID")
	private Long siteId;

	//(头衔名称)
	@Column(name="name")
	private String name;

	//(星星数)
	@Column(name="star_number")
	private Integer starNumber=0;

	//(积分需求)
	@Column(name="riches")
	private Integer riches=0;

	//(是允许发表新主题)
	@Column(name="can_thread")
	private Integer canThread=1;

	//(允许发表新回复)
	@Column(name="can_post")
	private Integer canPost = 1;

	//(允许直接发帖)
	@Column(name="need_confirm")
	private Integer needConfirm =0;

	//(允许下载/查看附件)
	@Column(name="can_download")
	private Integer canDownload = 1;

	//(允许发布附件)
	@Column(name="can_upload")
	private Integer canUpload = 1;

	//(删除帖子)
	@Column(name="can_remove")
	private Integer canRemove  = 0;

	//(审核帖子)
	@Column(name="can_confirm")
	private Integer canConfirm = 0;

	//(设置精华贴)
	@Column(name="can_set_good")
	private Integer canSetGood=0;

	//(设置贴子频道)
	@Column(name="can_set_channel_top")
	private Integer canSetChannelTop=0;

	//(置顶贴子总置顶)
	@Column(name="can_set_top")
	private Integer canSetTop=0;

	//(锁定贴子)
	@Column(name="can_set_lock")
	private Integer canSetLock=0;

	//(老的Id)
	@Column(name="old_id")
	private String oldId;

	//(等级)
	@Column(name="file_number")
	private Integer fileNumber;

	//(等级)
	@Column(name="OLD_LEVEL")
	private Integer level;

	public Integer getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(Integer fileNumber) {
		this.fileNumber = fileNumber;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStarNumber() {
		return starNumber;
	}

	public void setStarNumber(Integer starNumber) {
		this.starNumber = starNumber;
	}

	public Integer getRiches() {
		return riches;
	}

	public void setRiches(Integer riches) {
		this.riches = riches;
	}

	public Integer getCanThread() {
		return canThread;
	}

	public void setCanThread(Integer canThread) {
		this.canThread = canThread;
	}

	public Integer getCanPost() {
		return canPost;
	}

	public void setCanPost(Integer canPost) {
		this.canPost = canPost;
	}

	public Integer getNeedConfirm() {
		return needConfirm;
	}

	public void setNeedConfirm(Integer needConfirm) {
		this.needConfirm = needConfirm;
	}

	public Integer getCanDownload() {
		return canDownload;
	}

	public void setCanDownload(Integer canDownload) {
		this.canDownload = canDownload;
	}

	public Integer getCanUpload() {
		return canUpload;
	}

	public void setCanUpload(Integer canUpload) {
		this.canUpload = canUpload;
	}

	public Integer getCanRemove() {
		return canRemove;
	}

	public void setCanRemove(Integer canRemove) {
		this.canRemove = canRemove;
	}

	public Integer getCanConfirm() {
		return canConfirm;
	}

	public void setCanConfirm(Integer canConfirm) {
		this.canConfirm = canConfirm;
	}

	public Integer getCanSetGood() {
		return canSetGood;
	}

	public void setCanSetGood(Integer canSetGood) {
		this.canSetGood = canSetGood;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Integer getCanSetChannelTop() {
		return canSetChannelTop;
	}

	public void setCanSetChannelTop(Integer canSetChannelTop) {
		this.canSetChannelTop = canSetChannelTop;
	}

	public Integer getCanSetTop() {
		return canSetTop;
	}

	public void setCanSetTop(Integer canSetTop) {
		this.canSetTop = canSetTop;
	}

	public Integer getCanSetLock() {
		return canSetLock;
	}

	public void setCanSetLock(Integer canSetLock) {
		this.canSetLock = canSetLock;
	}
}
