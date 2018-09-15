package cn.lonsun.special.internal.service.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.special.internal.dao.ISpecialDownloadLogDao;
import cn.lonsun.special.internal.entity.SpecialDownloadLogEO;
import cn.lonsun.special.internal.service.ISpecialDownloadLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专题下载记录
 * @author zhongjun
 */
@Service("specialDownloadLogService")
public class SpecialDownloadLogServiceImpl extends MockService<SpecialDownloadLogEO> implements ISpecialDownloadLogService {

    @Autowired
    private ISpecialDownloadLogDao specialDownloadLogDao;

    @Override
    public List<SpecialDownloadLogEO> getByStatus(Long cloudId, Long siteId, Long... status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", SpecialDownloadLogEO.RecordStatus.Normal.toString());
        map.put("cloudId", cloudId);
//        map.put("siteId", siteId);
        map.put("status", status);
        return specialDownloadLogDao.getEntities(SpecialDownloadLogEO.class, map);
    }

    @Override
    public List<SpecialDownloadLogEO> getByCloudIds(Long[] cloudId, Long siteId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", SpecialDownloadLogEO.RecordStatus.Normal.toString());
        map.put("cloudId", cloudId);
//        map.put("siteId", siteId);
        return specialDownloadLogDao.getEntities(SpecialDownloadLogEO.class, map);
    }


    @Override
    public void updateLogStatus(Long id, Long status) {
        specialDownloadLogDao.updateLogStatus(id, status);
    }

    @Override
    public void deleteByThemeId(Long id) {
        String hql = "update SpecialDownloadLogEO set recordStatus = ? where  specialThemeId = ?";
        specialDownloadLogDao.executeUpdateByHql(hql, new Object[]{SpecialDownloadLogEO.RecordStatus.Removed.toString(), id});
    }
}
