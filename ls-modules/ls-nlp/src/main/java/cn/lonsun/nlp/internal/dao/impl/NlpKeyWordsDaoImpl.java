package cn.lonsun.nlp.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.nlp.internal.dao.INlpKeyWordsDao;
import cn.lonsun.nlp.internal.entity.NlpKeyWordsEO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关键词
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
@Repository
public class NlpKeyWordsDaoImpl extends BaseDao<NlpKeyWordsEO> implements INlpKeyWordsDao {

    @Override
    public NlpKeyWordsEO getEOByKeyword(String keyword) {
        String hql = "from NlpKeyWordsEO t where t.name = ? ";
        return getEntityByHql(hql,new Object[]{keyword});
    }

    @Override
    public List<NlpKeyWordsEO> getEOsByKeyword(List<String> keyword) {
        Map<String,Object> param = new HashMap<String,Object>();
        String hql = "from NlpKeyWordsEO t where t.name in (:name) ";
        param.put("name",keyword.toArray());
        return getEntitiesByHql(hql,param);
    }
}
