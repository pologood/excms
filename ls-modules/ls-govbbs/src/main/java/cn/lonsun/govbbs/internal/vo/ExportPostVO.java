package cn.lonsun.govbbs.internal.vo;

public class ExportPostVO {

	private String oldId;

	private Long acceptUnitId;

	private Long postId;

	private Long plateId;

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

	public Long getAcceptUnitId() {
		return acceptUnitId;
	}

	public void setAcceptUnitId(Long acceptUnitId) {
		this.acceptUnitId = acceptUnitId;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}
}