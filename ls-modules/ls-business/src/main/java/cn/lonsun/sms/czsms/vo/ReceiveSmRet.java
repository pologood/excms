package cn.lonsun.sms.czsms.vo;

import java.util.ArrayList;
import java.util.List;


public class ReceiveSmRet extends RetStatus
{
    private List<DeliverPO> deliverL = new ArrayList<DeliverPO>();

    public List<DeliverPO> getDeliverL()
    {
        return deliverL;
    }

    public void setDeliverL(List<DeliverPO> deliverL)
    {
        this.deliverL = deliverL;
    }

}
