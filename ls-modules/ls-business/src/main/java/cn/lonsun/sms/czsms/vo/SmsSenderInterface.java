package cn.lonsun.sms.czsms.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface SmsSenderInterface
{
    public Log log = LogFactory.getLog(SmsSenderInterface.class.getName());

    /**
     * 查询状态报告
     * @param sessionId 会话ID
     * @param msgId 消息ID
     * @return
     */
    public StatusReportRet queryStatusReport(String sessionId, long msgId);

    /**
     * 接收短信
     * @param sessionId 会话ID
     * @return
     */
    public ReceiveSmRet receiveSm(String sessionId);

    /**
     * 发送短信
     * @param sessionId 会话ID
     * @param destAddr 目的号码
     * @param content 短信内容
     * @return
     */
    public SendSmsRet sendSms(String sessionId, String destAddr, String content);

    /**
     * 登录
     * @param enterAccount 集团帐号
     * @param userName 用户名
     * @param userPwd 密码
     * @param srcExtCode 扩展码
     * @param userType 用户类型 辽宁、陕西有特殊用途，其它传0
     * @param localIp 客户端IP
     * @param localPort 客户端端口
     * @return
     */
    public LoginRet userLogin(String enterAccount, String userName,
                              String userPwd, String srcExtCode, int userType, String localIp,
                              int localPort);

    /**
     * 发送模版短信（指定模版ID方式）
     * @param sessionId 会话ID
     * @param templateId 模版ID 0为全匹配方式
     * @param destAddr 目的号码
     * @param params 以逗号分隔的参数
     * @return
     */
    public SendSmsRet sendTemplateMsg(String sessionId, int templateId,
                                      String destAddr, String params);

    // /**
    // * 发送模版短信（全匹配方式）
    // * @param sessionId 会话ID
    // * @param destAddr 目的号码
    // * @param params 短信内容
    // * @return
    // */
    // public SendSmsRet sendTemplateMsg(String sessionId, String destAddr,
    // String content);
}
