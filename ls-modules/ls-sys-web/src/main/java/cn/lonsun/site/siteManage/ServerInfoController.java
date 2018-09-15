package cn.lonsun.site.siteManage;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.site.serverInfo.internal.entity.ServerInfoEO;
import cn.lonsun.site.serverInfo.internal.service.IServerInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author gu.fei
 * @version 2017-08-01 8:14
 * 服务器信息
 */
@Controller
@RequestMapping(value = "serverInfo")
public class ServerInfoController extends BaseController {

    private static final String FILE_BASE = "/serverInfo/";

    @Resource
    private IServerInfoService serverInfoService;

    /**
     * 主页
     * @return
     */
    @RequestMapping(value = "index")
    public String index() {
        return FILE_BASE + "index";
    }

    /**
     * 获取服务器配置信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getServerInfo")
    public Object getServerInfo() {
        ServerInfoEO info = serverInfoService.getEntity(ServerInfoEO.class,new HashMap<String,Object>());
        return getObject(null == info?new ServerInfoEO():info);
    }

    /**
     * 更新服务器配置信息
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "updateServerInfo")
    public Object updateServerInfo(ServerInfoEO eo) {
        if(null == eo.getId()) {
            serverInfoService.saveEntity(eo);
        } else {
            serverInfoService.updateEntity(eo);
        }
        return ajaxOk();
    }
}
