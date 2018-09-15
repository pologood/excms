package cn.lonsun.msg.submit.controller;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitClassifyEO;
import cn.lonsun.msg.submit.service.IMsgSubmitClassifyService;
import cn.lonsun.msg.submit.service.IMsgSubmitService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-26 9:57
 */
@Controller
@RequestMapping("/msg/submit/classify")
public class MsgSubmitClassifyController extends BaseController {

    private static final String FILE_BASE = "/msg/submit";

    @Autowired
    private IMsgSubmitClassifyService msgSubmitClassifyService;

    @Autowired
    private IMsgSubmitService msgSubmitService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/classify/index";
    }

    @RequestMapping("/addOrEdit")
    public ModelAndView addOrEdit() {
        ModelAndView model = new ModelAndView(FILE_BASE + "/classify/edit");
        return model;
    }

    @ResponseBody
    @RequestMapping("/getEOs")
    public Object getEOs() {
        return msgSubmitClassifyService.getEOs();
    }

    @ResponseBody
    @RequestMapping("/getPageEOs")
    public Object getPageEOs(ParamDto dto) {
        return msgSubmitClassifyService.getPageEOs(dto);
    }

    @ResponseBody
    @RequestMapping("/save")
    public Object saveEO(CmsMsgSubmitClassifyEO eo) {
        msgSubmitClassifyService.saveEO(eo);
        return ResponseData.success("保存成功!");
    }

    @ResponseBody
    @RequestMapping("/update")
    public Object updateEO(CmsMsgSubmitClassifyEO eo) {
        msgSubmitClassifyService.updateEO(eo);
        return ResponseData.success("更新成功!");
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Object deleteEO(String ids) {
        Long[] idsn = StringUtils.getArrayWithLong(ids,",");

        String failmsg = null;
        for(Long id : idsn) {
            Long count = msgSubmitService.getCountByClassifyId(id);
            if(null != count && count > 0) {
                CmsMsgSubmitClassifyEO eo = msgSubmitClassifyService.getEntity(CmsMsgSubmitClassifyEO.class,id);
                if(failmsg == null) {
                    failmsg = eo.getName();
                } else {
                    failmsg += "," + eo.getName();
                }
            }
        }

        if(null != failmsg) {
            return ResponseData.fail(failmsg + "下有上报的新闻，不能删除!");
        }

        msgSubmitClassifyService.deleteEO(idsn);
        return ResponseData.success("删除成功!");
    }

    @RequestMapping("getColumnTreeBySite")
    @ResponseBody
    public Object getColumnTreeBySite(@RequestParam(value = "indicatorId", required = false, defaultValue = "") Long indicatorId) {
        if(null != indicatorId) {
            return new ArrayList<ColumnMgrEO>();
        }
        List<ColumnMgrEO> list = columnConfigService.getAllTree(BaseContentEO.TypeCode.articleNews.toString());
        return list;
    }
}
