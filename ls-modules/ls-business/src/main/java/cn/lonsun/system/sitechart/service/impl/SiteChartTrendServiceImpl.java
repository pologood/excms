package cn.lonsun.system.sitechart.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.system.sitechart.dao.ISiteChartTrendDao;
import cn.lonsun.system.sitechart.internal.entity.SiteChartTrendEO;
import cn.lonsun.system.sitechart.service.ISiteChartTrendService;
import cn.lonsun.system.sitechart.vo.CountStatVO;
import cn.lonsun.system.sitechart.vo.MainCountVO;

@Service("siteChartTrendService")
public class SiteChartTrendServiceImpl extends BaseService<SiteChartTrendEO>
		implements ISiteChartTrendService {

	@Autowired
	private ISiteChartTrendDao siteChartTrendDao;
	@Override
	public MainCountVO getMainCount(Long siteId, Long st, Long ed) {
		return siteChartTrendDao.getMainCount(siteId, st, ed);
	}
	@Override
	public MainCountVO getMainAvg(Long siteId, Long st, Long ed) {
		return siteChartTrendDao.getMainAvg(siteId, st, ed);
	}
	@Override
	public CountStatVO getCount(Long siteId, Long st, Long ed) {
		return siteChartTrendDao.getCount(siteId, st, ed);
	}
	@Override
	public List<SiteChartTrendEO> getList(Long siteId, Long st, Long ed) {
		return siteChartTrendDao.getList(siteId, st, ed);
	}
}
