package cn.lonsun.sms.czsms.vo;

public class RetStatus
{
    public static int E_SUCCESS = 0;
    public static int E_ERROR = 1;
    public static String E_ERROR_DESC="系统忙或未知错误";
    public static int E_NOTLOGIN = -1;
    public static String E_NOTLOGIN_DESC = "用户未登录，请重新登录";

    /**
     * 返回状态，0为成功，其它为失败
     */
    private int status = 0;

    /**
     * 返回结果描述
     */
    private String desc = "成功";

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

}
