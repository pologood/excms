package cn.lonsun.common.vo;



/**
 * <功能详细描述>：短信VO
 *
 * @Time 2014-12-24 14:22
 */
public class SmsVo {
    //短信内容
    private String msgContent;
    //发送短信的手机号（用户手动输入的手机号，以;隔开）
    private String destMobiles;
    //模块编码，标志短信来源的模块
    private Integer moduleCode;
    //序列ID，用于短信和实际的业务数据挂接，需要支持回复的短信主记录的序列必须单独建立
    private Long sequenceId;
    //短信发送的接收人
    private ReceiverVO[] users;
    //短信发送人的ID
    private Long sendUserId;
    //短信发送人的姓名
    private String sendUserName;
    //短信发送人的单位ID
    private Long sendUnitId;
    //短信发送人的单位名称
    private String sendUnitName;
    //是否支持短信回复查看功能，0：不支持，1：支持；
    private Integer supportAnswer = Integer.valueOf(0);

    // 显示单位名
    private Integer orgChk;
    // 显示姓名
    private Integer userChk;


    public Integer getOrgChk() {
        return orgChk;
    }

    public void setOrgChk(Integer orgChk) {
        this.orgChk = orgChk;
    }

    public Integer getUserChk() {
        return userChk;
    }

    public void setUserChk(Integer userChk) {
        this.userChk = userChk;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public Integer getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(Integer moduleCode) {
        this.moduleCode = moduleCode;
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getDestMobiles() {
        return destMobiles;
    }

    public void setDestMobiles(String destMobiles) {
        this.destMobiles = destMobiles;
    }

    public ReceiverVO[] getUsers() {
        return users;
    }

    public void setUsers(ReceiverVO[] users) {
        this.users = users;
    }

    public Long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public Long getSendUnitId() {
        return sendUnitId;
    }

    public void setSendUnitId(Long sendUnitId) {
        this.sendUnitId = sendUnitId;
    }

    public String getSendUnitName() {
        return sendUnitName;
    }

    public void setSendUnitName(String sendUnitName) {
        this.sendUnitName = sendUnitName;
    }

    public Integer getSupportAnswer() {
        return supportAnswer;
    }

    public void setSupportAnswer(Integer supportAnswer) {
        this.supportAnswer = supportAnswer;
    }
}
