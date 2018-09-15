package cn.lonsun.lsrobot;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.lsrobot.entity.LsRobotParamEO;
import cn.lonsun.lsrobot.entity.LsRobotSourcesEO;
import cn.lonsun.lsrobot.service.ILsRobotParamService;
import cn.lonsun.lsrobot.service.ILsRobotSourcesService;
import cn.lonsun.lsrobot.vo.RobotPageVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author gu.fei
 * @version 2016-07-07 10:00
 */
@Controller
@RequestMapping(value = "/lsrobot/")
public class LsRobotController extends BaseController {

    private static final String FILE_BASE = "/lsrobot";

    @Autowired
    private ILsRobotParamService lsRobotParamService;

    @Autowired
    private ILsRobotSourcesService lsRobotSourcesService;

    @RequestMapping(value = "/index")
    public String index() {
        return FILE_BASE + "/index";
    }

    @RequestMapping(value = "/addOrEditSources")
    public String addOrEditSources() {
        return FILE_BASE + "/edit_sources";
    }

    @RequestMapping(value = "/editParam")
    public String editParam(ModelMap map) {
        LsRobotParamEO eo = lsRobotParamService.getEntityBySiteId(LoginPersonUtil.getSiteId());
        if(null != eo) {
            map.put("eo",eo);
            map.put("notice",eo.getNotice());
        }
        return FILE_BASE + "/edit_param";
    }


    /**
     * 修改龙讯智能机器人配置参数
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateRobotParam")
    public Object updateRobotParam(LsRobotParamEO eo) {
        eo.setSiteId(LoginPersonUtil.getSiteId());
        lsRobotParamService.saveOrUpdateEntity(eo);
        return ajaxOk();
    }

    /**
     * 删除龙讯智能机器人配置参数
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteRobotParam")
    public Object deleteRobotParam(Long id) {
        lsRobotParamService.delete(LsRobotParamEO.class, id);
        return ajaxOk();
    }

    @ResponseBody
    @RequestMapping(value = "/getPageRobotSources")
    public Object getPageRobotSources(RobotPageVO vo) {
        return lsRobotSourcesService.getPageEntities(vo);
    }

    @ResponseBody
    @RequestMapping(value = "/saveRobotSources")
    public Object saveRobotSources(LsRobotSourcesEO eo) {
        eo.setSiteId(LoginPersonUtil.getSiteId());
        lsRobotSourcesService.saveEntity(eo);
        return ajaxOk();
    }

    @ResponseBody
    @RequestMapping(value = "/updateRobotSources")
    public Object updateRobotSources(LsRobotSourcesEO eo) {
        LsRobotSourcesEO update = lsRobotSourcesService.getEntity(LsRobotSourcesEO.class,eo.getId());
        update.setSeqNum(eo.getSeqNum());
        update.setTitle(eo.getTitle());
        update.setContent(eo.getContent());
        lsRobotSourcesService.updateEntity(update);
        return ajaxOk();
    }

    @ResponseBody
    @RequestMapping(value = "/deleteRobotSources")
    public Object deleteRobotSources(@RequestParam(value="ids[]",required=false) Long[] ids) {
        lsRobotSourcesService.delete(LsRobotSourcesEO.class, ids);
        return ajaxOk();
    }

    @ResponseBody
    @RequestMapping(value = "/activeRobotSources")
    public Object activeRobotSources(Long id,String active) {
        LsRobotSourcesEO update = lsRobotSourcesService.getEntity(LsRobotSourcesEO.class,id);
        update.setIfActive(active);
        lsRobotSourcesService.updateEntity(update);
        return ajaxOk();
    }

    @ResponseBody
    @RequestMapping(value = "/showRobotSources")
    public Object showRobotSources(Long id,String show) {
        LsRobotSourcesEO update = lsRobotSourcesService.getEntity(LsRobotSourcesEO.class,id);
        update.setIfShow(show);
        lsRobotSourcesService.updateEntity(update);
        return ajaxOk();
    }

    @ResponseBody
    @RequestMapping(value = "/getMaxSortNum")
    public Object getMaxSortNum() {
        return getObject(lsRobotSourcesService.getMaxSortNum());
    }
}
