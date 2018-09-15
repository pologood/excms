package cn.lonsun.system.systemlog.internal.service;

import java.util.Date;
import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.systemlog.internal.entity.CmsLoginHistoryEO;


/**
 * 
 * @ClassName: ICmsLoginHistoryService
 * @Description: 登录历史日志服务类
 * @author Hewbing
 * @date 2015年8月25日 上午10:54:08
 *
 */
public interface ICmsLoginHistoryService extends IMockService<CmsLoginHistoryEO> {
	
	
	
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

