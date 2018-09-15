package cn.lonsun.monitor.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.internal.entity.MonitorInteractDetailEO;
import cn.lonsun.monitor.internal.vo.BadInteractStatisVO;
import cn.lonsun.monitor.internal.vo.InteractInfoStatisVO;
import cn.lonsun.monitor.internal.vo.MonitorDetailQueryVO;

import java.util.List;
import java.util.Map;

/**
 * 日常监测互动更新详细service层<br/>
 *
 */
public interface IMonitorInteractDetailService extends IBaseService<MonitorInteractDetailEO> {

	/**
	 * 获取日常监测互动更新详细分页
	 */
	Pagination getPage(MonitorDetailQueryVO queryVO);

	/**
	 * 获取超过三个月未回复留言
	 */
	Pagination getUnReplyPage(Long pageIndex,Integer pageSize,String contentIds);

	/**
	 * 获取日常监测互动更新详细 不分页
	 */
	List<MonitorInteractDetailEO> getList(MonitorDetailQueryVO queryVO);

	/**
	 * 处理政务咨询类(留言)栏目数据
	 * @param monitorId
	 * @param columnType
	 */
	void handleZWZXDatas(Long siteId,Long monitorId,Long reportId,String columnType);

	/**
	 * 处理栏目更新数据
	 */
	void handleColumnDatasByType(Long siteId,Long monitorId,Long reportId,String columnType);

	/**
	 * 启动互动交流监测任务
	 * @param monitorId
	 */
	void monitorInteract(Long siteId,Long monitorId,Long reportId);


	/**
	 * 单项否决-互动回应差
	 * @return
	 */
	BadInteractStatisVO loadBadInteractStatis(Long monitorId, Long siteId);

	/**
	 * 综合评分项-互动回应情况
	 * @param monitorId
	 * @param siteId
	 * @return
	 */
	InteractInfoStatisVO loadInteractInfoStatis(Long monitorId, Long siteId);

	/**
	 * 根据栏目类型跟检测任务id计算分数
	 * @param monitorId
	 * @param siteId
	 * @param columnType
	 * @return
	 */
	Map<String,Object> calculateScoreByColumnType(Long monitorId, Long siteId, String columnType);

	Long getCount(Long monitorId,String[] columnType,Long updateCount);
}
