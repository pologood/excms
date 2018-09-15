package cn.lonsun.net.service.controller;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author gu.fei
 * @version 2016-10-06 16:16
 */
@Controller
@RequestMapping(value = "handleItems")
public class HandleItemsController extends BaseController {

    private static final String FILE_BASE = "/content/articlenews/";

    @RequestMapping("/index")
    public String index(Long pageIndex, ModelMap map) {
        if (pageIndex == null)
            pageIndex = 0L;
        map.put("pageIndex", pageIndex);
        map.put("typeCode", BaseContentEO.TypeCode.handleItems.toString());
        return FILE_BASE + "article_news_list";
    }
}
