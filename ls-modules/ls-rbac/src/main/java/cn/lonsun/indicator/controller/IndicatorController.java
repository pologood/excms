package cn.lonsun.indicator.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "indicatorInit")
public class IndicatorController extends BaseController {

    @Autowired
    private IIndicatorService indicatorService;

    @ResponseBody
    @RequestMapping(method= RequestMethod.GET)
    public Object index(){
        indicatorService.initParentPath();
        return getObject("目录初始化完成！");
    }

}
