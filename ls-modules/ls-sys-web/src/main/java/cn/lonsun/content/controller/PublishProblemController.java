package cn.lonsun.content.controller;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.publishproblem.internal.service.IPublishProblemService;
import cn.lonsun.publishproblem.vo.PublishProblemVO;
import cn.lonsun.publishproblem.vo.PulishProblemQueryVO;
import cn.lonsun.site.template.util.ResponseData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 发布中列表控制器
 * Created by huangxx on 2017/9/1.
 */
@Controller
@RequestMapping(value = "publishProblem")
public class PublishProblemController extends BaseController {

    private static final String FILE_BASE = "/content/publishproblem";

    @Resource
    private IPublishProblemService publishProblemService;

    @RequestMapping("index")
    public String guide(ModelMap map) {
        return FILE_BASE + "/index";
    }

    /**
     * 获取发布有问题的信息列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("getPage")
    public Object getPage(PulishProblemQueryVO vo) {
        vo.setIsPublish(Integer.valueOf(2));
        return publishProblemService.getPage(vo);
    }

    /**
     * 发布信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("publish")
    public Object publish(PublishProblemVO vo) {
        String msg = (null != vo.getIsPublish() && vo.getIsPublish().intValue() == 1)?"发布":"取消发布";
        if(null == vo.getIds()) {
            return ResponseData.success(msg + "失败!");
        }
        Long[] idsl = StringUtils.getArrayWithLong(vo.getIds(), ",");

        if(idsl.length <= 0) {
            return ResponseData.success(msg + "失败!");
        }

        String returnStr = publishProblemService.publish(idsl, vo.getIsPublish());
        if(!StringUtils.isEmpty(returnStr)) {
            if(vo.getIsPublish() != null && vo.getIsPublish().intValue() == 1) {
                MessageSenderUtil.publishCopyNews(returnStr);
            }
            else {
                MessageSenderUtil.unPublishCopyNews(returnStr);
            }
        }
        return ResponseData.success(msg + "成功!");
    }
}
