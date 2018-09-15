package cn.lonsun.net.service.controller;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.entity.dto.MapVO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-23 14:41
 */
@Controller
@RequestMapping("workGuide")
public class WorkGuideController extends BaseController {

    private static final String FILE_BASE = "/net/service";

    @Autowired
    private IWorkGuideService workGuideService;

    @RequestMapping("/index")
    public String guide(ModelMap map) {
        map.put("typeCode", BaseContentEO.TypeCode.workGuide.toString() + "," + BaseContentEO.TypeCode.sceneService.toString());
        return FILE_BASE + "/guide/index";
    }

    @RequestMapping("/yingzhou/index")
    public String yzguide(ModelMap map) {
        map.put("typeCode", BaseContentEO.TypeCode.workGuide.toString() + "," + BaseContentEO.TypeCode.sceneService.toString());
        return FILE_BASE + "/guide/index-yingzhou";
    }

    @RequestMapping("/tableSelect")
    public String tableSelect() {
        return FILE_BASE + "/guide/table_res_select";
    }

    @RequestMapping("/ruleSelect")
    public String ruleSelect() {
        return FILE_BASE + "/guide/rule_res_select";
    }


    @ResponseBody
    @RequestMapping("/getPageEOs")
    public Object getPageEOs(String classify,ParamDto dto) {
        if(classify != null) {
            JSONArray json = JSONArray.fromObject(classify);
            List<MapVO> vos = (List<MapVO>)JSONArray.toCollection(json,MapVO.class);
            if(vos.size() > 0) {
                dto.setMapVOs(vos);
            }
        }

        if(!RoleAuthUtil.isCurUserColumnAdmin(dto.getClassifyId()) && !LoginPersonUtil.isRoot() && !LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
            dto.setByOrgan(true);
        }

        Pagination page = workGuideService.getPageEOs(dto);
        return page;
    }

    @ResponseBody
    @RequestMapping("/getLinks")
    public Object getLinks(Long columnId) {
        ColumnTypeConfigVO vo = ModelConfigUtil.getCongfigVO(columnId, LoginPersonUtil.getSiteId());
        if(vo == null) {
            vo = new ColumnTypeConfigVO();
        }
        return getObject(vo);
    }

    @ResponseBody
    @RequestMapping("/saveEO")
    public Object saveEO(CmsWorkGuideEO eo,String cIds) {
        String returnStr = workGuideService.saveEO(eo, cIds);
        if(!StringUtils.isEmpty(returnStr)) {
            MessageSenderUtil.publishCopyNews(returnStr);
        }
        return ResponseData.success("保存成功!");
    }

    @ResponseBody
    @RequestMapping("/updateEO")
    public Object updateEO(CmsWorkGuideEO eo,String cIds) {
        String returnStr = workGuideService.updateEO(eo, cIds);
        if(!StringUtils.isEmpty(returnStr)) {
            MessageSenderUtil.publishCopyNews(returnStr);
        }
        return ResponseData.success("更新成功!");
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Object deleteEO(String ids) {
        Long[] idsn = StringUtils.getArrayWithLong(ids,",");
        String returnStr = workGuideService.deleteEO(idsn);
        if(!StringUtils.isEmpty(returnStr)) {
            MessageSenderUtil.unPublishCopyNews(returnStr);
        }
        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping("/publish")
    public Object publish(String ids,Long publish) {
        String msg = (null != publish && publish.intValue() == 1)?"发布":"取消发布";
        if(ids == null) {
            return ResponseData.success(msg + "失败!");
        }
        Long[] idsl = StringUtils.getArrayWithLong(ids, ",");

        if(idsl.length <= 0) {
            return ResponseData.success(msg + "失败!");
        }

        String returnStr = workGuideService.publish(idsl, publish);
        if(!StringUtils.isEmpty(returnStr)) {
            if(publish != null && publish.intValue() == 1) {
                MessageSenderUtil.publishCopyNews(returnStr);
            }
            else {
                MessageSenderUtil.unPublishCopyNews(returnStr);
            }
        }
        return ResponseData.success(msg + "成功!");
    }
}
