package cn.lonsun.system.sitechart.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.vo.BaseContentVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.sitechart.dao.ISiteChartMainDao;
import cn.lonsun.system.sitechart.internal.entity.SiteChartMainEO;
import cn.lonsun.system.sitechart.vo.*;

import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("siteChartMainDao")
public class SiteChartMainDaoImpl extends BaseDao<SiteChartMainEO> implements ISiteChartMainDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<LocationVO> getLocationList(Long siteId,Date st, Date ed,Integer limit) {
		StringBuffer hql=new StringBuffer("select s.city as location,count(s.id) as visits from SiteChartMainEO s where 1=1 ");
		Map<String,Object> map=new HashMap<String, Object>();
		if(null!=siteId){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(null!=st){
			hql.append(" and createDate>=:createDate");
			map.put("createDate", st);
		}
		if(null!=ed){
			hql.append(" and createDate<:createDate");
			map.put("createDate", ed);			
		}
		hql.append(" group by s.city order by visits desc");
		return (List<LocationVO>) getBeansByHql(hql.toString(), map, LocationVO.class, limit);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LocationVO> getTraffic(Long siteId, Date st, Date ed) {
		StringBuffer hql=new StringBuffer("select to_char(createDate,'YYYY-MM-DD Hh24') as location,count(s.id) as visits from SiteChartMainEO s where 1=1 ");
		Map<String,Object> map=new HashMap<String, Object>();
		if(siteId!=null){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(null!=st){
			hql.append(" and createDate>=:st");
			map.put("st", st);
		}
		if(null!=ed){
			hql.append(" and createDate<:ed");
			map.put("ed", ed);
		}
		hql.append(" group by to_char(createDate,'YYYY-MM-DD Hh24') order by location ");
		return (List<LocationVO>) getBeansByHql(hql.toString(), map, LocationVO.class, null);
	}

	@Override
	public List<LocationVO> getPvByLocation(Long siteId, Date st, Date ed,String divide,
			boolean isPv,Integer limit) {
		StringBuffer hql=new StringBuffer("select "+divide+" as location,count(t.id) as visits from SiteChartMainEO t where 1=1");
		Map<String,Object> map=new HashMap<String, Object>();
		if(null!=st){
			hql.append(" and createDate>=:st");
			map.put("st", st);
		}
		if(null!=ed){
			hql.append(" and createDate<:ed");
			map.put("ed", ed);			
		}
		if(!isPv){
			hql.append(" and cookie = '-'");
		}
		hql.append(" and "+divide+" != '-' ");
		hql.append(" and country='中国' and "+divide+" != '-' ");
		if(null!=siteId){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}

		hql.append(" group by "+divide+" order by visits desc");
		return (List<LocationVO>) getBeansByHql(hql.toString(), map, LocationVO.class, limit);
	}
	
	@Override
	public List<CountStatVO> getPv(Date st,Date ed) {
		String hql="select count(*) as num,t.siteId as siteId from SiteChartMainEO t where t.createDate >=? and t.createDate<? group by t.siteId";
		return (List<CountStatVO>) getBeansByHql(hql, new Object[]{st,ed},CountStatVO.class);
	}

	public List<CountVisitVO> getPvClick(Integer limit) {
		String hql="select count(*) as num,t.siteId as siteId,s.name as name from SiteChartMainEO t ,IndicatorEO s "+
				"where t.siteId=s.indicatorId group by t.siteId,s.name order by num desc";
		return (List<CountVisitVO>) getBeansByHql(hql, new Object[]{},CountVisitVO.class,limit);
	}


	public List<BaseContentVO> getPvList(BaseContentVO vo,Integer limit) {

		StringBuffer hql = new StringBuffer("select count(*) as counts ,t.siteId as siteId,s.name as organName from BaseContentEO t,IndicatorEO s " +
				"where t.siteId=s.indicatorId and t.recordStatus='Normal'  and t.isPublish=1 ");
		Map<String, Object> map = new HashMap<String, Object>();

		if (!AppUtil.isEmpty(vo.getTypeCode())) {
			hql.append(" and t.typeCode=:typeCode");
			map.put("typeCode", vo.getTypeCode());
		}
		hql.append(" group by t.siteId ,s.name order by count( t.siteId) desc ");

		return (List<BaseContentVO>)getBeansByHql(hql.toString(),map,BaseContentVO.class,limit);
	}

	@Override
	public List<CountStatVO> getUv(String day,String st, String ed) {
		String hql="select count(*) as num,s.site_id as siteId from ("
				+ "select l.*, row_number() over (partition by l.site_id,l.s_cookie order by l.create_date) as group_idx from CMS_SITE_CHART_MAIN l where l.create_date>=to_date('"+day+"','yyyy-mm-dd') "
				+ ") s where s.group_idx = 1 and s.create_date>=to_date('"+st+"','yyyy-mm-dd hh24:mi:ss') and s.create_date<to_date('"+ed+"','yyyy-mm-dd hh24:mi:ss') group by s.site_id";
		return (List<CountStatVO>) getBeansBySql(hql, new Object[]{},CountStatVO.class);
	}

	@Override
	public List<CountStatVO> getNuv(String st, String ed) {
		String hql="select count(*) as num,s.site_id as siteId from ("
				+ "select l.*, row_number() over (partition by l.site_id,l.ip order by l.create_date) as group_idx from CMS_SITE_CHART_MAIN l "
				+ ") s where s.group_idx = 1 and s.create_date>=to_date('"+st+"','yyyy-mm-dd hh24:mi:ss') and s.create_date<to_date('"+ed+"','yyyy-mm-dd hh24:mi:ss') group by s.site_id";
		return (List<CountStatVO>) getBeansBySql(hql, new Object[]{},CountStatVO.class);
	}

	@Override
	public List<CountStatVO> getIp(String day,String st, String ed) {
		String hql="select count(*) as num,s.site_id as siteId from ("
				+ "select l.*, row_number() over (partition by l.site_id,l.ip order by l.create_date) as group_idx from CMS_SITE_CHART_MAIN l where l.create_date>=to_date('"+day+"','yyyy-mm-dd') "
				+ ") s where s.group_idx = 1 and s.create_date>=to_date('"+st+"','yyyy-mm-dd hh24:mi:ss') and s.create_date<to_date('"+ed+"','yyyy-mm-dd hh24:mi:ss') group by s.site_id";
		return (List<CountStatVO>) getBeansBySql(hql, new Object[]{},CountStatVO.class);
	}

	@Override
	public List<CountStatVO> getSv(Date st, Date ed) {
		String hql="select count(*) as num,t.siteId as siteId from SiteChartMainEO t where cookie = '-' and t.createDate >=? and t.createDate<? group by t.siteId";
		return (List<CountStatVO>) getBeansByHql(hql, new Object[]{st,ed},CountStatVO.class);
	}

	@Override
	public Pagination getVisitPage(VisitDeatilPageVo pageVO) {
		StringBuffer hql=new StringBuffer("from SiteChartMainEO s where 1=1 ");
		Map<String,Object> map=new HashMap<String, Object>();
		if(!AppUtil.isEmpty(pageVO.getSiteId())){
			hql.append(" and siteId=:siteId");
			map.put("siteId", pageVO.getSiteId());
		}
		if(!AppUtil.isEmpty(pageVO.getMemberId())){
			hql.append(" and memberId=:memberId");
			map.put("memberId", pageVO.getMemberId());
		}
		if(!AppUtil.isEmpty(pageVO.getIp())){
			hql.append(" and ip like :ip escape'/'");
			map.put("ip", "%".concat(pageVO.getIp()).concat("%"));
		}
		if(!AppUtil.isEmpty(pageVO.getKeyWord())){
			hql.append(" and searchKey like :searchKey");
			map.put("searchKey", "%".concat(pageVO.getKeyWord()).concat("%"));
		}
		if(!AppUtil.isEmpty(pageVO.getEngine())){
			hql.append(" and searchEngine like :searchEngine");
			map.put("searchEngine", "%".concat(pageVO.getEngine()).concat("%"));
		}
		if(!AppUtil.isEmpty(pageVO.getSt())){
			hql.append(" and createDate>=:st");
			map.put("st", pageVO.getSt());
		}
		if(!AppUtil.isEmpty(pageVO.getEd())){
			hql.append(" and createDate<:ed");
			map.put("ed", pageVO.getEd());
		}
		hql.append(" order by createDate desc");
		return getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), map);
	}

