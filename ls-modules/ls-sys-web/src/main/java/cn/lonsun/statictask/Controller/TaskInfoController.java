package cn.lonsun.statictask.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.statictask.internal.service.ITaskInfoService;
import cn.lonsun.statictask.internal.vo.TaskInfoQueryVO;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-3-11<br/>
 */
@Controller
@RequestMapping(value = "taskInfo")
public class TaskInfoController extends BaseController {
    @Autowired
    private ITaskInfoService infoService;

    @RequestMapping("index")
    public String page(Long taskId, ModelMap map) {
        map.put("taskId", taskId);
        return "/static/err_list";
    }

    @ResponseBody
    @RequestMapping("getByTaskId")
    public Object getByTaskId(TaskInfoQueryVO queryVO) {
        return infoService.getPagination(queryVO);
    }
}