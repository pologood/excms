package cn.lonsun.publicInfo.controller;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogUpdateService;
import cn.lonsun.publicInfo.vo.OrganVO;
import cn.lonsun.publicInfo.vo.PublicCatalogUpdateQueryVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.PublicCatalogUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fth on 2017/6/9.
 */
@Controller
@RequestMapping("/public/catalogUpdate")
public class PublicCatalogUpdateController extends BaseController {

    @Resource
    private IOrganService organService;
    @Resource
    private IPublicCatalogUpdateService publicCatalogUpdateService;

    /**
     * 转向公共目录首页
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("index")
    public String index(ModelMap modelMap) {
        Long siteId = LoginPersonUtil.getSiteId();
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        List<OrganVO> organVOList = new ArrayList<OrganVO>();
        PublicCatalogUtil.filterOrgan(organList, organVOList);// 过滤单位列表
        PublicCatalogUtil.sortOrgan(organVOList);// 排序
        modelMap.put("organList", organVOList);
        return "public/update/index";
    }

    /**
     * 获取分页
     *
     * @param queryVO
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getPage")
    public Object getPage(PublicCatalogUpdateQueryVO queryVO) {
        return publicCatalogUpdateService.getPagination(queryVO);
    }

    /**
     * 根据单位id获取该单位下的空栏目
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getEmptyCatalogByOrganId")
    public Object getEmptyCatalogByOrganId(Long organId) {
        if (null == organId || organId <= 0L) {
            organId = LoginPersonUtil.getUnitId();//当前登录用户所在单位
        }
        List<PublicCatalogEO> catalogList = publicCatalogUpdateService.getEmptyCatalogByOrganId(organId);
        return getObject(catalogList);
    }

    /**
     * 导出
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("exportEmptyCatalog")
    public void exportEmptyCatalog(Long organId, HttpServletResponse response) {
        if (null == organId || organId <= 0L) {
            organId = LoginPersonUtil.getUnitId();//当前登录用户所在单位
        }
        publicCatalogUpdateService.exportEmptyCatalog(organId, response);
    }

    /**
     * 获取当前用户所在部门的条数，当为超级管理员或者站点管理员时，查看全部
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getCountForOrganId")
    public Object getCountForOrganId() {
        PublicCatalogUpdateQueryVO queryVO = new PublicCatalogUpdateQueryVO();
        if (!LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
            queryVO.setOrganId(LoginPersonUtil.getUnitId());
        }
        return getObject(publicCatalogUpdateService.getCountByOrganId(queryVO));
    }

    /**
     * 导出
     *
     * @param queryVO
     * @param response
     */
    @RequestMapping("export")
    public void export(PublicCatalogUpdateQueryVO queryVO, HttpServletResponse response) {
        publicCatalogUpdateService.export(queryVO, response);
    }
}