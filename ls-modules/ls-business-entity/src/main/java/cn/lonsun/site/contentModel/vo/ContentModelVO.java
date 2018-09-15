package cn.lonsun.site.contentModel.vo;




/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-2<br/>
 */

public class ContentModelVO extends  ColumnTypeConfigVO{
    private Long id;
    private String code;
    private String description;
    private String name;
    private Long siteId;
    private String content;
    private Integer isPublic=0;

    private Long tplId;
    private Long articalTempId;
    private String modelTypeCode;
	private Long processId;
	private String processName;
    private Integer type=0;
    private Long columnTempId;
    private String articalTempName;
    private Long commentTempId;
    private String columnTempName;
    private String modelTypeName;
	private Long wapArticalTempId;
	private Long wapColumnTempId;
	private String wapColumnTempName;
	private String wapArticalTempName;


	//政民论坛信息
    private Long settingId;	

	//(站点id)	
	private Long caseId;

	//(是否允许发布帖子)
	private Integer isIssue = 1;

	//(是否允许你回复帖子)
	private Integer isReply = 1;

	//(注册会员积分增加)	
	private Integer registerNum = 0; 

	//(登录积分增加)
	private Integer loginNum = 0;	

	//(发帖积分增加)
	private Integer postedNum = 0;		

	//(回帖积分增加)
	private Integer replyNum = 0;		

	//(帖子审核通过后积分增加)
	private Integer checkNum = 0;		

	//(删除积分减少)
	private Integer delNum = 0;		

	//(置顶积分增加)
	private Integer topNum = 0;		

	//(推荐积分增加)
	private Integer essenceNum = 0;	

	//(回复年限定)
	private Integer year;		

	//(禁止发帖ip)	
	private String ips;

	//(日期池)
	private String times;		

	//(超时回复天数)
	private Integer replyDay;		

	//(黄牌天数)
	private Integer yellowDay;		

	//(红牌天数)
	private Integer redDay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getColumnTempId() {
        return columnTempId;
    }

    public void setColumnTempId(Long columnTempId) {
        this.columnTempId = columnTempId;
    }

    public Long getArticalTempId() {
        return articalTempId;
    }

    public void setArticalTempId(Long articalTempId) {
        this.articalTempId = articalTempId;
    }

    public Long getTplId() {
        return tplId;
    }

    public void setTplId(Long tplId) {
        this.tplId = tplId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getModelTypeCode() {
        return modelTypeCode;
    }

    public void setModelTypeCode(String modelTypeCode) {
        this.modelTypeCode = modelTypeCode;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getArticalTempName() {
        return articalTempName;
    }

    public void setArticalTempName(String articalTempName) {
        this.articalTempName = articalTempName;
    }

    public String getColumnTempName() {
        return columnTempName;
    }

    public void setColumnTempName(String columnTempName) {
        this.columnTempName = columnTempName;
    }

    public String getModelTypeName() {
        return modelTypeName;
    }

    public void setModelTypeName(String modelTypeName) {
        this.modelTypeName = modelTypeName;
    }

    public Long getCommentTempId() {
        return commentTempId;
    }

    public void setCommentTempId(Long commentTempId) {
        this.commentTempId = commentTempId;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }

	public Long getSettingId() {
		return settingId;
	}

	public void setSettingId(Long settingId) {
		this.settingId = settingId;
	}

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public Integer getIsIssue() {
		return isIssue;
	}

	public void setIsIssue(Integer isIssue) {
		this.isIssue = isIssue;
	}

	public Integer getIsReply() {
		return isReply;
	}

	public void setIsReply(Integer isReply) {
		this.isReply = isReply;
	}

	public Integer getRegisterNum() {
		return registerNum;
	}

	public void setRegisterNum(Integer registerNum) {
		this.registerNum = registerNum;
	}

	public Integer getLoginNum() {
		return loginNum;
	}

	public void setLoginNum(Integer loginNum) {
		this.loginNum = loginNum;
	}

	public Integer getPostedNum() {
		return postedNum;
	}

	public void setPostedNum(Integer postedNum) {
		this.postedNum = postedNum;
	}

	public Integer getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(Integer replyNum) {
		this.replyNum = replyNum;
	}

	public Integer getCheckNum() {
		return checkNum;
	}

	public void setCheckNum(Integer checkNum) {
		this.checkNum = checkNum;
	}

	public Integer getDelNum() {
		return delNum;
	}

	public void setDelNum(Integer delNum) {
		this.delNum = delNum;
	}

	public Integer getTopNum() {
		return topNum;
	}

	public void setTopNum(Integer topNum) {
		this.topNum = topNum;
	}

	public Integer getEssenceNum() {
		return essenceNum;
	}

	public void setEssenceNum(Integer essenceNum) {
		this.essenceNum = essenceNum;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getIps() {
		return ips;
	}

	public void setIps(String ips) {
		this.ips = ips;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public Integer getReplyDay() {
		return replyDay;
	}

	public void setReplyDay(Integer replyDay) {
		this.replyDay = replyDay;
	}

	public Integer getYellowDay() {
		return yellowDay;
	}

	public void setYellowDay(Integer yellowDay) {
		this.yellowDay = yellowDay;
	}

	public Integer getRedDay() {
		return redDay;
	}

	public void setRedDay(Integer redDay) {
		this.redDay = redDay;
	}


	public Long getWapArticalTempId() {
		return wapArticalTempId;
	}

	public void setWapArticalTempId(Long wapArticalTempId) {
		this.wapArticalTempId = wapArticalTempId;
	}

	public Long getWapColumnTempId() {
		return wapColumnTempId;
	}

	public void setWapColumnTempId(Long wapColumnTempId) {
		this.wapColumnTempId = wapColumnTempId;
	}


	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getWapColumnTempName() {
		return wapColumnTempName;
	}

	public void setWapColumnTempName(String wapColumnTempName) {
		this.wapColumnTempName = wapColumnTempName;
	}

	public String getWapArticalTempName() {
		return wapArticalTempName;
	}

	public void setWapArticalTempName(String wapArticalTempName) {
		this.wapArticalTempName = wapArticalTempName;
	}

}
