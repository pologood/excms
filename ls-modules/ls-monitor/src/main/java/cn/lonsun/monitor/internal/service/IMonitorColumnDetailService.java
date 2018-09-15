package cn.lonsun.monitor.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.internal.entity.MonitorColumnDetailEO;
import cn.lonsun.monitor.internal.vo.*;

import java.util.Date;
import java.util.List;

/**
 * 日常监测栏目更新详细service层<br/>
 *
 */
public interface IMonitorColumnDetailService extends IBaseService<MonitorColumnDetailEO> {

	/**
	 * 获取日常监测栏目更新详细分页
	 */
	Pagination getPage(MonitorDetailQueryVO queryVO);


	/**
	 * 处理栏目不更新数据
	 */
	void handleColumnDatasByType(Long siteId,Long monitorId,Long reportId,String columnType);

	/**
	 * 启动新闻监测任务
	 * @param monitorId
	 */
	void monitorColumn(Long siteId,Long monitorId,Long reportId);

	/**
	 * 监测首页栏目
	 * @param siteId
	 * @param monitorId
	 * @param reportId
	 */
	void monitorIndexColumn(Long siteId,Long monitorId,Long reportId);

	/**
	 * 监测基本栏目信息
	 * @param siteId
	 * @param monitorId
	 * @param reportId
	 */
	void monitorBaseColumn(Long siteId,Long monitorId,Long reportId);


	/**
	 * 综合评分项 基本信息更新情况 自定义监测
	 *
	 * @param siteId
	 * @param monitorId
	 * @param reportId
	 */
	void monitorCustomBaseColumn(Long siteId, Long monitorId, Long reportId);

	/**
	 * 处理空白栏目
	 * @param monitorId
	 */
	void handleEmptyColumns(Long siteId,Long monitorId,Long reportId);


	/**
	 * 综合评分项-首页栏目
	 * @return
	 */
	IndexColumnStatisVO loadIndexColumnStatis(Long monitorId, Long siteId);

	/**
	 * 综合评分项目-基本信息
	 * @return
	 */
	ColumnBaseInfoStatisVO loadColumnBaseInfoStatis(Long monitorId, Long siteId);

	/**
	 * 单项否决-首页栏目不更新
	 * @return
	 */
	IndexNotUpdateStatisVO loadIndexNotUpdateStatis(Long monitorId, Long siteId);

	/**
	 * 单项否决-栏目不更新
	 * @return
	 */
	ColumnNotUpdateStatisVO loadColumnNotUpdateStatis(Long monitorId, Long siteId);


	List<MonitorColumnDetailEO> getList(Long monitorId, String[] columnType,String infoType, Long updateCount);

	List<MonitorColumnDetailEO> getList(MonitorDetailQueryVO queryVO);


	Long getCount(Long monitorId,String[] columnType,String infoType,Long updateCount);

	/**
	 * 获取规定时间内已经更新数据的首页栏目
	 * @param columnIds
	 * @param st
	 * @param ed
	 * @return
	 */
	List<MonitorColumnDetailEO> getIndexUpdatedColumns(String columnIds, Date st, Date ed);

	/**
	 * 获取规定时间内已经更新数据的首页信息公开栏目
	 * @param organCatId
	 * @param st
	 * @param ed
	 * @return
	 */
	List<MonitorColumnDetailEO> getIndexUpdatedPublics(String[] organCatId,Date st,Date ed);

}
