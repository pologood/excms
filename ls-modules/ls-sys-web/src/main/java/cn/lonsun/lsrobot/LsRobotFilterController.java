package cn.lonsun.lsrobot;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lsrobot.entity.LsRobotFilterEO;
import cn.lonsun.lsrobot.service.ILsRobotFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/lsrobot/filter")
public class LsRobotFilterController extends BaseController {

    private static final String FILE_BASE = "/lsrobot/filter";

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ILsRobotFilterService lsRobotFilterService;

    /**
     * 过滤界面
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String filter() {
        return FILE_BASE + "/list";
    }

    /**
     * 新增界面
     * @return
     */
    @RequestMapping(value = "/adder")
    public String filterAdder() {
        return FILE_BASE + "/add";
    }

    @ResponseBody
    @RequestMapping("getPage")
    public Object getPage(HttpServletRequest request, PageQueryVO pageInfo){
        Map<String, Object> param = AppUtil.parseRequestToMap(request);
        Pagination page = lsRobotFilterService.getPage(pageInfo, param);
        return getObject(page);
    }

    /** 新增
     * @author zhongjun
     * @createtime 2017-11-06 10:06:44
     */
    @ResponseBody
    @RequestMapping("/save")
    public Object save(LsRobotFilterEO eo){
        eo.setCreateUserName(ContextHolderUtils.getPersonName());
        eo.setCreateUserId(ContextHolderUtils.getUserId());
        lsRobotFilterService.saveEntity(eo);
        return getObject();
    }

    @ResponseBody
    @RequestMapping(value = "/remove")
    public Object deleteRobotSources(@RequestParam(value="ids[]",required=false) Long[] ids) {
        if(ids == null || ids.length == 0){
            return ajaxErr("请至少选择一条记录进行删除操作");
        }
        try{
            lsRobotFilterService.delete(LsRobotFilterEO.class, ids);
        }catch (Exception e){
            e.printStackTrace();
            log.error("批量删除失败：{}", e.getCause());
            return ajaxErr("删除失败");
        }
        return ajaxOk();
    }
}
