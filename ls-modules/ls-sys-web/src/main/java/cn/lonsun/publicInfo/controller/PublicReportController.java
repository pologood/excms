package cn.lonsun.publicInfo.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.publicInfo.internal.entity.PublicReportEO;
import cn.lonsun.publicInfo.internal.service.IPublicReportService;
import cn.lonsun.publicInfo.vo.PublicReportVO;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by fth on 2017/1/19.
 */
@Controller
@RequestMapping("/public/report")
public class PublicReportController extends BaseController {

    @Resource
    private IPublicReportService publicReportService;

    @RequestMapping("index")
    public String index(ModelMap map) {
        return "/public/report/index";
    }

    /**
     * 新增编辑
     *
     * @param publicReportEO
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("saveOrUpdate")
    public Object saveOrUpdate(PublicReportEO publicReportEO) {
        if (StringUtils.isEmpty(publicReportEO.getTitle())) {
            return ajaxErr("统计指标不能为空！");
        }
        if (null == publicReportEO.getSortNum()) {
            return ajaxErr("排序号不能为空！");
        }
        publicReportService.saveEntity(publicReportEO);
        return getObject(publicReportEO);
    }

    /**
     * 获取当前站点下的目录列表
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getPublicReportTjList")
    public Object getPublicReportTjList(PublicReportVO publicReportVO) {
        return publicReportService.getPublicReportTjList(publicReportVO);
    }

    /**
     * 导出
     *
     * @param publicReportVO
     * @param response
     */
    @RequestMapping("exportPublicReportList")
    public void exportPublicReportList(PublicReportVO publicReportVO, HttpServletResponse response) {
        publicReportService.exportPublicReportList(publicReportVO, response);
    }

    /**
     * 获取当前站点下的目录列表
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getPublicReportList")
    public Object getPublicReportList() {
        List<PublicReportEO> reportList = publicReportService.getPublicReportList();
        return getObject(reportList);
    }

    /**
     * 获取对象
     *
     * @param id
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getPublicReport")
    public Object getPublicReport(Long id) {
        PublicReportEO publicReportEO = null;
        if (null != id) {
            publicReportEO = publicReportService.getEntity(PublicReportEO.class, id);
        } else {
            publicReportEO = new PublicReportEO();
            publicReportEO.setSiteId(LoginPersonUtil.getSiteId());
        }
        return getObject(publicReportEO);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("delete")
    public Object delete(Long id) {
        publicReportService.delete(PublicReportEO.class, id);
        return getObject();
    }
}