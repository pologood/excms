/*
 * 版权所有 (C) 2001-2015 深圳市艾派应用系统有限公司。保留所有权利。
 * 版本：
 * 修改记录：
 *		1、2015-4-16，ypsong创建。
 */
package cn.lonsun.sms.czsms.vo;

public class LoginRet extends RetStatus
{
    /**
     * 响应会话ID
     */
    private String sessionId;

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

}
