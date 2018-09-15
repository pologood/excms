package cn.lonsun.site.contentModel.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.site.contentModel.internal.dao.IModelTemplateSpecialDao;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/26.
 */
@Repository
public class ModelTemplateSpecialDaoImpl extends MockDao<ModelTemplateEO> implements IModelTemplateSpecialDao  {
    @Override
    public ModelTemplateEO getFirstModelByColumnCode(String columnTypeCode) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("modelTypeCode", columnTypeCode);
        paramsMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return getEntity(ModelTemplateEO.class, paramsMap);
    }
}
