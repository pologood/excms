package cn.lonsun.site.contentModel.internal.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lonsun.core.base.entity.AMockEntity;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.contentModel.internal.dao.IModelTemplateDao;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-8<br/>
 */
@Repository("modelTemplateDao")
public class ModelTemplateDaoImpl extends MockDao<ModelTemplateEO> implements IModelTemplateDao {

    @Override
    public Pagination getPage(Long pageIndex, Integer pageSize, Long modelId) {
        String hql = " from ModelTemplateEO where 1=1 and recordStatus='Normal' ";
        if (pageIndex == null || pageIndex < 0) {
            pageIndex = 0L;
        }
        if (pageSize == null || pageSize < 0) {
            pageSize = 5;
        }
        if (modelId != null && modelId != 0L) {
            hql += " and modelId=" + modelId;
        } else {
            Pagination page = new Pagination();
            page.setPageIndex(pageIndex);
            page.setPageSize(pageSize);
            ModelTemplateEO eo = new ModelTemplateEO();
            List<Object> list = new ArrayList<Object>();
            list.add((Object) eo);
            page.setData(list);
            return page;
        }
        hql += " order by createDate";
        return getPagination(pageIndex, pageSize, hql, new Object[]{});
    }

    @Override
    public List<ModelTemplateEO> getFirstModelType(Long modelId) {
        String hql = " from ModelTemplateEO where 1=1 and recordStatus='Normal' ";
        if (modelId != null && modelId != 0L) {
            hql += " and modelId=" + modelId;
        }
        hql += " order by createDate";
        return getEntitiesByHql(hql);
    }

    @Override
    public ModelTemplateEO getFirstModelByColumnCode(String columnTypeCode) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("modelTypeCode", columnTypeCode);
        paramsMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return getEntity(ModelTemplateEO.class, paramsMap);
    }
}
