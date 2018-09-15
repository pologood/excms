package cn.lonsun.publicInfo.internal.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogCountEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogCountService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fth on 2017/5/31.
 */
@Service
public class PublicCatalogCountServiceImpl extends BaseService<PublicCatalogCountEO> implements IPublicCatalogCountService {

    @Override
    public List<PublicCatalogCountEO> getListByOrganId(Long organId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("organId", organId);
        return getEntities(PublicCatalogCountEO.class, paramMap);
    }

    @Override
    public void updateOrganCatIdCountByStatus(Long organId, Long catId, Long increment, Integer status, boolean cascade) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("organId", organId);
        paramMap.put("catId", catId);

        boolean save = false;
        PublicCatalogCountEO update = getEntity(PublicCatalogCountEO.class, paramMap);
        if (null == update) {//新增
            save = true;
            update = new PublicCatalogCountEO();
            update.setOrganId(organId);
            update.setCatId(catId);
        }
        // 设置数字
        if (status == 1) {//发布
            update.setPublishCount(update.getPublishCount() + increment);
            if (cascade) {
                Long count = update.getUnPublishCount() - increment;
                update.setUnPublishCount(count > 0L ? count : 0L);
            }
        } else if (status == 0) {//取消发布
            update.setUnPublishCount(update.getUnPublishCount() + increment);
            if (cascade) {
                Long count = update.getPublishCount() - increment;
                update.setPublishCount(count > 0L ? count : 0L);
            }
        }
        if (save) {//更新到数据库
            this.saveEntity(update);
        } else {
            this.updateEntity(update);
        }
    }
}
