package cn.lonsun.pushinfo;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.pushinfo.entity.PushInfoEO;
import cn.lonsun.pushinfo.service.IPushInfoService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-09-20 9:27
 */
@Controller
@RequestMapping(value = "pushinfo")
public class PushInfoController extends BaseController {

    private static final String FILE_BASE = "/pushinfo/";

    @Autowired
    private IPushInfoService pushInfoService;

    @RequestMapping(value = "index")
    public String index() {
        return FILE_BASE + "index";
    }

    @RequestMapping("/addOrEdit")
    public String addOrEdit() {
        return FILE_BASE + "edit";
    }

    /**
     * 分页获取
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageEOs")
    public Object getPageTaskEOs(ParamDto dto) {
        Pagination page = pushInfoService.getPageEOs(dto);
        List<PushInfoEO> list = (List<PushInfoEO>) page.getData();

        for(PushInfoEO eo : list) {
            if(null != eo.getColumnId()) {
                eo.setColumnName(ColumnUtil.getColumnName(eo.getColumnId(),eo.getcSiteId()));
            }
        }

        return page;
    }

    /**
     * 保存采集任务
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveEO")
    public Object saveEO(PushInfoEO eo) {
        PushInfoEO info = pushInfoService.getByPath(eo.getPath());
        if(null != info) {
            return ajaxErr("路径已经存在，请用重新输入");
        }
        eo.setSiteId(LoginPersonUtil.getSiteId());
        pushInfoService.saveEntity(eo);
        return ResponseData.success("保存成功!");
    }

    /**
     * 更新采集任务
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateEO")
    public Object updateEO(PushInfoEO eo) {


        PushInfoEO neo = pushInfoService.getEntity(PushInfoEO.class,eo.getId());
        PushInfoEO info = pushInfoService.getByPath(eo.getPath());
        if(null != info && !eo.getPath().equals(neo.getPath())) {
            return ajaxErr("路径已经存在，请用重新输入");
        }
        neo.setName(eo.getName());
        neo.setColumnId(eo.getColumnId());
        neo.setPath(eo.getPath());
        neo.setPageSize(eo.getPageSize());
        neo.setIfActive(eo.getIfActive());
        neo.setcSiteId(eo.getcSiteId());
        pushInfoService.updateEntity(neo);
        return ResponseData.success("更新成功!");
    }

    /**
     * 删除任务
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteEOs")
    public Object deleteEOs(@RequestParam(value="ids[]",required=false) Long[] ids) {
        pushInfoService.deleteEOs(ids);
        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping(value = "/active")
    public Object active(Long id,Integer active) {
        PushInfoEO neo = pushInfoService.getEntity(PushInfoEO.class,id);
        if(null != neo) {
            neo.setIfActive(active);
            pushInfoService.updateEntity(neo);
        } else {
            return ajaxErr("启用失败");
        }
        return ajaxOk();
    }
}
