package cn.lonsun.sms.czsms.vo;

import java.util.ArrayList;
import java.util.List;

public class SendSmsRet extends RetStatus
{
    private long onlyID = 0;// 消息标识
    private List<MsgError> errorList = new ArrayList<MsgError>();

    public long getOnlyID()
    {
        return onlyID;
    }

    public void setOnlyID(long onlyID)
    {
        this.onlyID = onlyID;
    }

    public List<MsgError> getErrorList()
    {
        return errorList;
    }

    public void setErrorList(List<MsgError> errorList)
    {
        this.errorList = errorList;
    }

}
