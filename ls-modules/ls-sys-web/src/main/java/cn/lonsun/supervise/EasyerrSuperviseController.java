package cn.lonsun.supervise;

import cn.lonsun.core.base.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author gu.fei
 * @version 2016-4-5 10:00
 */
@Controller
@RequestMapping("/easyerr/supervise")
public class EasyerrSuperviseController extends BaseController {

    private static final String FILE_BASE = "/supervise/easyerr/";

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/index";
    }

}
