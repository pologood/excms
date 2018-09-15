package cn.lonsun.monitor.config.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.JSONConvertUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.monitor.config.internal.entity.MonitoredColumnConfigEO;
import cn.lonsun.monitor.config.internal.service.IMonitoredColumnConfigService;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.webservice.config.IMonitoredService;
import cn.lonsun.webservice.to.WebServiceTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**栏目类别配置服务类
 * Created by lonsun on 2017-9-22.
 */
@Controller
@RequestMapping("/monitor/column")
public class MonitoredColumnConfigController extends BaseController {
    @Resource
    private IMonitoredColumnConfigService monitoredColumnConfigService;
    @Resource
    private IMonitoredService monitoredService;
    @RequestMapping("siteColumnConfigIndex")
    private  String siteColumnConfigIndex(){
        return "/monitor/config/site_column_config_index";
    }

    /**
     * 获取配置信息
     * @param pageQueryVO
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    private  Object getPage(PageQueryVO pageQueryVO){

        Pagination pagination =monitoredColumnConfigService.getPage(pageQueryVO);
        return getObject(pagination);
    }
    @RequestMapping("edit")
    private  ModelAndView edit(Long  typeId,Integer type,String synColumnIds,String synOrganCatIds, ModelAndView model){
        model.setViewName("/monitor/config/config_edit");
        model.addObject("typeId", typeId);
        model.addObject("type",type);
        model.addObject("synOrganCatIds",synOrganCatIds);
        model.addObject("synColumnIds",synColumnIds);
        return  model;
    }



    /**
     * 同步获取云平台栏目类别
     * @param
     * @return
     */
    @RequestMapping("sysColumn")
    @ResponseBody
    private  Object sysColumn(){
        try {
            boolean flag = monitoredColumnConfigService.syncConfigFromCloud(LoginPersonUtil.getSiteId().toString());
            if(!flag){
                return ajaxErr("同步异常，请重试或联系管理员！");
            }
        } catch (BaseRunTimeException e) {
            return ajaxErr(e.getMessage());
        }
        return getObject();
    }

    /**
     *栏目配置绑定栏目
     * @return
     */
    @RequestMapping("saveBindColumn")
    @ResponseBody
    private  Object saveBindColumn(Long typeId,String synOrganCatIds,String synColumnIds ){
        MonitoredColumnConfigEO columnConfigEO =new MonitoredColumnConfigEO();
        columnConfigEO  =  monitoredColumnConfigService.queryConfigByTypeId(typeId);
        if(!AppUtil.isEmpty(synOrganCatIds)){
            String[] synOrganCatIdArray = synOrganCatIds.split(",");
            columnConfigEO.setSynOrganCatIds(synOrganCatIds);
            columnConfigEO.setPublicCateIdNum(Long.valueOf(synOrganCatIdArray.length));


        }else{
            columnConfigEO.setSynOrganCatIds("");
            columnConfigEO.setPublicCateIdNum(0l);
        }
        if(!AppUtil.isEmpty(synColumnIds)){
            String[]  synColumnIdArray =synColumnIds.split(",");
            columnConfigEO.setSynColumnIds(synColumnIds);
            columnConfigEO.setColumnIdNum(Long.valueOf(synColumnIdArray.length));

        }else{
            columnConfigEO.setSynColumnIds("");
            columnConfigEO.setColumnIdNum(0l);
        }
        monitoredColumnConfigService.updateEntity(columnConfigEO);
        return getObject();
    }
    @RequestMapping("columnDetail")
    private  ModelAndView columnDetail(Long  typeId, ModelAndView model){
        model.setViewName("/monitor/config/column_detail");
        model.addObject("typeId", typeId);
        return  model;
    }

    @RequestMapping("getColumnDetail")
    @ResponseBody
    private  Object getColumnDetail(Long  typeId){
        MonitoredColumnConfigEO columnConfigEO = monitoredColumnConfigService.getEntity(MonitoredColumnConfigEO.class, typeId);
        String synColumnIds  = columnConfigEO.getSynColumnIds();
        if(!AppUtil.isEmpty(synColumnIds)){
           String[] synColumnIdArray  = synColumnIds.split(",");
           for(String columnIdStr:  synColumnIdArray) {
            Long columnId =  Long.valueOf(columnIdStr);

           }



        }


        return  getObject();
    }

    /**
     * 栏目级别详情
     * @param columnId
     * @param model
     * @return
     */
    @RequestMapping("columnLevel")
    private  ModelAndView columnLevel(String  columnId, ModelAndView model){
        model.setViewName("/monitor/config/column_level");
        model.addObject("columnId", columnId);
        return  model;
    }


    /**
     * 获取栏目级别详情
     * @return
     */
    @RequestMapping("getColumnLevel")
    @ResponseBody
    private  Object getColumnLevel(String columnId,PageQueryVO pageQueryVO){
        Pagination pagination = monitoredColumnConfigService.getColumnLevel(columnId,pageQueryVO);
        return getObject(pagination);
    }


    /**
     * 信息公开栏目详情页
     * @param columnId
     * @param model
     * @return
     */
    @RequestMapping("publicLevel")
    private  ModelAndView publicLevel(String  columnId, ModelAndView model){
        model.setViewName("/monitor/config/public_level");
        model.addObject("columnId", columnId);
        return  model;
    }

    /**
     * 获取栏目级别详情
     * @return
     */
    @RequestMapping("getPublicLevel")
    @ResponseBody
    private  Object getPublicLevel(String columnId,PageQueryVO pageQueryVO){
        Pagination pagination = monitoredColumnConfigService.getPublicLevel(columnId,pageQueryVO);
        return getObject(pagination);
    }

}
