package cn.lonsun.content.onlineDeclaration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.onlineDeclaration.internal.service.IDeclarationRecordService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-13<br/>
 */
@Controller
@RequestMapping("declarationRecord")
public class DeclarationRecordController extends BaseController{

    @Autowired
    private IDeclarationRecordService recordService;


    @RequestMapping("openRecord")
    public String openRecord(Long declarationId,Model model){
        if(declarationId==null){
            return "";
        }else{
            model.addAttribute("declarationId",declarationId);
            return "content/onlinedeclaration/record";
        }
    }

    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(PageQueryVO pageVO, Long declarationId){
        Pagination page=recordService.getPage(pageVO,declarationId);
        return page;
    }
}
