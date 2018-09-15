package cn.lonsun.system.sitechart.service;

import cn.lonsun.content.vo.BaseContentVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.sitechart.internal.entity.SiteChartMainEO;
import cn.lonsun.system.sitechart.vo.*;

import java.util.Date;
import java.util.List;

public interface ISiteChartMainService extends IBaseService<SiteChartMainEO> {

	void saveSiteChart(SiteChartMainEO mainEO);
	
	List<LocationVO> getLocationList(Long siteId,Date st,Date ed,Integer limit);
	
	List<LocationVO> getTraffic(Long siteId,Date st,Date ed);
	
	List<LocationVO> getPvByLocation(Long siteId,Date st,Date ed,String divide,boolean isPv,Integer limit);
	
	List<CountStatVO> getPv(Date st,Date ed);

	List<CountVisitVO> getPvClick(Integer limit);

	List<BaseContentVO> getPvList(BaseContentVO vo,Integer limit);

	List<CountStatVO> getUv(String day,String st,String ed);
	
	List<CountStatVO> getNuv(String st,String ed);
	
	List<CountStatVO> getIp(String day,String st, String ed);
	
	List<CountStatVO> getSv(Date st,Date ed);
	
	Pagination getVisitPage(VisitDeatilPageVo pageVO);
	
	List<KVP> getSource(Date st,Date ed,Long siteId,String groupBy);
	
	List<KVP> getVisitBySourceType(String st,String ed,Long siteId,String sourceType);

	List<TypeKVP> getVisitGroupBySourceType(String st, String ed, Long siteId);
	
	List<KVP> getVisitByEngine(String st,String ed,Long siteId,String engine);

	List<TypeKVP> getVisitGroupByEngine(String st, String ed, Long siteId);
	
	Long getCountVisitByCity(Long siteId,Date st,Date ed,boolean isPv);
	
	Pagination getSearchKey(Long siteId, String st, String ed,String key,Long pageIndex,Integer pageSize);
	
	SearchKeyTotalVO getSearchKeyTotal(Long siteId, String st, String ed,String key);
	
	List<KVP> getVisitTrend(String st,String ed,boolean isPV,Long siteId);
	
	List<KVP> getPageSource(Long siteId,Date st, Date ed,boolean isPv,Integer limit,String groupBy);
	
	Long getPageSourceCount(Long siteId,Date st, Date ed,boolean isPv,boolean isHost);
	
	List<KVP> getPageSourceTrend(String st,String ed,boolean isPV,Long siteId,String sourceHost,String target);

	List<TypeKVP> getPageSourceGroupByTarget(String st, String ed, boolean isPV,Long siteId,String target);
	
	Pagination getPageSourceDetail(Long siteId, String st, String ed,Long pageIndex,Integer pageSize,boolean isHost,String groupBy);
	
	Pagination getLocationPage(Long siteId, String st, String ed,Long pageIndex,Integer pageSize);
	
	List<ClientVO> getClientList(Long siteId,Date st, Date ed,String target,boolean isPv,Integer limit);
	
	List<KVP> getRelList(Long siteId,Date st, Date ed,String target,boolean isPv,Integer limit);
	
	Long getClientCount(Long siteId,Date st, Date ed,boolean isPv);
	
	List<KVP> getVisitTrendByCod(String st,String ed,Long siteId,boolean isPv,String where,String data);

	Long getCount(Long siteId);
}
