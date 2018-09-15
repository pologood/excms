package cn.lonsun.process.controller;

import cn.lonsun.base.DealStatus;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.process.entity.ProcessFormEO;
import cn.lonsun.process.service.IProcessFormService;
import cn.lonsun.process.vo.ProcessFormQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by zhu124866 on 2015-12-23.
 */
@Controller
@RequestMapping(value = "processForm", produces = {"application/json;charset=UTF-8"})
public class ProcessFormController extends BaseController {

    @Autowired
    private IProcessFormService processFormService;


    /**
     * 事项监督
     * @return
     */
    @RequestMapping("supervisionList")
    public ModelAndView supervisionList(Model model,String moduleCode,Long indicatorId) {
        model.addAttribute("moduleCode", moduleCode);
        return new ModelAndView("/process/form_supervision_list");
    }


    /**
     * 获取事项监督列表
     * @return
     */
    @RequestMapping("getSupervisionPagination")
    @ResponseBody
    public Object getSupervisionPagination(ProcessFormQueryVO queryVO){
        if (AppUtil.isEmpty(queryVO.getPageIndex())){
            queryVO.setPageIndex(0L);
        }
        if (AppUtil.isEmpty(queryVO.getPageSize())) {
            queryVO.setPageSize(15);
        }
        queryVO.setFormStatus(new Integer[]{DealStatus.Dealing.getValue(),
                DealStatus.Completed.getValue(),
                DealStatus.Terminate.getValue()});
        queryVO.setUnitId(ContextHolderUtils.getUnitId());
        Pagination page = processFormService.getPagination(queryVO);
        return getObject(page);
    }




}
