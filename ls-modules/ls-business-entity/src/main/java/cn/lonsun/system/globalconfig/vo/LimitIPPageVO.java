package cn.lonsun.system.globalconfig.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * @author Doocal
 * @ClassName: LimitIPPageVO
 * @Description: 限制IP分页
 */
public class LimitIPPageVO extends PageQueryVO {

    //IP
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
