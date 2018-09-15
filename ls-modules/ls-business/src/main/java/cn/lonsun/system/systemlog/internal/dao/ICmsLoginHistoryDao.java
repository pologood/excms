package cn.lonsun.system.systemlog.internal.dao;

import java.util.Date;
import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.systemlog.internal.entity.CmsLoginHistoryEO;


/**
 * 
 * @ClassName: ICmsLoginHistoryDao
 * @Description: 登入日志数据访问层
 * @author Hewbing
 * @date 2015年8月25日 上午10:55:37
 *
 */
public interface ICmsLoginHistoryDao extends IMockDao<CmsLoginHistoryEO> {
	
	/**
	 * 根据日期获取登录次数
	 *
	 * @param date
	 * @param type 登录类型
	 * @return
	 */
	public long getLoginTimesByDate(String date,Integer type);
	
	/**
	 * 根据日期获取登录用户数量
	 *
	 * @param date
	 * @return
	 */
	public long getLoginUserCountByDate(String date);

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
    public List<CmsLoginHistoryEO> getAllLogs(Date startDate, Date endDate, String type, String key,Long siteId);
}

