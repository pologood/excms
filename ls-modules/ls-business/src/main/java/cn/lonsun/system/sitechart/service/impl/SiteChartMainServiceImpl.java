package cn.lonsun.system.sitechart.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.vo.BaseContentVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.sitechart.dao.ISiteChartMainDao;
import cn.lonsun.system.sitechart.internal.entity.SiteChartMainEO;
import cn.lonsun.system.sitechart.service.ISiteChartMainService;
import cn.lonsun.system.sitechart.vo.*;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("siteChartMainService")
public class SiteChartMainServiceImpl extends BaseService<SiteChartMainEO>
		implements ISiteChartMainService {

	@DbInject("siteChartMain")
	private ISiteChartMainDao siteChartMainDao;
	
	@Override
	public void saveSiteChart(SiteChartMainEO mainEO) {
			Long mainId=saveEntity(mainEO);
	}

	@Override
	public List<LocationVO> getLocationList(Long siteId, Date st, Date ed,
			Integer limit) {
		if(siteId==null){
			siteId=LoginPersonUtil.getSiteId();
		}
		return siteChartMainDao.getLocationList(siteId, st, ed, limit);
	}

	@Override
	public List<LocationVO> getTraffic(Long siteId, Date st, Date ed) {
		if(siteId==null){
			siteId=LoginPersonUtil.getSiteId();
		}
		return siteChartMainDao.getTraffic(siteId, st, ed);
	}

	@Override
	public List<LocationVO> getPvByLocation(Long siteId, Date st, Date ed,
			String divide, boolean isPv,Integer limit) {
		if(AppUtil.isEmpty(divide)) divide="province";
		return siteChartMainDao.getPvByLocation(siteId, st, ed, divide, isPv,limit);
	}
	
	@Override
	public List<CountStatVO> getPv(Date st, Date ed) {

		return siteChartMainDao.getPv(st, ed);
	}
	//网站访问排行
	public List<CountVisitVO> getPvClick(Integer limit) {

		return siteChartMainDao.getPvClick(limit);
	}
	//网站文章排行
	public List<BaseContentVO> getPvList(BaseContentVO vo,Integer limit){
		return siteChartMainDao.getPvList(vo,limit);
	}

	@Override
	public List<CountStatVO> getUv(String day, String st,String ed) {
		return siteChartMainDao.getUv(day, st, ed);
	}

	@Override
	public List<CountStatVO> getNuv(String st, String ed) {
		return siteChartMainDao.getNuv(st, ed);
	}

	@Override
	public List<CountStatVO> getIp(String day,String st, String ed) {
		return siteChartMainDao.getIp(day, st, ed);
	}

	@Override
	public List<CountStatVO> getSv(Date st, Date ed) {
		return siteChartMainDao.getSv(st, ed);
	}

	@Override
	public Pagination getVisitPage(VisitDeatilPageVo pageVO) {
		if(AppUtil.isEmpty(pageVO.getSiteId())){
			pageVO.setSiteId(LoginPersonUtil.getSiteId());
		}
		return siteChartMainDao.getVisitPage(pageVO);
	}

	@Override
	public List<KVP> getSource(Date st, Date ed, Long siteId,
			String groupBy) {
		if(AppUtil.isEmpty(groupBy)) groupBy="sourceType";
		return siteChartMainDao.getSource(st, ed, siteId, groupBy);
	}

	@Override
	public List<KVP> getVisitBySourceType(String st, String ed, Long siteId,
			String sourceType) {
		return siteChartMainDao.getVisitBySourceType(st, ed, siteId, sourceType);
	}

	@Override
	public List<TypeKVP> getVisitGroupBySourceType(String st, String ed, Long siteId){
		return siteChartMainDao.getVisitGroupBySourceType(st, ed, siteId);
	}

	@Override
	public List<KVP> getVisitByEngine(String st, String ed, Long siteId,
			String engine) {
		return siteChartMainDao.getVisitByEngine(st, ed, siteId, engine);
	}

	@Override
	public List<TypeKVP> getVisitGroupByEngine(String st, String ed, Long siteId){
		return siteChartMainDao.getVisitGroupByEngine(st, ed, siteId);
	}

	@Override
	public Long getCountVisitByCity(Long siteId, Date st, Date ed, boolean isPv) {
		return siteChartMainDao.getCountVisitByCity(siteId, st, ed, isPv);
	}

	@Override
	public Pagination getSearchKey(Long siteId, String st, String ed,
			String key, Long pageIndex, Integer pageSize) {
		if(pageIndex==null||pageIndex<0L) pageIndex=0L;
		if(pageSize==null||pageSize<0) pageSize=15;
		return siteChartMainDao.getSearchKey(siteId, st, ed, key, pageIndex, pageSize);
	}

	@Override
	public SearchKeyTotalVO getSearchKeyTotal(Long siteId, String st, String ed,
			String key) {
		return siteChartMainDao.getSearchKeyTotal(siteId, st, ed, key);
	}

	@Override
	public List<KVP> getVisitTrend(String st, String ed, boolean isPV,
			Long siteId) {
		return siteChartMainDao.getVisitTrend(st, ed, isPV, siteId);
	}

	@Override
	public List<KVP> getPageSource(Long siteId, Date st, Date ed, boolean isPv,
			Integer limit,String groupBy) {
		if(null==limit) limit=5;
		return siteChartMainDao.getPageSource(siteId, st, ed, isPv, limit,groupBy);
	}

	@Override
	public Long getPageSourceCount(Long siteId, Date st, Date ed, boolean isPv,boolean isHost) {
		return siteChartMainDao.getPageSourceCount(siteId, st, ed, isPv,isHost);
	}

	@Override
	public List<KVP> getPageSourceTrend(String st, String ed, boolean isPV,
			Long siteId,String sourceHost,String target) {
		return siteChartMainDao.getPageSourceTrend(st, ed, isPV, siteId, sourceHost,target);
	}

	@Override
	public List<TypeKVP> getPageSourceGroupByTarget(String st, String ed, boolean isPV,Long siteId,String target){
		return siteChartMainDao.getPageSourceGroupByTarget(st, ed, isPV,siteId,target);
	}

	@Override
	public Pagination getPageSourceDetail(Long siteId, String st, String ed,
			Long pageIndex, Integer pageSize,boolean isHost,String groupBy) {
		if(AppUtil.isEmpty(pageIndex)) pageIndex=0L;
		if(AppUtil.isEmpty(pageSize)) pageSize=15;
		return siteChartMainDao.getPageSourceDetail(siteId, st, ed, pageIndex, pageSize,isHost,groupBy);
	}

	@Override
	public Pagination getLocationPage(Long siteId, String st, String ed,
			Long pageIndex, Integer pageSize) {
		if(AppUtil.isEmpty(pageIndex)) pageIndex=0L;
		if(AppUtil.isEmpty(pageSize)) pageSize=15;
		return siteChartMainDao.getLocationPage(siteId, st, ed, pageIndex, pageSize);
	}

	@Override
	public List<ClientVO> getClientList(Long siteId, Date st, Date ed,String target,
			boolean isPv,Integer limit) {
		return siteChartMainDao.getClientList(siteId, st, ed,target, isPv,limit);
	}

	@Override
	public List<KVP> getVisitTrendByCod(String st, String ed, Long siteId,boolean isPv,
			String where, String data) {
		return siteChartMainDao.getVisitTrendByCod(st, ed, siteId, isPv, where, data);
	}

	@Override
	public Long getClientCount(Long siteId, Date st, Date ed, boolean isPv) {
		return siteChartMainDao.getClientCount(siteId, st, ed, isPv);
	}

	@Override
	public List<KVP> getRelList(Long siteId, Date st, Date ed, String target,
			boolean isPv, Integer limit) {
		return siteChartMainDao.getRelList(siteId, st, ed, target, isPv, limit);
	}

	@Override
	public Long getCount(Long siteId) {
		return siteChartMainDao.getPersonTimes(siteId);
	}
}
