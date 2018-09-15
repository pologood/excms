package cn.lonsun.special.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.special.internal.entity.SpecialDownloadLogEO;

public interface ISpecialDownloadLogDao extends IMockDao<SpecialDownloadLogEO> {

    /**
     * 更新日志状态
     * @param id
     * @param status
     */
    public void updateLogStatus(Long id, Long status);
}
