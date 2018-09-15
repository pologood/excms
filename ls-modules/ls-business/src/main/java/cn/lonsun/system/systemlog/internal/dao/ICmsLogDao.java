package cn.lonsun.system.systemlog.internal.dao;


import java.util.Date;
import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;

/**
 * 
 * @ClassName: ICmsLogDao
 * @Description: 操作日志数据访问层
 * @author Hewbing
 * @date 2015年8月25日 上午10:54:50
 *
 */
public interface ICmsLogDao extends IMockDao<CmsLogEO> {
    /**
     * 日志查询
     * 
     * @param request
     * @param pageIndex
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param type
     * @param key
     * @return
     */
    public Pagination getPage(Long pageIndex, Integer pageSize, Date startDate, Date endDate, String type, String key,Long siteId);

    /**
     * 获取所有日志
     * @param startDate
     * @param endDate
     * @param type
     * @param key
     * @return
     */
    public List<CmsLogEO> getAllLogs(Date startDate, Date endDate, String type, String key,Long siteId);
}
