package cn.lonsun.webservice.rbac.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.rbac.IPlatformService;
import cn.lonsun.webservice.vo.rbac.PlatformVO;

@Service("indirectPlatformService")
public class PlatformServiceImpl implements IPlatformService {

    /**
     * 服务编码
     *
     * @author xujh
     * @version 1.0 2015年4月24日
     *
     */
    private enum Codes {
        Platform_getExternalPlatforms, Platform_getCurrentPlatformCode
    }

    @Override
    public List<PlatformVO> getExternalPlatforms() {
        String code = Codes.Platform_getExternalPlatforms.toString();
        return (List<PlatformVO>)WebServiceCaller.getList(code, new Object[] {}, PlatformVO.class);
    }

    @Override
    public String getCurrentPlatformCode() {
        String code = Codes.Platform_getCurrentPlatformCode.toString();
        return (String)WebServiceCaller.getSimpleObject(code, new Object[] {}, String.class);
    }
}