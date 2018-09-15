package cn.lonsun.net.service.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsTableResourcesEO;
import cn.lonsun.net.service.entity.dto.MapVO;
import cn.lonsun.net.service.service.IResourcesClassifyService;
import cn.lonsun.net.service.service.ITableResourcesService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.site.vo.ColumnVO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.util.LoginPersonUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-11-19 15:27
 */
@Controller
@RequestMapping("tableResources")
public class TableResourcesController extends BaseController {

    private static final String FILE_BASE = "/net/service";

    @Autowired
    private ITableResourcesService tableResourcesService;

    @Autowired
    private IResourcesClassifyService resourcesClassifyService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IOrganService organService;

    @RequestMapping("/index")
    public String index(ModelMap map) {
        map.put("typeCode", BaseContentEO.TypeCode.workGuide.toString() + "," + BaseContentEO.TypeCode.sceneService.toString());
        return FILE_BASE + "/table/index";
    }

    @RequestMapping("/addOrEdit")
    public ModelAndView addOrEdit() {
        ModelAndView model = new ModelAndView(FILE_BASE + "/table/table_edit");
        return model;
    }

    @RequestMapping("/addNew")
    public ModelAndView addNew() {
        ModelAndView model = new ModelAndView(FILE_BASE + "/table/table_edit01");
        model.addObject("typeCode",BaseContentEO.TypeCode.workGuide.toString() + "," + BaseContentEO.TypeCode.sceneService.toString());
        return model;
    }

    @RequestMapping("/classifySelect")
    public String classifySelect() {
        return FILE_BASE + "/rule/index";
    }

    @ResponseBody
    @RequestMapping("/getPageEOs")
    public Object getPageEOs(String classify,ParamDto dto) {
        if(classify != null) {
            JSONArray json = JSONArray.fromObject(classify);
            List<MapVO> vos = (List<MapVO>)JSONArray.toCollection(json,MapVO.class);
            if(vos.size() > 0) {
                dto.setMapVOs(vos);
            }
        }
        Pagination page = tableResourcesService.getPageEOs(dto);
        List<CmsTableResourcesEO> list = (List<CmsTableResourcesEO>) page.getData();

        for(CmsTableResourcesEO eo : list) {
            if(eo.getOrganId() != null) {
                OrganEO organEO = CacheHandler.getEntity(OrganEO.class,eo.getOrganId());
                if(organEO != null) {
                    eo.setOrganName(organEO.getName());
                }
            }
        }

        return page;
    }

    @ResponseBody
    @RequestMapping("/save")
    public Object saveEO(CmsTableResourcesEO eo, String cIds) {
        tableResourcesService.saveEO(eo, cIds);
        return ResponseData.success("保存成功!");
    }

    @ResponseBody
    @RequestMapping("/update")
    public Object updateEO(CmsTableResourcesEO eo, String cIds) {
       tableResourcesService.updateEO(eo, cIds);
        return ResponseData.success("更新成功!");
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Object deleteEO(Long id) {
        tableResourcesService.deleteEO(id);
        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping("/getClassifyEOs")
    public Object getClassifyEOs(Long pId,Long cIds) {

        Map<Long,Object> map = null;
        if(pId != null) {
            map = resourcesClassifyService.getMap(pId);
        }

        Long siteId = LoginPersonUtil.getSiteId();
        List<ColumnMgrEO> list = columnConfigService.getColumnByTypeCode(siteId, "workGuide");

        if(map != null && map.size() > 0) {
            for(ColumnMgrEO eo : list) {
                if(AppUtil.isEmpty(map.get(eo.getIndicatorId()))) {
                    eo.setChecked(true);
                }
            }
        }

        return list == null?new ArrayList<ColumnVO>() : list;
    }

    @ResponseBody
    @RequestMapping("/getClassifyEOsByCIds")
    public Object getClassifyEOsByCIds(String cIds) {

        Long siteId = LoginPersonUtil.getSiteId();
        List<ColumnMgrEO> list = columnConfigService.getColumnByTypeCode(siteId, "workGuide");

        if(!AppUtil.isEmpty(cIds)) {
            List<Long> cIdList = StringUtils.getListWithLong(cIds,",");
            for(ColumnMgrEO eo : list) {
                if(cIdList.contains(eo.getIndicatorId())) {
                    eo.setChecked(true);
                }
            }
        }

        return list == null?new ArrayList<ColumnVO>() : list;
    }

    @ResponseBody
    @RequestMapping("/getCheckedClassifyEOs")
    public Object getCheckedClassifyEOs(Long pId) {
        Map<Long,Object> map = null;
        if(pId != null) {
            map = resourcesClassifyService.getMap(pId);
        }

        Long siteId = LoginPersonUtil.getSiteId();
        List<ColumnMgrEO> list = columnConfigService.getColumnByTypeCode(siteId, "workGuide");
        List<ColumnMgrEO> temp = new ArrayList<ColumnMgrEO>();
        if(map != null && map.size() > 0) {
            for(ColumnMgrEO eo : list) {
                if(!AppUtil.isEmpty(map.get(eo.getIndicatorId()))) {
                    temp.add(eo);
                }
            }
        }

        return getObject(temp);
    }


    @ResponseBody
    @RequestMapping("/getOrgans")
    public Object getOrgans() {
        boolean flag = LoginPersonUtil.isRoot() || LoginPersonUtil.isSuperAdmin();
        if(flag) {
            Long siteId = LoginPersonUtil.getSiteId();
            return organService.getOrgansByType(siteId,null);
        }
        return organService.getOrgans(LoginPersonUtil.getUnitId(), OrganEO.Type.OrganUnit.toString());
    }

}
