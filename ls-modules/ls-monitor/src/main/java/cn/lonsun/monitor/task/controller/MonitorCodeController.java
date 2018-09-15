package cn.lonsun.monitor.task.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.monitor.config.internal.service.IMonitoredColumnConfigService;
import cn.lonsun.monitor.internal.entity.MonitorCodeRegisterEO;
import cn.lonsun.monitor.internal.entity.MonitorSiteRegisterEO;
import cn.lonsun.monitor.internal.service.IMonitorCodeRegisterService;
import cn.lonsun.monitor.internal.service.IMonitorSiteRegisterService;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.webservice.monitor.client.ICheckMonitorCodeWebClient;
import cn.lonsun.webservice.monitor.client.IMonitorSiteRegisterClient;
import cn.lonsun.webservice.monitor.client.vo.SiteRegisterVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author liuk
 * @version 2017-09-28 9:25
 */
@Controller
@RequestMapping(value = "/monitor/code")
public class MonitorCodeController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String FILE_BASE = "/monitor/code/";

    @Resource
    private ICheckMonitorCodeWebClient checkMonitorCodeWebClient;

    @Resource
    private IMonitorSiteRegisterClient monitorSiteRegisterClient;

    @Resource
    private ISiteConfigService siteConfigService;

    @Resource
    private IMonitorSiteRegisterService monitorSiteRegisterService;

    @Resource
    private IMonitoredColumnConfigService monitoredColumnConfigService;

    @Resource
    private IMonitorCodeRegisterService monitorCodeRegisterService;

    @Resource
    private TaskExecutor taskExecutor;

    /**
     * 任务主页面
     * @return
     */
    @RequestMapping(value = "index")
    public String index(ModelMap map) {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("isRegistered",1);
        MonitorCodeRegisterEO codeRegisterEO = monitorCodeRegisterService.getEntity(MonitorCodeRegisterEO.class,param);
        if(codeRegisterEO!=null&&!AppUtil.isEmpty(codeRegisterEO.getCode())){
            map.put("isRegistered",codeRegisterEO.getIsRegistered());
            map.put("code",codeRegisterEO.getCode());
        }else{
            map.put("isRegistered",0);
        }
        return FILE_BASE + "index";
    }


    /**
     * 注册
     * @param code
     * @return
     */
    @RequestMapping(value = "register")
    @ResponseBody
    public Object register(String code) {
        if(AppUtil.isEmpty(code)){
            return ajaxErr("注册码不能为空");
        }
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("isRegistered",1);
        MonitorCodeRegisterEO codeRegisterEO = monitorCodeRegisterService.getEntity(MonitorCodeRegisterEO.class,param);
        if(codeRegisterEO!=null&&!AppUtil.isEmpty(codeRegisterEO.getCode())){
            return ajaxErr("该站群已经注册过，不能重复注册");
        }else{
            Object object = checkMonitorCodeWebClient.checkMonitorCode(code);//获取返回的数据，目前不返回数据
            MonitorCodeRegisterEO monitorCodeRegisterEO = new MonitorCodeRegisterEO();
            monitorCodeRegisterEO.setCode(code);
            monitorCodeRegisterEO.setIsRegistered(1);
            monitorCodeRegisterService.saveEntity(monitorCodeRegisterEO);
        }
        return getObject();
    }

    /**
     * 获取站点信息分页列表
     * @return
     */
    @RequestMapping(value = "getSites")
    @ResponseBody
    public Object getSites(Long pageIndex,Integer pageSize) {
        if(pageIndex==null){
            pageIndex = 0l;
        }
        if(pageSize==null){
            pageSize = 10;
        }
        return getObject(monitorSiteRegisterService.getSiteRegisterInfos(pageIndex,pageSize));
    }

    /**
     * 选择站点
     * @return
     */
    @RequestMapping(value = "chooseSites")
    public String chooseSites(String siteIds,ModelMap map) {
        map.addAttribute("siteIds",siteIds);
        return FILE_BASE + "choose_sites";
    }

    /**
     * 开通站点
     * @return
     */
    @RequestMapping(value = "ableMonitor")
    @ResponseBody
    public Object ableMonitor(String code, @RequestParam("siteIds[]")String[] siteIds) {
        Object object = checkMonitorCodeWebClient.checkMonitorCode(code);//获取返回的数据，目前不返回数据
        List<String> register = new ArrayList<String>();
        if(!AppUtil.isEmpty(siteIds)){
            List<SiteRegisterVO> registerArray = new ArrayList<SiteRegisterVO>();
            for(int i=0;i<siteIds.length;i++){
                SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,Long.parseLong(siteIds[i]));
                String siteName = siteMgrEO.getName();
                SiteRegisterVO vo = new SiteRegisterVO();
                vo.setSiteId(Long.parseLong(siteIds[i]));
                vo.setSiteName(siteName);
                vo.setRegisterCode(code);
                vo.setUrl(siteMgrEO.getUri());
                registerArray.add(vo);

                //更改站点配置表中的注册状态
//                SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, Long.parseLong(siteIds[i]));
                MonitorSiteRegisterEO siteRegisterEO = monitorSiteRegisterService.getBySiteId(Long.parseLong(siteIds[i]));
                //如果站点已注册，则不同步数据
                if(siteRegisterEO == null || StringUtils.isEmpty(siteRegisterEO.getRegisteredCode())){
                    register.add(siteIds[i]);
                    siteRegisterEO = new MonitorSiteRegisterEO();
                    siteRegisterEO.setSiteId(Long.parseLong(siteIds[i]));
                    siteRegisterEO.setIsRegistered(1);//已开通
                    siteRegisterEO.setRegisteredCode(code);
                    siteRegisterEO.setRegisteredTime(new Date());//开通时间
                    monitorSiteRegisterService.saveEntity(siteRegisterEO);
                }else{
                    siteRegisterEO.setSiteId(Long.parseLong(siteIds[i]));
                    siteRegisterEO.setIsRegistered(1);//已开通
                    siteRegisterEO.setRegisteredCode(code);
                    siteRegisterEO.setRegisteredTime(new Date());//开通时间
                    monitorSiteRegisterService.updateEntity(siteRegisterEO);
                }

            }
            //同步栏目配置
//            syncConfigFromCloud(code,siteIds);
            //调用推送站点信息给云平台的webservice接口
            monitorSiteRegisterClient.saveSiteRegister(registerArray);

        }
        return getObject(register);
    }

    /**
     * 取消开通站点
     * @return
     */
    @RequestMapping(value = "disableMonitor")
    @ResponseBody
    public Object disableMonitor(String code,@RequestParam("siteIds[]")String[] siteIds) {
        if(!AppUtil.isEmpty(siteIds)){
            for(int i=0;i<siteIds.length;i++){
                //更改站点配置表中的注册状态
                MonitorSiteRegisterEO siteRegisterEO = monitorSiteRegisterService.getBySiteId(Long.parseLong(siteIds[i]));
                siteRegisterEO.setIsRegistered(0);//取消开通
                monitorSiteRegisterService.updateEntity(siteRegisterEO);
                //调用推送站点信息给云平台的webservice接口
                monitorSiteRegisterClient.disableSiteRegister(code,Long.parseLong(siteIds[i]));
            }
        }
        return getObject();
    }



    /**
     * 同步栏目配置
     * @return
     */
    @RequestMapping(value = "syncConfigFromCloud")
    @ResponseBody
    public Object syncConfigFromCloud(String code,@RequestParam("siteIds[]")String[] siteIds) {
        Object object = checkMonitorCodeWebClient.checkMonitorCode(code);//获取返回的数据，目前不返回数据
        if(!AppUtil.isEmpty(siteIds)){
            try {
                //同步栏目类型配置
                boolean flag = monitoredColumnConfigService.syncConfigFromCloud(siteIds);
                if(!flag){
                    return ajaxErr("同步栏目配置异常，请重试或联系管理员！");
                }
            } catch (BaseRunTimeException e) {
                return ajaxErr("同步栏目配置异常，请重试或联系管理员！");
            }
        }
        return getObject();
    }



}
