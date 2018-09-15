package cn.lonsun.content.vo;

import cn.lonsun.mongodb.vo.MongoFileVO;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-6<br/>
 */

public class VideoTransVO extends MongoFileVO{
    //状态值
    private Integer status;

    //信息
    private String msg;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
