package cn.lonsun.msg.submit.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitEO;
import cn.lonsun.msg.submit.entity.CmsMsgToColumnEO;
import cn.lonsun.msg.submit.service.IMsgSubmitService;
import cn.lonsun.msg.submit.service.IMsgToColumnService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-26 9:57
 */
@Controller
@RequestMapping("/msg/submit")
public class MsgSubmitController extends BaseController {

    private static final String FILE_BASE = "/msg/submit";

    @Autowired
    private IMsgSubmitService msgSubmitService;

    @Autowired
    private IMsgToColumnService msgToColumnService;

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/index/index";
    }

    @RequestMapping("/msgDetail")
    public ModelAndView msgDetail(Long id) {
        ModelAndView model = new ModelAndView(FILE_BASE + "/index/msg_detail");
        CmsMsgSubmitEO eo = msgSubmitService.getEntity(CmsMsgSubmitEO.class, id);
        List<CmsMsgToColumnEO> list = msgToColumnService.getEOsByMsgId(id);
        for(CmsMsgToColumnEO toEo:list) {
            toEo.setEmployDate(formatDate(toEo.getCreateDate()));
        }
        model.addObject("eo",eo);
        model.addObject("list",list);
        return model;
    }

    @RequestMapping("/addOrEdit")
    public ModelAndView addOrEdit() {
        ModelAndView model = new ModelAndView(FILE_BASE + "/index/edit");
        return model;
    }

    @ResponseBody
    @RequestMapping("/getPageEOs")
    public Object getPageEOs(ParamDto dto) {
        return msgSubmitService.getPageEOs(dto);
    }

    @ResponseBody
    @RequestMapping("/save")
    public Object saveEO(CmsMsgSubmitEO eo) {
        eo.setCreateUnitId(LoginPersonUtil.getUnitId());
        eo.setCreateUnitName(LoginPersonUtil.getUnitName());
        msgSubmitService.saveEO(eo);
        return ResponseData.success("保存成功!");
    }

    @ResponseBody
    @RequestMapping("/update")
    public Object updateEO(CmsMsgSubmitEO eo) {
        CmsMsgSubmitEO eos = msgSubmitService.getEntity(CmsMsgSubmitEO.class,eo.getId());

        if(eos.getUseCount() > 0) {
            return ResponseData.fail("报送信息已被采用!");
        } else {
            eos.setName(eo.getName());
            eos.setClassifyId(eo.getClassifyId());
            eos.setProvider(eo.getProvider());
            eos.setAuthor(eo.getAuthor());
            eos.setFromCode(eo.getFromCode());
            eos.setImageLink(eo.getImageLink());
            eos.setStatus(eo.getStatus());
            eos.setBackReason(eo.getBackReason());
            eos.setPublishDate(eo.getPublishDate());
            eos.setContent(eo.getContent());
            msgSubmitService.updateEntity(eos);
        }
        return ResponseData.success("更新成功!");
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Object deleteEO(String ids) {
        Long[] idsn = StringUtils.getArrayWithLong(ids, ",");
        for(Long id : idsn) {
            CmsMsgSubmitEO eos = msgSubmitService.getEntity(CmsMsgSubmitEO.class,id);
            if(eos.getUseCount() > 0) {
                return ResponseData.fail("报送信息已被采用!");
            } else {
                msgSubmitService.delete(CmsMsgSubmitEO.class, id);
            }
        }
        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping("/msgFrom")
    public Object msgFrom() {
        List<DataDictVO> itemEOs = DataDictionaryUtil.getDDList("sourceMgr");
        return itemEOs;
    }

    private String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date.getTime());
    }
}