	@Override
	public List<KVP> getSource(Date st, Date ed, Long siteId,String groupBy) {
		StringBuffer hql=new StringBuffer("select "+groupBy+" as name, count(s.id) as value from SiteChartMainEO s where cookie = '-' and "+groupBy+" != '-' ");
		Map<String,Object> map=new HashMap<String, Object>();
		if(!AppUtil.isEmpty(siteId)){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(!AppUtil.isEmpty(st)){
			hql.append(" and createDate>=:st");
			map.put("st", st);
		}
		if(!AppUtil.isEmpty(ed)){
			hql.append(" and createDate<:ed");
			map.put("ed", ed);
		}
		if(!AppUtil.isEmpty(groupBy)){
			hql.append(" group by "+groupBy);
		}
		return (List<KVP>) getBeansByHql(hql.toString(), map, KVP.class, null);
	}

	@Override
	public List<KVP> getVisitBySourceType(String st, String ed, Long siteId,
			String sourceType) {
		String sql="select count(to_char(t.create_date, 'yyyy-mm-dd hh24')) as value,to_char(t.create_date, 'yyyy-mm-dd hh24') as name from cms_site_chart_main t "
				+ " where cookie = '-'";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=to_date('"+st+"', 'yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<to_date('"+ed+"', 'yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(sourceType)){
			sql+=" and source_type='"+sourceType+"'";
		}
		sql+=" group by to_char(t.create_date, 'yyyy-mm-dd hh24')  order by to_char(t.create_date, 'yyyy-mm-dd hh24') asc";
		return (List<KVP>) getBeansBySql(sql, new Object[]{}, KVP.class);
	}

	@Override
	public List<TypeKVP> getVisitGroupBySourceType(String st, String ed, Long siteId) {
		String sql="select source_type as type, count(to_char(t.create_date, 'yyyy-mm-dd hh24')) as value,to_char(t.create_date, 'yyyy-mm-dd hh24') as name from cms_site_chart_main t "
				+ " where cookie = '-' ";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=to_date('"+st+"', 'yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<to_date('"+ed+"', 'yyyy-mm-dd')";
		}
		sql+=" group by source_type, to_char(t.create_date, 'yyyy-mm-dd hh24')  order by to_char(t.create_date, 'yyyy-mm-dd hh24') asc";
		return (List<TypeKVP>) getBeansBySql(sql, new Object[]{}, TypeKVP.class);
	}

	@Override
	public List<KVP> getVisitByEngine(String st, String ed, Long siteId,
			String engine) {
		String sql="select count(to_char(t.create_date, 'yyyy-mm-dd hh24')) as value,to_char(t.create_date, 'yyyy-mm-dd hh24') as name from cms_site_chart_main t "
				+ " where cookie = '-'";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=to_date('"+st+"', 'yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<to_date('"+ed+"', 'yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(engine)){
			sql+=" and search_engine='"+engine+"'";
		}
		sql+=" group by to_char(t.create_date, 'yyyy-mm-dd hh24')  order by to_char(t.create_date, 'yyyy-mm-dd hh24') asc";
		return (List<KVP>) getBeansBySql(sql, new Object[]{}, KVP.class);
	}

	@Override
	public List<TypeKVP> getVisitGroupByEngine(String st, String ed, Long siteId) {
		String sql="select search_engine as type, count(to_char(t.create_date, 'yyyy-mm-dd hh24')) as value,to_char(t.create_date, 'yyyy-mm-dd hh24') as name from cms_site_chart_main t "
				+ " where cookie = '-' ";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=to_date('"+st+"', 'yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<to_date('"+ed+"', 'yyyy-mm-dd')";
		}
		sql+=" group by search_engine, to_char(t.create_date, 'yyyy-mm-dd hh24')  order by to_char(t.create_date, 'yyyy-mm-dd hh24') asc";
		return (List<TypeKVP>) getBeansBySql(sql, new Object[]{}, TypeKVP.class);
	}
	
	@Override
	public Long getCountVisitByCity(Long siteId, Date st, Date ed, boolean isPv) {
		StringBuffer hql=new StringBuffer(" from SiteChartMainEO t where country='中国'");
		Map<String,Object> map=new HashMap<String, Object>();
		if(!AppUtil.isEmpty(siteId)){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(!AppUtil.isEmpty(st)){
			hql.append(" and createDate>=:st");
			map.put("st", st);
		}
		if(!AppUtil.isEmpty(ed)){
			hql.append(" and createDate<:ed");
			map.put("ed", ed);
		}
		if(!isPv){
			hql.append(" and cookie = '-'");
		}
		return getCount(hql.toString(), map);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pagination getSearchKey(Long siteId, String st, String ed,
			String key,Long pageIndex,Integer pageSize) {
		String sql="select lower(t.search_key) as keyWord,count(t.id) as count,sum(CASE  WHEN t.search_engine= 'www.baidu.com' THEN 1 ELSE 0 END) as baidu,"+
 					"sum(CASE  WHEN t.search_engine='www.so.com' THEN 1 ELSE 0 END) as so,"+
  					"sum(CASE  WHEN t.search_engine='www.soso.com' or t.search_engine='www.sogou.com' THEN 1 ELSE 0 END) as soso,"+
   					"sum(CASE  WHEN t.search_engine='www.google.com' THEN 1 ELSE 0 END) as google,"+
   					"sum(case WHEN t.search_engine!='www.google.com'and t.search_engine!='www.baidu.com' and t.search_engine!='www.so.com' "
   					+ "and t.search_engine!='www.soso.com' and t.search_engine!='www.sogou.com'  THEN 1 ELSE 0 END) as other"+ 
					" FROM cms_site_chart_main t where t.search_key != '-' ";
   		if(!AppUtil.isEmpty(siteId)){
   			sql+=" and t.site_id = "+siteId;
   		}		
		if(!AppUtil.isEmpty(key)){
			sql+="  and lower(t.search_key) like lower('%"+key+"%') escape '/'";
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date >=to_date('"+st+"','yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date <to_date('"+ed+"','yyyy-mm-dd')";
		}
		sql+=" GROUP BY lower(t.search_key)";
		return getPaginationBySql(pageIndex, pageSize, sql, new Object[]{}, SearchKeyVO.class);
	}

	@Override
	public SearchKeyTotalVO getSearchKeyTotal(Long siteId, String st, String ed,
			String key) {
			String sql="select count(t.id) as count,sum(CASE WHEN t.search_engine= 'www.baidu.com' THEN 1 ELSE 0 END) as baidu,"+
						"sum(CASE WHEN t.search_engine='www.so.com' THEN 1 ELSE 0 END) as so,"+
						"sum(CASE WHEN t.search_engine='www.soso.com' or t.search_engine='www.sogou.com' THEN 1 ELSE 0 END) as soso,"+
						"sum(CASE WHEN t.search_engine='www.google.com' THEN 1 ELSE 0 END) as google,"+
						"sum(case WHEN t.search_engine!='www.google.com'and t.search_engine!='www.baidu.com' and t.search_engine!='www.so.com' "
						+ "and t.search_engine!='www.soso.com' and t.search_engine!='www.sogou.com' THEN 1 ELSE 0 END) as other"+ 
						" FROM cms_site_chart_main t where t.search_key != '-' ";
	   		if(!AppUtil.isEmpty(siteId)){
	   			sql+=" and t.site_id = "+siteId;
	   		}	
			if(!AppUtil.isEmpty(key)){
				sql+="  and lower(t.search_key) like lower('%"+key+"%') escape '/'";
			}
			if(!AppUtil.isEmpty(st)){
				sql+=" and t.create_date >=to_date('"+st+"','yyyy-mm-dd')";
			}
			if(!AppUtil.isEmpty(ed)){
				sql+=" and t.create_date <to_date('"+ed+"','yyyy-mm-dd')";
			}
			return (SearchKeyTotalVO) getBeanBySql(sql, new Object[]{}, SearchKeyTotalVO.class);
		}

	@Override
	public List<KVP> getVisitTrend(String st, String ed, boolean isPV,
			Long siteId) {
		String sql="select count(to_char(t.create_date, 'yyyy-mm-dd hh24')) as value,to_char(t.create_date, 'yyyy-mm-dd hh24') as name from cms_site_chart_main t "
				+ " where 1=1";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=to_date('"+st+"', 'yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<to_date('"+ed+"', 'yyyy-mm-dd')";
		}
		if(!isPV){
			sql+=" and cookie = '-'";
		}
		sql+=" group by to_char(t.create_date, 'yyyy-mm-dd hh24')  order by to_char(t.create_date, 'yyyy-mm-dd hh24') asc";
		return (List<KVP>) getBeansBySql(sql, new Object[]{}, KVP.class);
	}

	@Override
	public List<KVP> getPageSource(Long siteId,Date st, Date ed, boolean isPv,Integer limit,String groupBy) {
		StringBuffer hql=new StringBuffer("select t."+groupBy+" as name,count(t.id) as value from SiteChartMainEO t where "+groupBy+" != '-' ");
		Map<String,Object> map=new HashMap<String, Object>();
		if(!AppUtil.isEmpty(siteId)){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(!AppUtil.isEmpty(st)){
			hql.append(" and createDate>=:st");
			map.put("st", st);
		}
		if(!AppUtil.isEmpty(ed)){
			hql.append(" and createDate<:ed");
			map.put("ed", ed);
		}
		if(!isPv){
			hql.append(" and cookie = '-' ");
		}
		hql.append(" group by t."+groupBy+" order by value desc");
		return (List<KVP>) getBeansByHql(hql.toString(), map, KVP.class, limit);
	}

	@Override
	public Long getPageSourceCount(Long siteId, Date st, Date ed, boolean isPv,boolean isHost) {
		StringBuffer hql=new StringBuffer(" from SiteChartMainEO t where 1=1");
		Map<String,Object> map=new HashMap<String, Object>();
		if(!AppUtil.isEmpty(siteId)){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(!AppUtil.isEmpty(st)){
			hql.append(" and createDate>=:st");
			map.put("st", st);
		}
		if(!AppUtil.isEmpty(ed)){
			hql.append(" and createDate<:ed");
			map.put("ed", ed);
		}
		if(!isPv){
			hql.append(" and cookie = '-' ");
		}
		if(isHost){
			hql.append(" and sourceHost != '-' ");
		}
		return getCount(hql.toString(), map);
	}

	@Override
	public List<KVP> getPageSourceTrend(String st, String ed, boolean isPV,
			Long siteId,String sourceHost,String target) {
		String sql="select count(to_char(t.create_date, 'yyyy-mm-dd hh24')) as value,to_char(t.create_date, 'yyyy-mm-dd hh24') as name from cms_site_chart_main t "
				+ " where 1=1";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=to_date('"+st+"', 'yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<to_date('"+ed+"', 'yyyy-mm-dd')";
		}
		if(!isPV){
			sql+=" and cookie = '-'";
		}
		if(!AppUtil.isEmpty(sourceHost)){
			sql+=" and "+target+"='"+sourceHost+"'";
		}
		sql+=" group by to_char(t.create_date, 'yyyy-mm-dd hh24')  order by to_char(t.create_date, 'yyyy-mm-dd hh24') asc";
		return (List<KVP>) getBeansBySql(sql, new Object[]{}, KVP.class);
	}

	@Override
	public List<TypeKVP> getPageSourceGroupByTarget(String st, String ed, boolean isPV,Long siteId,String target){
		String sql="select "+target+" as type, count(to_char(t.create_date, 'yyyy-mm-dd hh24')) as value,to_char(t.create_date, 'yyyy-mm-dd hh24') as name from cms_site_chart_main t "
				+ " where 1=1";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=to_date('"+st+"', 'yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<to_date('"+ed+"', 'yyyy-mm-dd')";
		}
		if(!isPV){
			sql+=" and cookie = '-' ";
		}
		sql+=" group by "+target+", to_char(t.create_date, 'yyyy-mm-dd hh24')  order by to_char(t.create_date, 'yyyy-mm-dd hh24') asc";
		return (List<TypeKVP>) getBeansBySql(sql, new Object[]{}, TypeKVP.class);
	}

	@Override
	public Pagination getPageSourceDetail(Long siteId, String st, String ed,Long pageIndex,Integer pageSize,boolean isHost,String groupBy) {
		String sql="select t."+groupBy+" as referer,sum(CASE WHEN t.cookie = '-' THEN 1 ELSE 0 END) as sv,count(t.id) as pv from CMS_SITE_CHART_MAIN t where 1=1";
		if(!AppUtil.isEmpty(siteId)){
			sql+=" and t.site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date >=to_date('"+st+"','yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date <to_date('"+ed+"','yyyy-mm-dd')";
		}
		if(isHost){
			sql+=" and t.source_host != '-' ";
		}
		sql+=" group by t."+groupBy+" order by pv desc";
		return getPaginationBySql(pageIndex, pageSize, sql, new Object[]{}, PageSourceVO.class);
	}

	@Override
	public Pagination getLocationPage(Long siteId, String st, String ed,
			Long pageIndex, Integer pageSize) {
		String sql="select t.city as city,t.province as province ,t.country as country,sum(CASE WHEN t.cookie = '-' THEN 1 ELSE 0 END) as sv,count(t.id) as pv from CMS_SITE_CHART_MAIN t where 1=1";
		if(!AppUtil.isEmpty(siteId)){
			sql+=" and t.site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date >=to_date('"+st+"','yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date <to_date('"+ed+"','yyyy-mm-dd')";
		}
		sql+=" group by t.city,t.province,t.country order by pv desc,province";
		return getPaginationBySql(pageIndex, pageSize, sql, new Object[]{}, LocationPageVO.class,new String[]{"city","province","country","pv","sv"});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClientVO> getClientList(Long siteId, Date st, Date ed,String target,
			boolean isPv,Integer limit) {
		StringBuffer hql=new StringBuffer("select count(t.id) as num,t."+target+" as target,t.isPc as isPC from SiteChartMainEO t where 1=1");
		Map<String,Object> map=new HashMap<String, Object>();
		if(!AppUtil.isEmpty(siteId)){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(!isPv){
			hql.append(" and cookie = '-'");
		}
		if(null!=st){
			hql.append(" and createDate>=:st");
			map.put("st", st);
		}
		if(null!=ed){
			hql.append(" and createDate<:ed");
			map.put("ed", ed);
		}
		hql.append(" group by t.isPc,t."+target+" order by num desc");
		return (List<ClientVO>) getBeansByHql(hql.toString(), map, ClientVO.class, limit);
	}

	@Override
	public Long getClientCount(Long siteId, Date st, Date ed, boolean isPv) {
		StringBuffer hql=new StringBuffer("from SiteChartMainEO t where 1=1");
		Map<String,Object> map=new HashMap<String, Object>();
		if(!AppUtil.isEmpty(siteId)){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(!isPv){
			hql.append(" and cookie = '-'");
		}
		if(null!=st){
			hql.append(" and createDate>=:st");
			map.put("st", st);
		}
		if(null!=ed){
			hql.append(" and createDate<:ed");
			map.put("ed", ed);
		}
		return getCount(hql.toString(), map);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<KVP> getVisitTrendByCod(String st, String ed, Long siteId,boolean isPv,
			String where, String data) {
		String sql="select count(to_char(t.create_date, 'yyyy-mm-dd hh24')) as value,to_char(t.create_date, 'yyyy-mm-dd hh24') as name from cms_site_chart_main t "
				+ " where cookie = '-'";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=to_date('"+st+"', 'yyyy-mm-dd')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<to_date('"+ed+"', 'yyyy-mm-dd')";
		}
		if(!isPv){
			sql+=" and cookie = '-'";
		}
		if(!AppUtil.isEmpty(where)&&!AppUtil.isEmpty(data)){
			sql+=" and "+where+"='"+data+"'";
		}
		sql+=" group by to_char(t.create_date, 'yyyy-mm-dd hh24')  order by to_char(t.create_date, 'yyyy-mm-dd hh24') asc";
		return (List<KVP>) getBeansBySql(sql, new Object[]{}, KVP.class);
	}

	@Override
	public List<KVP> getRelList(Long siteId, Date st, Date ed,
			String target, boolean isPv, Integer limit) {
		StringBuffer hql=new StringBuffer("select count(t.id) as value,t."+target+" as name from SiteChartMainEO t where 1=1");
		Map<String,Object> map=new HashMap<String, Object>();
		if(!AppUtil.isEmpty(siteId)){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(!isPv){
			hql.append(" and cookie = '-'");
		}
		if(null!=st){
			hql.append(" and createDate>=:st");
			map.put("st", st);
		}
		if(null!=ed){
			hql.append(" and createDate<:ed");
			map.put("ed", ed);
		}
		hql.append(" group by t."+target+" order by value desc");
		return (List<KVP>) getBeansByHql(hql.toString(), map, KVP.class, limit);
	}

	@Override
	public Long getPersonTimes(Long siteId) {
		String hql = "from SiteChartMainEO t where siteId= ?";
		return getCount(hql.toString(),new Object[]{siteId});
	}
}
