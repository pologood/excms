package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.GlobalConfig;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.XSSFilterUtil;
import cn.lonsun.net.service.entity.CmsGuideResRelatedEO;
import cn.lonsun.net.service.entity.CmsTableResourcesEO;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.ITableResourcesService;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-14 13:56
 */
@Component
public class OnlineNavTableResBeanService extends AbstractBeanService {

    @Autowired
    private IWorkGuideService workGuideService;

    @Autowired
    private ITableResourcesService tableResourcesService;

    private static GlobalConfig globalConfig = SpringContextHolder.getBean(GlobalConfig.class);

    @Override
    public Object getObject(JSONObject paramObj) {
        Map<String,Object> result = new HashMap<String, Object>();
        Context context = ContextHolder.getContext();
        //标签参数
        int num = paramObj.getInteger("num");

        //标签参数
        Long pageIndex = context.getPageIndex();
        //来自url参数
        Map<String, String> pmap = context.getParamMap();

        Long guideId = null;
        Long rescolumnId = null;
        Long organId = null;
        if (pmap != null && pmap.size() > 0) {
            if (!AppUtil.isEmpty(pmap.get("guideId"))) {
                guideId = Long.valueOf(pmap.get("guideId"));
                result.put("guideId",guideId);
            }

            if (!AppUtil.isEmpty(pmap.get("rescolumnId"))) {
                rescolumnId = Long.valueOf(pmap.get("rescolumnId"));
            }

            if(!AppUtil.isEmpty(pmap.get("organId"))) {
                organId = Long.valueOf(pmap.get("organId"));
            }
        }

        String name = null;
        if (pmap != null && pmap.size() > 0) {
            if (!AppUtil.isEmpty(pmap.get("keywords"))) {
                name = pmap.get("keywords");
                name = XSSFilterUtil.filterSqlInject(name);
            }
        }

        ParamDto dto = new ParamDto();
        if(!AppUtil.isEmpty(num)) {
            dto.setPageSize(num);
        }

        if(null != name) {
            dto.setKeys("name");
            dto.setKeyValue(name);
        }

        dto.setPageIndex(pageIndex-1);
        dto.setSiteId(ContextHolder.getContext().getSiteId());
        if(null != guideId) {
            Pagination page = null;
            CmsWorkGuideEO eo = workGuideService.getEntity(CmsWorkGuideEO.class,guideId);
            if(null != eo) {
                List<CmsGuideResRelatedEO> relatedEOs = CacheHandler.getList(CmsGuideResRelatedEO.class, CacheGroup.CMS_PARENTID,eo.getId());
                if(null != relatedEOs && !relatedEOs.isEmpty()) {
                    for(CmsGuideResRelatedEO resRelatedEO: relatedEOs) {
                        if(dto.getResIds() == null) {
                            dto.setResIds(resRelatedEO.getResId() + "");
                        } else {
                            dto.setResIds(dto.getResIds() + "," + resRelatedEO.getResId());
                        }
                    }
                    page = tableResourcesService.getPageEOs(dto);
                }
            }
            if(page != null && page.getData() != null) {
                List<CmsTableResourcesEO> list = (List<CmsTableResourcesEO>) page.getData();
                for(CmsTableResourcesEO resourcesEO : list) {
                    if(resourcesEO.getOrganId() != null) {
                        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, resourcesEO.getOrganId());
                        if(organEO != null) {
                            resourcesEO.setOrganName(organEO.getName());
                        }
                    }
                }
            }

            result.put("page",page);
        } else if(rescolumnId != null) {
            Pagination page = null;
            List<Long> cids = new ArrayList<Long>();
            cids.add(rescolumnId);
            List<CmsWorkGuideEO> guideEOs = workGuideService.getEOsByCIds(dto,cids);
            List<CmsGuideResRelatedEO> relatedEOs = new ArrayList<CmsGuideResRelatedEO>();
            if(null != guideEOs && !guideEOs.isEmpty()) {
                for(CmsWorkGuideEO guideEO : guideEOs) {
                    List<CmsGuideResRelatedEO> resRelatedEOs = CacheHandler.getList(CmsGuideResRelatedEO.class, CacheGroup.CMS_PARENTID,guideEO.getId());
                    if(null != resRelatedEOs) {
                        relatedEOs.addAll(resRelatedEOs);
                    }
                }

                for(CmsGuideResRelatedEO resRelatedEO: relatedEOs) {
                    if(dto.getResIds() == null) {
                        dto.setResIds(resRelatedEO.getResId() + "");
                    } else {
                        dto.setResIds(dto.getResIds() + "," + resRelatedEO.getResId());
                    }
                }

                if(dto.getResIds() == null) {
                    dto.setResIds("-1");
                }
                page = tableResourcesService.getPageEOs(dto);
            }
            result.put("page",page);
        } else if(organId != null) {
            Pagination guides = workGuideService.getPageEOs(dto,organId,"","","workGuide");
            List<CmsWorkGuideEO> wgs = (List<CmsWorkGuideEO>)guides.getData();
            if(null != wgs) {
                for(CmsWorkGuideEO cwge : wgs) {
                    if(null != cwge) {
                        List<CmsGuideResRelatedEO> relatedEOs = CacheHandler.getList(CmsGuideResRelatedEO.class, CacheGroup.CMS_PARENTID,cwge.getId());
                        if(null != relatedEOs && !relatedEOs.isEmpty()) {
                            for(CmsGuideResRelatedEO resRelatedEO: relatedEOs) {
                                if(dto.getResIds() == null) {
                                    dto.setResIds(resRelatedEO.getResId() + "");
                                } else {
                                    dto.setResIds(dto.getResIds() + "," + resRelatedEO.getResId());
                                }
                            }
                        }
                    }
                }
            }

            if(AppUtil.isEmpty(dto.getResIds())) {
                dto.setResIds("-1");
            }
            Pagination page = tableResourcesService.getPageEOs(dto);
            result.put("page",page);
        } else {
            Pagination page = tableResourcesService.getPageEOs(dto);
            result.put("page",page);
        }

        result.put("columnId",context.getColumnId());
        result.put("fileServerPath", globalConfig.getFileServerNamePath());
        return result;
    }

    /**
     * 预处理结果
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        Map<String, Object> map = (Map<String, Object>) resultObj;
        Context context = ContextHolder.getContext();
        //来自url参数
        Map<String, String> pmap = context.getParamMap();
        String name = null;
        if (pmap != null && pmap.size() > 0) {
            if (!AppUtil.isEmpty(pmap.get("keywords"))) {
                name = pmap.get("keywords");
                name = XSSFilterUtil.stripXSS(name);
                map.put("keywords",name);
            }
        }
        return map;
    }
}
