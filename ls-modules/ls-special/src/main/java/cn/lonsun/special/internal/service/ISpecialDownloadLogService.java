package cn.lonsun.special.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.special.internal.entity.SpecialDownloadLogEO;

import java.util.List;

public interface ISpecialDownloadLogService extends IMockService<SpecialDownloadLogEO> {

    /**
     * 获取某个专题的下载记录
     * @param cloudId
     * @param siteId
     * @return
     */
    public List<SpecialDownloadLogEO> getByStatus(Long cloudId, Long siteId, Long... status);

    public List<SpecialDownloadLogEO> getByCloudIds(Long[] cloudId, Long siteId);

    /**
     * 更新状态
     * @param id
     * @param status
     */
    public void updateLogStatus(Long id, Long status);


    void deleteByThemeId(Long id);
}
