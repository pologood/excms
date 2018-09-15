package cn.lonsun.staticcenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.onlinePetition.internal.service.IOnlinePetitionService;
import cn.lonsun.content.onlinePetition.vo.OnlinePetitionVO;
import cn.lonsun.core.base.controller.BaseController;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-5-21<br/>
 */

@Controller
@RequestMapping(value = "/frontPetition")
public class FrontPetitionController extends BaseController {

    @Autowired
    private IOnlinePetitionService petitionService;

    @RequestMapping("saveVO")
    @ResponseBody
    public Object  saveVO(OnlinePetitionVO vo){
        if(AppUtil.isEmpty(vo.getRecUnitId())){
            return ajaxErr("收信单位不能为空");
        }
        if(AppUtil.isEmpty(vo.getPurposeCode())){
            return ajaxErr("信件目的不能为空");
        }
        if(AppUtil.isEmpty(vo.getCategoryCode())){
            return ajaxErr("信件类型不能为空");
        }
        if (AppUtil.isEmpty(vo.getTitle())) {
            return ajaxErr("标题不能为空");
        }
        if(AppUtil.isEmpty(vo.getAuthor())){
            return ajaxErr("您的姓名不能为空");
        }
        if(AppUtil.isEmpty(vo.getPhoneNum())){
            return ajaxErr("联系方式不能为空");
        }
        if(AppUtil.isEmpty(vo.getContent())){
            return ajaxErr("内容不能为空");
        }
        petitionService.saveVO(vo);
        return getObject("0");

    }

}
