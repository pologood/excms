package cn.lonsun.resourceMonitor.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.resourceMonitor.internal.entity.ResourceMonitorEO;
import cn.lonsun.resourceMonitor.internal.service.IResourceMonitorService;
import cn.lonsun.resourceMonitor.vo.ResourceMonitorQueryVO;
import cn.lonsun.site.template.util.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by huangxx on 2017/4/11.
 */
@Controller
@RequestMapping(value = "/resource/monitor", produces = { "application/json;charset=UTF-8" })
public class resourceMonitorController extends BaseController{

    @Autowired
    private IResourceMonitorService resourceMonitorService;

    /**
     * 列表页面
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "/resourceMonitor/index";
    }
    /**
     * 获取分页
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(ResourceMonitorQueryVO queryVO) {
        // 页码与查询最多查询数据量纠正
        if (queryVO.getPageIndex() == null || queryVO.getPageIndex() < 0) {
            queryVO.setPageIndex(0L);
        }
        Integer size = queryVO.getPageSize();
        if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
            queryVO.setPageSize(15);
        }
        Pagination page = resourceMonitorService.getPageEntities(queryVO);
        return getObject(page);
    }

    @RequestMapping("edit")
    public String edit(Long resourceId,Model model) {
        model.addAttribute("resourceId",resourceId);
        return "/resourceMonitor/resourceEdit";
    }
    @RequestMapping("getResource")
    @ResponseBody
    public Object getResource(Long id) {
        ResourceMonitorEO resourceEO = null;

        if (null == id || id == 0) {
            resourceEO = new ResourceMonitorEO();
        } else if (null != id) {
            resourceEO = resourceMonitorService.getEntity(ResourceMonitorEO.class, id);
        }
        return getObject(resourceEO);
    }

    @RequestMapping("save")
    @ResponseBody
    public Object save(ResourceMonitorEO resourceEO) {

        if(null != resourceEO.getResourceId()) {
            resourceMonitorService.updateEntity(resourceEO);
        } else {
            check(resourceEO);
            resourceMonitorService.saveEntity(resourceEO);
        }
        return getObject();
    }

    private void check(ResourceMonitorEO resourceEO) {
        if(!AppUtil.isEmpty(resourceEO.getResourceName())) {
            Boolean isExist = resourceMonitorService.isExist(resourceEO);
            if(isExist) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "标题已经存在，请重新输入！");
            }
        }
    }

    @RequestMapping("delete")
    @ResponseBody
    public Object delete(String ids) {
        Long[] idss = StringUtils.getArrayWithLong(ids,",");
        resourceMonitorService.deleteEO(idss);
        return ResponseData.success("删除成功！");
    }

    @RequestMapping("showDetail")
    public String showDetail(Long resourceId,Model model) {
        ResourceMonitorEO EO = resourceMonitorService.getEntity(ResourceMonitorEO.class,resourceId);
        model.addAttribute("url",EO.getResourceAddress());
        return "/resourceMonitor/detail";
    }

}
