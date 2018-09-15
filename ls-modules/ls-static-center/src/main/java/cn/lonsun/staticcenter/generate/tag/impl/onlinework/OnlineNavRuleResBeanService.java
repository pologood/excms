package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.GlobalConfig;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.net.service.entity.CmsGuideResRelatedEO;
import cn.lonsun.net.service.entity.CmsRelatedRuleEO;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IRelatedRuleService;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangxx on 2016/8/18.
 */
@Component
public class OnlineNavRuleResBeanService extends AbstractBeanService {

    @Autowired
    private IWorkGuideService workGuideService;

    @Autowired
    private IRelatedRuleService relatedRuleService;

    private static GlobalConfig globalConfig = SpringContextHolder.getBean(GlobalConfig.class);

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {

        //获取上下文
        Context context = ContextHolder.getContext();
        //来自URL的参数
        Map<String,String> pmap = context.getParamMap();
        Map<String,Object> result = new HashMap<String,Object>();
        String dateFormat = paramObj.getString("dateFormat");


        int num = paramObj.getInteger("num");
        Long pageIndex = context.getPageIndex();
        Long guideId=null;

        if(null != pmap && pmap.size() > 0) {
            if( !AppUtil.isEmpty(pmap.get("guideId"))) {
                guideId = Long.valueOf(pmap.get("guideId"));
                result.put("guideId",guideId);
            }
        }

        ParamDto dto=new ParamDto();
        dto.setSiteId(ContextHolder.getContext().getSiteId());
        dto.setPageIndex(pageIndex-1);
        dto.setPageSize(num);
        dto.setPublish(1);

        if(null != guideId) {
            CmsWorkGuideEO eo = workGuideService.getEntity(CmsWorkGuideEO.class,guideId);
            Pagination page=null;

            if(null != eo) {
               List<CmsGuideResRelatedEO> relatedEOs = CacheHandler.getList(CmsGuideResRelatedEO.class, CacheGroup.CMS_PARENTID,eo.getId());
                if(null != relatedEOs && !relatedEOs.isEmpty()) {
                    for(CmsGuideResRelatedEO relatedEO : relatedEOs) {
                        if(dto.getResIds() == null) {
                            dto.setResIds(relatedEO.getResId() + "");
                        } else {
                            dto.setResIds(dto.getResIds() + "," + relatedEO.getResId());
                        }
                    }
                    page = relatedRuleService.getPageEOs(dto);
                }
            }
            if(null != page && page.getData() != null) {
                List<CmsRelatedRuleEO> list = (List<CmsRelatedRuleEO>)page.getData();
                for(CmsRelatedRuleEO relatedRuleEO : list) {
                    if( null !=relatedRuleEO.getOrganId()) {
                        OrganEO organEO = CacheHandler.getEntity(OrganEO.class,relatedRuleEO.getOrganId());
                        if( null != organEO ) {
                            relatedRuleEO.setOrganName(organEO.getName());
                        }
                    }
                    String joinDate = relatedRuleEO.getJoinDate();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                    try {
                       Date newDate = simpleDateFormat.parse(joinDate);
                        relatedRuleEO.setJoinDate(newDate.toString());
                    } catch (Exception e ){
                        e.printStackTrace();
                    }
                }
            }

            result.put("page",page);

        } else {
            Pagination page = relatedRuleService.getPageEOs(dto);
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
        return map;
    }
}
