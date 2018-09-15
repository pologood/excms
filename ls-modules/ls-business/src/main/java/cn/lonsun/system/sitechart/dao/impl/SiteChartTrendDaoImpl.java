package cn.lonsun.system.sitechart.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.system.sitechart.dao.ISiteChartTrendDao;
import cn.lonsun.system.sitechart.internal.entity.SiteChartTrendEO;
import cn.lonsun.system.sitechart.vo.CountStatVO;
import cn.lonsun.system.sitechart.vo.MainCountVO;

@Repository("siteChartTrendDao")
public class SiteChartTrendDaoImpl extends BaseDao<SiteChartTrendEO> implements
		ISiteChartTrendDao {

	@Override
	public MainCountVO getMainCount(Long siteId, Long st, Long ed) {
		StringBuffer hql=new StringBuffer("select sum(t.pv) as pv,sum(t.uv) as uv,sum(t.nuv) as nuv,sum(t.ip) as ip,sum(t.sv) as sv from"
				+ " SiteChartTrendEO t where 1=1");
		Map<String,Object> map=new HashMap<String, Object>();
		if(null!=siteId){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(null!=st){
			hql.append(" and time>=:st");
			map.put("st", st);
		}
		if(null!=ed){
			hql.append(" and time<:ed");
			map.put("ed", ed);
		}
		List<MainCountVO> beans = (List<MainCountVO>) getBeansByHql(hql.toString(), map, MainCountVO.class, null);
		return beans!=null?beans.get(0):null;
	}

	@Override
	public MainCountVO getMainAvg(Long siteId, Long st, Long ed) {
		StringBuffer hql=new StringBuffer("select round(avg(t.pv),0) as pv,round(avg(t.uv),0) as uv,round(avg(t.nuv),0) as nuv,round(avg(t.ip),0) as ip,round(avg(t.sv),0) as sv from"
				+ " SiteChartTrendEO t where 1=1");
		Map<String,Object> map=new HashMap<String, Object>();
		if(null!=siteId){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(null!=st){
			hql.append(" and time>=:st");
			map.put("st", st);
		}
		if(null!=ed){
			hql.append(" and time<:ed");
			map.put("ed", ed);
		}
		List<MainCountVO> beans = (List<MainCountVO>) getBeansByHql(hql.toString(), map, MainCountVO.class, null);
		return beans!=null?beans.get(0):null;
	}

	@Override
	public CountStatVO getCount(Long siteId, Long st, Long ed) {
		StringBuffer hql=new StringBuffer("select count(*) as num from"
				+ " SiteChartTrendEO t where 1=1");
		Map<String,Object> map=new HashMap<String, Object>();
		if(null!=siteId){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(null!=st){
			hql.append(" and time>=:st");
			map.put("st", st);
		}
		if(null!=ed){
			hql.append(" and time<:ed");
			map.put("ed", ed);
		}
		List<CountStatVO> bean = (List<CountStatVO>) getBeansByHql(hql.toString(), map, CountStatVO.class, null);
		return bean!=null?bean.get(0):null;
	}

	@Override
	public List<SiteChartTrendEO> getList(Long siteId, Long st, Long ed) {
		StringBuffer hql=new StringBuffer("from SiteChartTrendEO where 1=1");
		Map<String,Object> map=new HashMap<String, Object>();
		if(null!=siteId){
			hql.append(" and siteId=:siteId");
			map.put("siteId", siteId);
		}
		if(null!=st){
			hql.append(" and time>=:st");
			map.put("st", st);
		}
		if(null!=ed){
			hql.append(" and time<:ed");
			map.put("ed", ed);
		}
		return getEntitiesByHql(hql.toString(), map);
	}
}
