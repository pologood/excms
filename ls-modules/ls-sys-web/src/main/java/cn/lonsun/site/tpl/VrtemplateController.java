package cn.lonsun.site.tpl;

import cn.lonsun.core.base.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author gu.fei
 * @version 2016-5-10 17:51
 */
@Controller
@RequestMapping("/vrtpl")
public class VrtemplateController extends BaseController {

    private static final String FILE_BASE = "site/vrtpl/";

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/index";
    }

    @RequestMapping("/addOrEdit")
    public ModelAndView addOrEdit() {
        ModelAndView model = new ModelAndView(FILE_BASE + "/tpl_edit");
        return model;
    }

    @RequestMapping("/uploadTpl")
    public ModelAndView uploadTpl(String id) {
        ModelAndView model = new ModelAndView(FILE_BASE + "/tpl_upload");
        model.addObject("id",id);
        return model;
    }

    @RequestMapping("/history")
    public ModelAndView history(String tempId) {
        ModelAndView model = new ModelAndView(FILE_BASE + "/tpl_history");
        model.addObject("tempId",tempId);
        return model;
    }

    @RequestMapping("/addLabel")
    public ModelAndView addLabel() {
        ModelAndView model = new ModelAndView(FILE_BASE + "/add_label");
        return model;
    }
}
