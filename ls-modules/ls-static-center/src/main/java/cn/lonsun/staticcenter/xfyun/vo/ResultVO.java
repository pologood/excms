package cn.lonsun.staticcenter.xfyun.vo;

/**
 * Created by huangxx on 2018/7/4.
 */
public class ResultVO {

    private String data;//语音内容

    private String desc;//描述

    private String code;//结果码

    private String sid;//会话ID

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
