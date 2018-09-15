package cn.lonsun.heatAnalysis.dao.impl;

import org.springframework.stereotype.Repository;

/**
 * Created by fth on 2017/5/9.
 */
@Repository("keyWordsHeatMySqlDao")
public class KeyWordsHeatMySqlDaoImpl extends KeyWordsHeatDaoImpl {

    @Override
    public Long getMinSortNum(Long siteId) {
        String hql = "select nvl(min(sortNum),1) as c from KeyWordsHeatEO where siteId = ?";
        return getCount(hql, new Object[] { siteId });
    }
}
