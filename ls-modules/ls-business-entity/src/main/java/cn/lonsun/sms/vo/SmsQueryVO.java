package cn.lonsun.sms.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * Created by zhangchao on 2016/9/19.
 */
public class SmsQueryVO  extends PageQueryVO {

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
