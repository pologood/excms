package cn.lonsun.system.sitechart.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.sitechart.vo.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("siteChartMainMySqlDao")
public class SiteChartMainMySqlDaoImpl extends SiteChartMainDaoImpl{

	@SuppressWarnings("unchecked")
	@Override
	public List<LocationVO> getTraffic(Long siteId, Date st, Date ed) {
		StringBuffer hql=new StringBuffer("select date_format(createDate,'%Y-%m-%d %H') as location,count(s.id) as visits from SiteChartMainEO s where 1=1 ");
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
		hql.append(" group by date_format(createDate,'%Y-%m-%d %H') order by location ");
		return (List<LocationVO>) getBeansByHql(hql.toString(), map, LocationVO.class, null);
	}


	@Override
	public List<CountStatVO> getUv(String day,String st, String ed) {
		String hql="SELECT count(*) AS num, l.siteId AS siteId from " +
				"( select count(*) as num,s.site_id as siteId,s.s_cookie AS scookie from CMS_SITE_CHART_MAIN s where" +
				" s.create_date>=str_to_date('"+st+"','%Y-%m-%d %H:%i:%s') and s.create_date<str_to_date('"+ed+"','%Y-%m-%d %H:%i:%s') " +
				"group by s.site_id,s.s_cookie ) l GROUP BY l.siteId";
		return (List<CountStatVO>) getBeansBySql(hql, new Object[]{},CountStatVO.class);
	}

	@Override
	public List<CountStatVO> getNuv(String st, String ed) {
		String hql="SELECT COUNT(l.siteId) AS num, l.siteId FROM ( " +
				"SELECT s.site_id AS siteId, s.IP AS ip FROM CMS_SITE_CHART_MAIN s  " +
				"WHERE s.create_date < str_to_date('"+ed+"','%Y-%m-%d %H:%i:%s') GROUP BY s.site_id, s.ip " +  //只统计结束时间之前的数据
				"HAVING SUM( CASE WHEN s.create_date >= str_to_date('"+st+"','%Y-%m-%d %H:%i:%s') " +
				"AND s.create_date < str_to_date('"+ed+"','%Y-%m-%d %H:%i:%s') THEN 1 ELSE 0 END ) > 0 " +  //规定时间内的次数大于0
				"AND SUM(CASE WHEN s.create_date < str_to_date('"+st+"','%Y-%m-%d %H:%i:%s') THEN 1 ELSE 0 END ) <= 0 ) l " + //之前的次数小于等于0
				"GROUP BY l.siteId";
		return (List<CountStatVO>) getBeansBySql(hql, new Object[]{},CountStatVO.class);
	}

	@Override
	public List<CountStatVO> getIp(String day,String st, String ed) {
		String hql="SELECT count(*) AS num, l.siteId AS siteId from " +
				"( select count(*) as num,s.site_id as siteId,s.ip AS ip from CMS_SITE_CHART_MAIN s where " +
				"s.create_date>=str_to_date('"+st+"','%Y-%m-%d %H:%i:%s') and s.create_date<str_to_date('"+ed+"','%Y-%m-%d %H:%i:%s')  " +
				"group by s.site_id,s.ip ) l GROUP BY l.siteId";
		return (List<CountStatVO>) getBeansBySql(hql, new Object[]{},CountStatVO.class);
	}


	@Override
	public List<KVP> getVisitBySourceType(String st, String ed, Long siteId,
			String sourceType) {
		String sql="select count(date_format(t.create_date,'%Y-%m-%d %H')) as value,date_format(t.create_date,'%Y-%m-%d %H') as name from cms_site_chart_main t "
				+ " where cookie = '-'";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=str_to_date('"+st+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<str_to_date('"+ed+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(sourceType)){
			sql+=" and source_type='"+sourceType+"'";
		}
		sql+=" group by date_format(t.create_date,'%Y-%m-%d %H')  order by date_format(t.create_date,'%Y-%m-%d %H') asc";
		return (List<KVP>) getBeansBySql(sql, new Object[]{}, KVP.class);
	}


