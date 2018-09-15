package cn.lonsun.core.util;

import cn.lonsun.core.exception.util.ExceptionTipsMessage;
import cn.lonsun.core.exception.util.ExceptionTipsMessage.SystemTipsKey;

/**
 * 返回数据封装
 * 
 * @author 徐建华
 *
 */
public class ResultVO {

    public ResultVO() {
    }

    private Object data;// 返回数据

    private Integer status = Status.SUCCESS.getValue();// 返回码

    private String desc;// 描述信息

    /**
     * JsonpObject返回的状态码
     * 
     * @author xujh
     *
     */
    public enum Status {
        SUCCESS(Integer.valueOf(1)), Failure(Integer.valueOf(0)), Timeout(Integer.valueOf(-9));

        private Integer value;

        Status(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    /**
     * 方法正确执行，返回数据使用该构造器
     * 
     * @param data
     */
    public ResultVO(Object data) {
        this.data = data;
    }

    /**
     * 系统出现异常，返回异常信息使用此构造器
     * 
     * @param status
     * @param tipsMessage
     */
    public ResultVO(Integer status, String tipsMessage) {
        // 校验status是否合法
        if (!isStatusIllegal(status)) {
            // 不合法，设置一个默认值返回
            this.status = Status.Failure.getValue();
            ExceptionTipsMessage tipsM = ExceptionTipsMessage.getInstance();
            this.setDesc(tipsM.get(SystemTipsKey.SystemException.toString()));
        } else {
            this.status = status;
            this.setDesc(tipsMessage);
        }
    }

    /**
     * 校验status是否合法，status必须是null，或者是枚举Status中的一个
     * 
     * @param status
     * @return
     */
    private boolean isStatusIllegal(Integer status) {
        boolean isIllegal = false;
        if (status == null) {
            isIllegal = true;
        } else {
            Status[] ss = Status.values();
            for (Status s : ss) {
                Integer value = s.getValue();
                if (status.equals(value)) {
                    isIllegal = true;
                }
            }
        }
        return isIllegal;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}