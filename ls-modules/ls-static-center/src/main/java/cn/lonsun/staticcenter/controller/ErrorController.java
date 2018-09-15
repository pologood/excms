package cn.lonsun.staticcenter.controller;

import cn.lonsun.core.base.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 系统错误控制器
 * Created by zsy on 2016-9-26.
 */
@Controller
@RequestMapping("/error")
public class ErrorController extends BaseController {

    @RequestMapping("500")
    public String error500() {
        return "/error/500";
    }

    @RequestMapping("401")
    public String error401() {
        return "/error/401";
    }

    @RequestMapping("403")
    public String error403() {
        return "/error/403";
    }

    @RequestMapping("errorTest")
    public String errorTest() {
        return "";
    }
}
