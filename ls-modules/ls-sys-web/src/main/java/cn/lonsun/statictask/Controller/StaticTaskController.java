package cn.lonsun.statictask.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.AjaxObj;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.service.IUserService;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;
import cn.lonsun.statictask.internal.service.IStaticTaskService;
import cn.lonsun.util.LoginPersonUtil;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-3-7<br/>
 */

@Controller
@RequestMapping(value = "staticTask")
public class StaticTaskController extends BaseController {

    @Autowired
    private IStaticTaskService taskService;

    @Autowired
    private IUserService userService;

    @RequestMapping("getPage")
    @ResponseBody
    public Pagination getPage(Long pageIndex, Integer pageSize) {
        if (pageIndex == null) {
            pageIndex = 0L;
        }
        if (pageSize == null) {
            pageSize = 15;
        }
        Long userId = LoginPersonUtil.getUserId();
        if (null == userId) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "用户ID为空,请先登录");
        }
        Pagination page = taskService.getPage(pageIndex, pageSize, userId);
        return page;
    }

    @RequestMapping("deleteTask")
    @ResponseBody
    public Object deleteTask(Long id) {
        if (null == id) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误");
        }
        taskService.delete(StaticTaskEO.class, id);
        return getObject();
    }

    @RequestMapping("clearTask")
    @ResponseBody
    public Object clearTask() {
        List<StaticTaskEO> list = taskService.clearTask();
        return getObject(list);
    }

    @RequestMapping("checkTask")
    @ResponseBody
    public Object checkTask(Long siteId, Long columnID, Long scope, Long source) {
        List<StaticTaskEO> eo = taskService.checkTask(siteId, columnID, scope, source);

        Integer status = 1;
        if (eo == null || eo.size() == 0) {
            return 0;
        }

        UserEO userEO = userService.getEntity(PersonEO.class, eo.get(0).getCreateUserId());
        String desc = "不能创建重复的任务，此任务已存在！";
        if (!AppUtil.isEmpty(userEO)) {
            desc = "不能创建重复的任务，用户[" + userEO.getPersonName() + "]正在运行该任务！";
        }
        return new AjaxObj(status, desc, "");

    }

}
