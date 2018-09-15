package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.publicInfo.internal.service.IOrganConfigService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据目录id查找对应的单位
 * Created by houhd on 2016/9/22.
 */
@Component
public class PublicUnitListBeanService extends AbstractBeanService {

    @Resource
    private IOrganService organService;
    @Autowired
    private IOrganConfigService organConfigService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {

        Context context = ContextHolder.getContext();
        Long id=paramObj.getLong("id");
        Long siteId = context.getSiteId();
        List<Long> organIdList = organConfigService.getOrganIdsByCatId(id);
        List<OrganEO> list = new ArrayList<OrganEO>();
        List<OrganEO> templist = new ArrayList<OrganEO>();
        if(null!=organIdList&&organIdList.size()>0){
            list = organService.getOrgansByOrganIds( organIdList.toArray(new Long[]{}));
            SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
            List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
            for(OrganEO eo:list){
                if(!organList.contains(eo)){
                    templist.add(eo);
                }
            }
            list.removeAll(templist);
        }
        return list;
    }
}
