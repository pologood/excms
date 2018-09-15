package cn.lonsun.sms.czsms.vo;

import java.util.ArrayList;
import java.util.List;

public class StatusReportRet extends RetStatus
{
    private List<MsgStatus> statusL=new ArrayList<MsgStatus>();

    public List<MsgStatus> getStatusL()
    {
        return statusL;
    }

    public void setStatusL(List<MsgStatus> statusL)
    {
        this.statusL = statusL;
    }

}
