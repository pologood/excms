package cn.lonsun.system.systemlog.internal.service;

import java.util.Date;
import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;

/**
 * 
 * @ClassName: ICmsLogService
 * @Description: 操作日志业务逻辑层
 * @author Hewbing
 * @date 2015年8月25日 上午10:54:22
 *
 */
public interface ICmsLogService extends IMockService<CmsLogEO> {
    /**
     * 日志添加
     * 
     * @param description 内容
     * @param caseType 业务对象类型,例如：UserEO
     * @param operation 操作,例如：LogEO.Operation.Add.toString(),//新增操作
     */
    public void recLog(String description, String caseType, String operation);

    /**
     * 日志删除
     * 
     * @param logId 日志ID
     */
    public void deleteLog(Long logId);

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
