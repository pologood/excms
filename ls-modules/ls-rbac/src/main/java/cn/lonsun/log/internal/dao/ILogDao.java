package cn.lonsun.log.internal.dao;

import java.util.Date;
import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.log.internal.entity.LogEO;

/**
 * Created by yy on 2014/8/13.
 */
public interface ILogDao extends IMockDao<LogEO> {
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
    public Pagination getPage(Long pageIndex, Integer pageSize, Date startDate, Date endDate, String type, String key);

    /**
     * 获取所有日志
     * @param startDate
     * @param endDate
     * @param type
     * @param key
     * @return
     */
    public List<LogEO> getAllLogs(Date startDate, Date endDate, String type, String key);
}
