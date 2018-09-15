package cn.lonsun.special.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.special.internal.dao.ISpecialDownloadLogDao;
import cn.lonsun.special.internal.entity.SpecialDownloadLogEO;
import org.springframework.stereotype.Repository;

/**
 * 专题下载记录
 * @author zhongjun
 */
@Repository
public class SpecialDownloadLogDaoImpl extends MockDao<SpecialDownloadLogEO> implements ISpecialDownloadLogDao {

    @Override
    public void updateLogStatus(Long id, Long status) {
        String hql = "update SpecialDownloadLogEO set status = ? where id = ? ";
        super.executeUpdateByHql(hql, new Object[]{status, id});
    }
}
