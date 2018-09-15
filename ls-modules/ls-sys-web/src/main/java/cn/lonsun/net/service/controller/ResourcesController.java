package cn.lonsun.net.service.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.net.service.service.IResourcesClassifyService;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
@RequestMapping("resources")
public class ResourcesController extends BaseController {

    private static final String FILE_BASE = "/net/service";

    @Autowired
    private IResourcesClassifyService resourcesClassifyService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @RequestMapping("/classifySelect")
    public ModelAndView classifySelect() {
        ModelAndView model = new ModelAndView(FILE_BASE + "/classify/classify_select");
        return model;
    }

    @ResponseBody
    @RequestMapping("/getClassifyEOs")
    public Object getClassifyEOs(Long pId) {

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

        return list == null?new ArrayList<ColumnMgrEO>() : list;
    }

    @ResponseBody
    @RequestMapping("/getClassifyEOsByCIds")
    public Object getClassifyEOsByCIds(String cIds,String typeCode) {

        Long siteId = LoginPersonUtil.getSiteId();
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();
        List<String> typeCodes = StringUtils.getListWithString(typeCode, ",");
        if(typeCodes != null) {
            for(String code : typeCodes) {
                list.addAll(columnConfigService.getColumnByTypeCode(siteId, code));
            }
        }

        if(!AppUtil.isEmpty(cIds)) {
            List<Long> cIdList = StringUtils.getListWithLong(cIds,",");
            for(ColumnMgrEO eo : list) {
                if(cIdList.contains(eo.getIndicatorId())) {
                    eo.setChecked(true);
                }
            }
        }

        return list == null?new ArrayList<ColumnMgrEO>() : list;
    }

    @ResponseBody
    @RequestMapping("/getCheckedClassifyEOs")
    public Object getCheckedClassifyEOs(Long pId,String typeCode) {
        Map<Long,Object> map = null;
        if(pId != null) {
            map = resourcesClassifyService.getMap(pId);
        }

        Long siteId = LoginPersonUtil.getSiteId();
//        List<ColumnMgrEO> list = columnConfigService.getColumnByModelCode(siteId, typeCode);


        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();
        List<String> typeCodes = StringUtils.getListWithString(typeCode, ",");
        if(typeCodes != null) {
            for(String code : typeCodes) {
                list.addAll(columnConfigService.getColumnByTypeCode(siteId,code));
            }
        }

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
    public Object getOrgans(Long columnId) {
        List<ContentModelParaVO> list = ModelConfigUtil.getParam(columnId,LoginPersonUtil.getSiteId(),null);
        List<ContentModelParaVO> vos = new ArrayList<ContentModelParaVO>();
        if(!LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
            for(ContentModelParaVO vo : list) {
                if(null != vo.getRecUnitId() && null != LoginPersonUtil.getUnitId()) {
                    if(vo.getRecUnitId().intValue() == LoginPersonUtil.getUnitId().intValue()) {
                        vos.add(vo);
                        break;
                    }
                }
            }
        } else {
            vos.addAll(list);
        }
        return vos;
    }

}
