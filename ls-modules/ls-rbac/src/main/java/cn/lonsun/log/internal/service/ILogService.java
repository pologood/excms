package cn.lonsun.log.internal.service;

import java.util.Date;
import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.log.internal.entity.LogEO;

/**
 * Created by yy on 2014/8/12.
 */
public interface ILogService extends IMockService<LogEO> {
    /**
     * 日志添加
     * 
     * @param description 内容
     * @param caseType 业务对象类型,例如：UserEO
     * @param operation 操作,例如：LogEO.Operation.Add.toString(),//新增操作
     */
    public void saveLog(String description, String caseType, String operation);

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
