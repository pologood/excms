package cn.lonsun.content.leaderwin.internal.entity;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;



import cn.lonsun.core.base.entity.ABaseEntity;

@Entity
@Table(name="CMS_LEADER_INFO")
public class LeaderInfoEO extends ABaseEntity{

	
	public enum Issued {
		Yes(1), // 是
		No(0);// 否
		private Integer issued;
		private Issued(Integer issued){
			this.issued=issued;
		}
		public Integer getIssued(){
			return issued;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="LEADER_INFO_ID")
	private Long leaderInfoId;
	
	@Column(name="CONTENT_ID")
	private Long contentId;
	
	//分类id
	@Column(name="LEADER_TYPE_ID")
	private Long leaderTypeId;
	
	//职务
	@Column(name="POSITIONS_")
	private String positions;
	
	//工作分工
	@Column(name="WORK_")
	private String work;
	
	//工作简历
	@Column(name="JOB_RESUME")
	private String jobResume;
	
	@Transient
	private String typeName;

	public Long getLeaderInfoId() {
		return leaderInfoId;
	}

	public void setLeaderInfoId(Long leaderInfoId) {
		this.leaderInfoId = leaderInfoId;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public Long getLeaderTypeId() {
		return leaderTypeId;
	}

	public void setLeaderTypeId(Long leaderTypeId) {
		this.leaderTypeId = leaderTypeId;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getJobResume() {
		return jobResume;
	}

	public void setJobResume(String jobResume) {
		this.jobResume = jobResume;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	
	
}
