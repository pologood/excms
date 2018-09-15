package cn.lonsun.net.service.controller;

import cn.lonsun.core.base.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.lonsun.content.internal.entity.BaseContentEO;

/**
 * @author gu.fei
 * @version 2015-11-25 9:56
 */
@Controller
@RequestMapping("sceneService")
public class SceneServiceController extends BaseController {

    private static final String FILE_BASE = "/net/service";

    @RequestMapping("/index")
    public String index(ModelMap map) {
        map.put("typeCode", BaseContentEO.TypeCode.workGuide.toString() + "," + BaseContentEO.TypeCode.sceneService.toString());
        return FILE_BASE + "/guide/index";
    }

}
