package cn.lonsun.content.controller;

import cn.lonsun.content.internal.service.IBaseContentUpdateService;
import cn.lonsun.content.vo.BaseContentUpdateQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liuk on 2017/6/30.
 */
@Controller
@RequestMapping("/content/contentUpdate")
public class BaseContentUpdateController extends BaseController {

    @Resource
    private IBaseContentUpdateService baseContentUpdateService;

    /**
     * 转向栏目首页
     *
     * @return
     * @author liuk
     */
    @RequestMapping("index")
    public String index(ModelMap modelMap) {

        return "content/update/index";
    }

    /**
     * 获取分页
     *
     * @param queryVO
     * @return
     * @author liuk
     */
    @ResponseBody
    @RequestMapping("getPage")
    public Object getPage(BaseContentUpdateQueryVO queryVO) {
        queryVO.setSiteId(LoginPersonUtil.getSiteId());
        if (!LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
            queryVO.setUserId(LoginPersonUtil.getUserId());
        }
        return baseContentUpdateService.getPagination(queryVO);
    }


    /**
     * 获取当前用户所在部门的条数，当为超级管理员或者站点管理员时，查看全部
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getCountByColumnId")
    public Object getCountByColumnId() {
        BaseContentUpdateQueryVO queryVO = new BaseContentUpdateQueryVO();
        queryVO.setSiteId(LoginPersonUtil.getSiteId());
        if (!LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
            queryVO.setUserId(LoginPersonUtil.getUserId());
        }
        return getObject(baseContentUpdateService.getCountByColumnId(queryVO));
    }


    /**
     * 导出
     *
     * @param queryVO
     * @param response
     */
    @RequestMapping("export")
    public void export(BaseContentUpdateQueryVO queryVO, HttpServletResponse response) {
        queryVO.setSiteId(LoginPersonUtil.getSiteId());
        if (!LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
            queryVO.setUserId(LoginPersonUtil.getUserId());
        }
        baseContentUpdateService.export(queryVO, response);
    }
}