package cn.lonsun.content.onlinepetition;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.onlinePetition.internal.service.IRunRecordService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-30<br/>
 */
@Controller
@RequestMapping("runRecord")
public class RunRecordController extends BaseController {

    @Autowired
    private IRunRecordService recordService;


    @RequestMapping("openRecord")
    public String openRecord(Long petitionId,Long columnId,Model model){
        if(petitionId==null||columnId==null){
            return "";
        }else{
            model.addAttribute("petitionId",petitionId);
            model.addAttribute("columnId",columnId);
            return "content/onlinepetition/record_page";
        }
    }

    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(PageQueryVO pageVO,Long petitionId){
        Pagination page=recordService.getPage(pageVO,petitionId);
        return page;
    }
}
