package cn.lonsun.content.controller;

import cn.lonsun.content.optrecord.service.IContentOptRecordService;
import cn.lonsun.content.optrecord.vo.OptRecordQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author gu.fei
 * @version 2018-01-18 15:59
 */
@Controller
@RequestMapping(value = "/content/opt/record")
public class ContentOptRecordController extends BaseController {

    @Resource
    private IContentOptRecordService contentOptRecordService;

    @RequestMapping(value = "index")
    public String index() {
        return "/content/optrecord/index";
    }

    /**
     * 分页获取
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getPage")
    public Object getPage(OptRecordQueryVO vo) {
        return contentOptRecordService.getPage(vo);
    }
}
