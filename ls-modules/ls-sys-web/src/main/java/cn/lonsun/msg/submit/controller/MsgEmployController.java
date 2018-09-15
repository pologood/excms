package cn.lonsun.msg.submit.controller;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitClassifyEO;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitEO;
import cn.lonsun.msg.submit.entity.CmsMsgToColumnEO;
import cn.lonsun.msg.submit.entity.vo.EmployParamVo;
import cn.lonsun.msg.submit.entity.vo.ExportEO;
import cn.lonsun.msg.submit.service.IMsgSubmitService;
import cn.lonsun.msg.submit.service.IMsgToColumnService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.site.words.util.ExportExcel;
import cn.lonsun.util.ColumnUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-27 10:49
 */
@Controller
@RequestMapping("/msg/employ")
public class MsgEmployController extends BaseController {

    private static final String FILE_BASE = "/msg/submit";

    @Autowired
    private IMsgSubmitService msgSubmitService;

    @Autowired
    private IMsgToColumnService msgToColumnService;

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/employ/index";
    }

    @RequestMapping("/employOther")
    public ModelAndView employOther() {
        ModelAndView model = new ModelAndView(FILE_BASE + "/employ/employ_other");
        return model;
    }

    @RequestMapping("/batchEmployOther")
    public ModelAndView batchEmployOther() {
        ModelAndView model = new ModelAndView(FILE_BASE + "/employ/batch_employ_other");
        return model;
    }

    @RequestMapping("/msgDetail")
    public ModelAndView msgDetail(Long id) {
        ModelAndView model = new ModelAndView(FILE_BASE + "/employ/msg_detail");
        CmsMsgSubmitEO eo = msgSubmitService.getEntity(CmsMsgSubmitEO.class, id);
        CmsMsgSubmitClassifyEO cEO = CacheHandler.getEntity(CmsMsgSubmitClassifyEO.class, eo.getClassifyId());
        if(null != cEO) {
            eo.setClassifyName(cEO.getName());
            eo.setColumnId(cEO.getColumnId());
            if(null != cEO.getColumnId()) {
                eo.setColumnName(ColumnUtil.getColumnName(cEO.getColumnId(), cEO.getcSiteId()));
            }
        }

        model.addObject("eo",eo);
        return model;
    }

    @RequestMapping("/employHistory")
    public ModelAndView employHistory(Long id) {
        ModelAndView model = new ModelAndView(FILE_BASE + "/employ/employ_history");
        List<CmsMsgToColumnEO> list = msgToColumnService.getEOsByMsgId(id);
        for(CmsMsgToColumnEO toEo:list) {
            toEo.setEmployDate(formatDate(toEo.getCreateDate()));
        }
        model.addObject("list", list);
        return model;
    }

    @RequestMapping("/backMsg")
    public String backMsg() {
        return FILE_BASE + "/employ/back_msg";
    }

    @ResponseBody
    @RequestMapping("/batchEmploy")
    public Object batchEmploy(EmployParamVo vo) {
        String returnStr = msgToColumnService.batchEmploy(vo);
        if(!StringUtils.isEmpty(returnStr)) {
            MessageSenderUtil.publishCopyNews(returnStr);
        }
        return ResponseData.success("采用成功!");
    }

    @ResponseBody
    @RequestMapping("/saveBackMsg")
    public Object saveBackMsg(Long id,String backReason) {
        CmsMsgSubmitEO eo = msgSubmitService.getEntity(CmsMsgSubmitEO.class,id);
        eo.setStatus(1);
        eo.setBackReason(backReason);
        msgSubmitService.updateEntity(eo);
        return ajaxOk();
    }

    @RequestMapping("/exportMsg")
    public void exportMsg(HttpServletRequest request, HttpServletResponse response,String keys,String keyValue,
                          String status,String siteId,String classifyId,String startDate,String endDate) {
        String[] titles = new String[]{"标题","所属分类","供稿人","发布日期","状态"};

        ParamDto dto = new ParamDto();
        dto.setKeys(keys);
        dto.setKeyValue(keyValue);
        dto.setStatus(!StringUtils.isEmpty(status)?Integer.valueOf(status):null);
        dto.setSiteId(!StringUtils.isEmpty(siteId)?Long.valueOf(siteId):null);
        dto.setClassifyId(!StringUtils.isEmpty(classifyId)?Long.valueOf(classifyId):null);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        List<CmsMsgSubmitEO> list = msgSubmitService.getEOs(dto);
        List<Object> datas = new ArrayList<Object>();
        if(null != list) {
            for(CmsMsgSubmitEO eo : list) {
                ExportEO exportEO = new ExportEO();
                exportEO.setTitle(eo.getName());
                exportEO.setProvider(eo.getProvider());
                exportEO.setPublishDate(eo.getPublishDate());
                exportEO.setStatus(getStatus(eo.getStatus()));
                CmsMsgSubmitClassifyEO cEO = CacheHandler.getEntity(CmsMsgSubmitClassifyEO.class, eo.getClassifyId());
                if(null != cEO) {
                    exportEO.setClassify(cEO.getName());
                }
                datas.add(exportEO);
            }
        }

        String name = "信息采编(" + dateFormat(new Date()) + ")";
        try {
            String suffic = "xls";
            ExportExcel.exportExcel(name, suffic, titles, datas, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStatus(int status) {
        switch(status) {
            case 0: return "未采用";

            case 1: return "已退回";

            default: return "已采用";
        }
    }

    private String dateFormat(Date date) {
        SimpleDateFormat time = new SimpleDateFormat("yyyy.MM.dd");
        return time.format(date);
    }

    private String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date.getTime());
    }
}
