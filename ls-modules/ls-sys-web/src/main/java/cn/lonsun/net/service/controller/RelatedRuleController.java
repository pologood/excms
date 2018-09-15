package cn.lonsun.net.service.controller;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsRelatedRuleEO;
import cn.lonsun.net.service.entity.dto.MapVO;
import cn.lonsun.net.service.service.IRelatedRuleService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.ResponseData;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 相关犯法规controller类
 * @author gu.fei
 * @version 2015-11-21 9:23
 */
@Controller
@RequestMapping("relatedRule")
public class RelatedRuleController extends BaseController {

    private static final String FILE_BASE = "/net/service";

    @Autowired
    private IRelatedRuleService relatedRuleService;

    @RequestMapping("/index")
    public String index(ModelMap map) {
        map.put("typeCode", BaseContentEO.TypeCode.workGuide.toString() + "," + BaseContentEO.TypeCode.sceneService.toString());
        return FILE_BASE + "/rule/index";
    }

    @RequestMapping("/addOrEdit")
    public ModelAndView addOrEdit() {
        ModelAndView model = new ModelAndView(FILE_BASE + "/rule/rule_edit");
        return model;
    }
    @RequestMapping("/addNew")
    public ModelAndView addNew() {
        ModelAndView model = new ModelAndView(FILE_BASE + "/rule/rule_edit01");
        model.addObject("typeCode",BaseContentEO.TypeCode.workGuide.toString() + "," + BaseContentEO.TypeCode.sceneService.toString());
        return model;
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
        Pagination page = relatedRuleService.getPageEOs(dto);
        return page;
    }

    @ResponseBody
    @RequestMapping("/saveEO")
    public Object saveEO(CmsRelatedRuleEO eo,String cIds) {
        String returnStr = relatedRuleService.saveEO(eo, cIds);
        if(!StringUtils.isEmpty(returnStr)) {
            MessageSenderUtil.publishCopyNews(returnStr);
        }
        return ResponseData.success("保存成功!");
    }

    @ResponseBody
    @RequestMapping("/updateEO")
    public Object updateEO(CmsRelatedRuleEO eo,String cIds) {
        String returnStr = relatedRuleService.updateEO(eo, cIds);
        if(!StringUtils.isEmpty(returnStr)) {
            MessageSenderUtil.publishCopyNews(returnStr);
        }
        return ResponseData.success("更新成功!");
    }

    @ResponseBody
    @RequestMapping("/deleteEO")
    public Object deleteEO(String ids) {
        Long[] idsn = StringUtils.getArrayWithLong(ids,",");
        String returnStr = relatedRuleService.deleteEO(idsn);
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
        Long[] idsl = StringUtils.getArrayWithLong(ids,",");

        if(idsl.length <= 0) {
            return ResponseData.success(msg + "失败!");
        }

        String returnStr = relatedRuleService.publish(idsl, publish);
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
