package cn.lonsun.system.sitechart.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.system.sitechart.internal.entity.SiteChartTrendEO;
import cn.lonsun.system.sitechart.vo.CountStatVO;
import cn.lonsun.system.sitechart.vo.MainCountVO;

public interface ISiteChartTrendService extends IBaseService<SiteChartTrendEO> {

	MainCountVO getMainCount(Long siteId,Long st,Long ed);
	MainCountVO getMainAvg(Long siteId,Long st,Long ed);
	CountStatVO getCount(Long siteId,Long st,Long ed);
	List<SiteChartTrendEO> getList(Long siteId,Long st,Long ed);
}