	@Override
	public List<TypeKVP> getVisitGroupBySourceType(String st, String ed, Long siteId) {
		String sql="select source_type as type, count(date_format(t.create_date,'%Y-%m-%d %H')) as value,date_format(t.create_date,'%Y-%m-%d %H') as name from cms_site_chart_main t "
				+ " where cookie = '-'";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=str_to_date('"+st+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<str_to_date('"+ed+"','%Y-%m-%d')";
		}
		sql+=" group by source_type, date_format(t.create_date,'%Y-%m-%d %H')  order by date_format(t.create_date,'%Y-%m-%d %H') asc";
		return (List<TypeKVP>) getBeansBySql(sql, new Object[]{}, TypeKVP.class);
	}

	@Override
	public List<KVP> getVisitByEngine(String st, String ed, Long siteId,
			String engine) {
		String sql="select count(date_format(t.create_date,'%Y-%m-%d %H')) as value,date_format(t.create_date,'%Y-%m-%d %H') as name from cms_site_chart_main t "
				+ " where cookie = '-'";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=str_to_date('"+st+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<str_to_date('"+ed+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(engine)){
			sql+=" and search_engine='"+engine+"'";
		}
		sql+=" group by date_format(t.create_date,'%Y-%m-%d %H')  order by date_format(t.create_date,'%Y-%m-%d %H') asc";
		return (List<KVP>) getBeansBySql(sql, new Object[]{}, KVP.class);
	}

