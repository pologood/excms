package cn.lonsun.webservice.rbac;

import java.util.List;

import cn.lonsun.webservice.vo.rbac.PlatformVO;

public interface IPlatformService {

    /**
     * 获取所有外部平台信息
     *
     * @return
     */
    public List<PlatformVO> getExternalPlatforms();

    /**
     * 获取本平台编码
     *
     * @return
     */
    public String getCurrentPlatformCode();
}