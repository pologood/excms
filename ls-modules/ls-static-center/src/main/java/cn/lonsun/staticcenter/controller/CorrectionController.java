package cn.lonsun.staticcenter.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.content.contentcorrection.internal.entity.ContentCorrectionEO;
import cn.lonsun.content.contentcorrection.internal.service.IContentCorrectionService;
import cn.lonsun.core.base.controller.BaseController;

/**
 * @author gu.fei
 * @version 2016-5-24 10:18
 */
@Controller
@RequestMapping(value = "/correction")
public class CorrectionController extends BaseController {

    @Autowired
    private IContentCorrectionService contentCorrectionService;

    @ResponseBody
    @RequestMapping("/saveData")
    public Object saveData(HttpServletRequest request,ContentCorrectionEO eo,String checkCode) {
        if(StringUtils.isEmpty(checkCode)){
            return ajaxErr("验证码不能为空！");
        }
        Object obj = request.getSession().getAttribute("webCode");
        if(obj == null){
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String)obj;
        if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
            return ajaxErr("验证码不正确，请重新输入");
        }

        contentCorrectionService.saveEntity(eo);
        return ajaxOk();
    }
}
