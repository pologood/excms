package cn.lonsun.monitor.config.controller;


import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.JSONConvertUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;
import cn.lonsun.monitor.config.internal.service.IMonitoredColumnConfigService;
import cn.lonsun.monitor.config.internal.service.IMonitoredVetoConfigService;
import cn.lonsun.monitor.internal.service.IMonitorSiteRegisterService;
import cn.lonsun.monitor.internal.vo.MonitorSiteRegisterVO;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.webservice.config.IMonitoredService;
import cn.lonsun.webservice.to.WebServiceTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lonsun on 2017-9-25.
 */
@Controller
@RequestMapping("/monitor/vetoConfig")
public class MonitoredVetoConfigController extends BaseController {
    @Resource
    private IMonitoredVetoConfigService monitoredVetoConfigService;
    @Resource
    private IMonitoredColumnConfigService monitoredColumnConfigService;
    @Resource
    private IMonitorSiteRegisterService monitorSiteRegisterService;
    @Resource
    private IMonitoredService monitoredService;

    @Resource
    private ISiteConfigService siteConfigService;

    /**
     * 单项否决配置首页
     * @param typeCode
     * @param modelAndView
     * @return
     */
    @RequestMapping("vetoIndex")
    public ModelAndView vetoIndex(String typeCode, ModelAndView modelAndView) {
        modelAndView.addObject("typeCode", typeCode);
        modelAndView.addObject("typeCodeList", MonitoredVetoConfigEO.BaseCode.values());
        if (AppUtil.isEmpty(typeCode)) {
            modelAndView.setViewName("/monitor/config/index");
        } else {
            modelAndView.setViewName("/monitor/config/" + typeCode);
        }
        return modelAndView;
    }

    /**
     * 通用的编辑页，根据枚举类型定位文件
     *
     * @param typeCode
     * @param modelAndView
     * @return
     */
    @RequestMapping("vetoEdit")
    public ModelAndView vetoEdit(Long id, String typeCode, ModelAndView modelAndView) {
        modelAndView.setViewName("/monitor/config/" + typeCode + "_edit");
        modelAndView.addObject("id", id);
        modelAndView.addObject("typeCode", typeCode);
        return modelAndView;
    }

    /**
     * 根据单项否决类别查询
     *
     * @param typeCode
     * @return
     */
    @RequestMapping("getDataByCode")
    @ResponseBody
    public Object getDataByCode(String typeCode, String id) {
        Map<String, Object> baseConfigVO = monitoredVetoConfigService.getDataByCode(typeCode, id, LoginPersonUtil.getSiteId());
        return getObject(baseConfigVO);
    }


    /**
     * 保存单项选择
     * @return
     */
    @RequestMapping("saveData")
    @ResponseBody
    public Object saveData(String content, String typeCode) {
        monitoredVetoConfigService.saveData(content, typeCode, LoginPersonUtil.getSiteId());
        monitoredVetoConfigService.sentConfigToCloud(LoginPersonUtil.getSiteId());
        return getObject();
    }

    /**
     * 根据单项否决类别查询
     *
     * @param typeCode
     * @return
     */
    @RequestMapping("getDataPageByCode")
    @ResponseBody
    public Object getDataPageByCode(PageQueryVO page, String typeCode) {
        Pagination result = monitoredVetoConfigService.getDataPageByCode(page, typeCode, LoginPersonUtil.getSiteId());
        return getObject(result);
    }

    /**
     * 保存综合评分-获取互动回应
     *
     * @param
     * @return
     */
    @RequestMapping("synByCode")
    @ResponseBody
    public Object synByCode(String typeCode,String columnTypeCode,String baseCode) {
        Long siteId = LoginPersonUtil.getSiteId();
        //获取站点注册码
//        SiteMgrEO siteConfigEO = siteConfigService.getById(siteId);
        MonitorSiteRegisterVO siteRegisterVO = monitorSiteRegisterService.getSiteRegisterInfo(siteId);
        //缓存存在不刷新的情况，此处换成查数据库
//        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        if(siteRegisterVO == null){
            return ajaxErr("未找到站点！");
        }
        if(siteRegisterVO.getIsRegistered() == null || siteRegisterVO.getIsRegistered() == 0 || StringUtils.isEmpty(siteRegisterVO.getRegisteredCode())){
            return ajaxErr("站点尚未注册云监控服务，请先注册！");
        }
        WebServiceTO webServiceTO = monitoredService.getMonitorConfig(typeCode, columnTypeCode, baseCode, siteId, siteRegisterVO.getRegisteredCode());
        if(webServiceTO.getStatus()==0){
            throw new BaseRunTimeException(TipsMode.Message.toString(),webServiceTO.getDescription());
        }
        String jsonData  = webServiceTO.getJsonData();
        Map<String, Object> map =JSONConvertUtil.toObejct(jsonData, HashMap.class);
        return getObject(map);
    }

}