	@Override
	public List<TypeKVP> getVisitGroupByEngine(String st, String ed, Long siteId) {
		String sql="select search_engine as type, count(date_format(t.create_date,'%Y-%m-%d %H')) as value,date_format(t.create_date,'%Y-%m-%d %H') as name from cms_site_chart_main t "
				+ " where cookie = '-'";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=str_to_date('"+st+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<str_to_date('"+ed+"','%Y-%m-%d')";
		}
		sql+=" group by search_engine, date_format(t.create_date,'%Y-%m-%d %H')  order by date_format(t.create_date,'%Y-%m-%d %H') asc";
		return (List<TypeKVP>) getBeansBySql(sql, new Object[]{}, TypeKVP.class);
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
					" FROM cms_site_chart_main t where t.search_key is not null";
   		if(!AppUtil.isEmpty(siteId)){
   			sql+=" and t.site_id = "+siteId;
   		}
		if(!AppUtil.isEmpty(key)){
			sql+="  and lower(t.search_key) like lower('%"+key+"%') escape '/'";
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=str_to_date('"+st+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<str_to_date('"+ed+"','%Y-%m-%d')";
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
						" FROM cms_site_chart_main t where t.search_key is not null";
	   		if(!AppUtil.isEmpty(siteId)){
	   			sql+=" and t.site_id = "+siteId;
	   		}
			if(!AppUtil.isEmpty(key)){
				sql+="  and lower(t.search_key) like lower('%"+key+"%') escape '/'";
			}
			if(!AppUtil.isEmpty(st)){
				sql+=" and t.create_date>=str_to_date('"+st+"','%Y-%m-%d')";
			}
			if(!AppUtil.isEmpty(ed)){
				sql+=" and t.create_date<str_to_date('"+ed+"','%Y-%m-%d')";
			}
			return (SearchKeyTotalVO) getBeanBySql(sql, new Object[]{}, SearchKeyTotalVO.class);
		}

	@Override
	public List<KVP> getVisitTrend(String st, String ed, boolean isPV,
			Long siteId) {
		String sql="select count(date_format(t.create_date,'%Y-%m-%d %H')) as value,date_format(t.create_date,'%Y-%m-%d %H') as name from cms_site_chart_main t "
				+ " where 1=1";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=str_to_date('"+st+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<str_to_date('"+ed+"','%Y-%m-%d')";
		}
		if(!isPV){
			sql+=" and cookie = '-'";
		}
		sql+=" group by date_format(t.create_date,'%Y-%m-%d %H')  order by date_format(t.create_date,'%Y-%m-%d %H') asc";
		return (List<KVP>) getBeansBySql(sql, new Object[]{}, KVP.class);
	}


	@Override
	public List<KVP> getPageSourceTrend(String st, String ed, boolean isPV,
			Long siteId,String sourceHost,String target) {
		String sql="select count(date_format(t.create_date,'%Y-%m-%d %H')) as value,date_format(t.create_date,'%Y-%m-%d %H') as name from cms_site_chart_main t "
				+ " where 1=1";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=str_to_date('"+st+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<str_to_date('"+ed+"','%Y-%m-%d')";
		}
		if(!isPV){
			sql+=" and cookie = '-'";
		}
		if(!AppUtil.isEmpty(sourceHost)){
			sql+=" and "+target+"='"+sourceHost+"'";
		}
		sql+=" group by date_format(t.create_date,'%Y-%m-%d %H')  order by date_format(t.create_date,'%Y-%m-%d %H') asc";
		return (List<KVP>) getBeansBySql(sql, new Object[]{}, KVP.class);
	}


	@Override
	public List<TypeKVP> getPageSourceGroupByTarget(String st, String ed, boolean isPV,Long siteId,String target) {
		String sql="select "+target+" as type, count(date_format(t.create_date,'%Y-%m-%d %H')) as value,date_format(t.create_date,'%Y-%m-%d %H') as name from cms_site_chart_main t "
				+ " where 1=1";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=str_to_date('"+st+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<str_to_date('"+ed+"','%Y-%m-%d')";
		}
		if(!isPV){
			sql+=" and cookie = '-'";
		}
		sql+=" group by "+target+", date_format(t.create_date,'%Y-%m-%d %H')  order by date_format(t.create_date,'%Y-%m-%d %H') asc";
		return (List<TypeKVP>) getBeansBySql(sql, new Object[]{}, TypeKVP.class);
	}


	@Override
	public Pagination getPageSourceDetail(Long siteId, String st, String ed,Long pageIndex,Integer pageSize,boolean isHost,String groupBy) {
		String sql="select t."+groupBy+" as referer,sum(CASE WHEN t.cookie = '-' THEN 1 ELSE 0 END) as sv,count(t.id) as pv from CMS_SITE_CHART_MAIN t where 1=1";
		if(!AppUtil.isEmpty(siteId)){
			sql+=" and t.site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=str_to_date('"+st+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<str_to_date('"+ed+"','%Y-%m-%d')";
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
			sql+=" and t.create_date>=str_to_date('"+st+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<str_to_date('"+ed+"','%Y-%m-%d')";
		}
		sql+=" group by t.city,t.province,t.country order by pv desc,province";
		return getPaginationBySql(pageIndex, pageSize, sql, new Object[]{}, LocationPageVO.class,new String[]{"city","province","country","pv","sv"});
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<KVP> getVisitTrendByCod(String st, String ed, Long siteId,boolean isPv,
			String where, String data) {
		String sql="select count(date_format(t.create_date,'%Y-%m-%d %H')) as value,date_format(t.create_date,'%Y-%m-%d %H') as name from cms_site_chart_main t "
				+ " where cookie = '-'";
		if(null!=siteId){
			sql+=" and site_id="+siteId;
		}
		if(!AppUtil.isEmpty(st)){
			sql+=" and t.create_date>=str_to_date('"+st+"','%Y-%m-%d')";
		}
		if(!AppUtil.isEmpty(ed)){
			sql+=" and t.create_date<str_to_date('"+ed+"','%Y-%m-%d')";
		}
		if(!isPv){
			sql+=" and cookie = '-'";
		}
		if(!AppUtil.isEmpty(where)&&!AppUtil.isEmpty(data)){
			sql+=" and "+where+"='"+data+"'";
		}
		sql+=" group by date_format(t.create_date,'%Y-%m-%d %H')  order by date_format(t.create_date,'%Y-%m-%d %H') asc";
		return (List<KVP>) getBeansBySql(sql, new Object[]{}, KVP.class);
	}


}
