package cn.lonsun.process.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.process.service.IFormHandleRecordService;
import cn.lonsun.process.vo.ProcessFormQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 表单办理记录控制器
 * Created by zhu124866 on 2015-12-23.
 */
@Controller
@RequestMapping(value = "formHandleRecord", produces = {"application/json;charset=UTF-8"})
public class FormHandleRecordController extends BaseController {

    @Autowired
    private IFormHandleRecordService formHandleRecordService;


    /**
     * 已办列表
     * @return
     */
    @RequestMapping("formHandleRecordList")
    public ModelAndView formHandleRecordList(Model model,String moduleCode,Long indicatorId) {
        model.addAttribute("moduleCode", moduleCode);
        model.addAttribute("indicatorId", indicatorId);
        return new ModelAndView("process/form_handle_record_list");
    }

    /**
     * 获取分页列表
     * @return
     */
    @RequestMapping("getFormHandleRecordPagination")
    @ResponseBody
    public Object getFormHandleRecordPagination(ProcessFormQueryVO queryVO){
        if (AppUtil.isEmpty(queryVO.getPageIndex())){
            queryVO.setPageIndex(0L);
        }
        if (AppUtil.isEmpty(queryVO.getPageSize())) {
            queryVO.setPageSize(15);
        }
        Long userId = ContextHolderUtils.getUserId();
        Long unitId = ContextHolderUtils.getUnitId();
        queryVO.setUserId(userId);
        queryVO.setUnitId(unitId);
        return getObject(formHandleRecordService.getFormHandleRecordPagination(queryVO));
    }






}


