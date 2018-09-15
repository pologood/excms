package cn.lonsun.system.sitechart.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.system.sitechart.internal.entity.SiteChartTrendEO;
import cn.lonsun.system.sitechart.vo.CountStatVO;
import cn.lonsun.system.sitechart.vo.MainCountVO;

public interface ISiteChartTrendDao extends IBaseDao<SiteChartTrendEO> {

	MainCountVO getMainCount(Long siteId,Long st,Long ed);
	
	MainCountVO getMainAvg(Long siteId,Long st,Long ed);
	
	CountStatVO getCount(Long siteId,Long st,Long ed);
	
	List<SiteChartTrendEO> getList(Long siteId,Long st,Long ed);

}
