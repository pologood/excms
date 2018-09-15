package cn.lonsun.system.sitechart.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lonsun.system.sitechart.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.sitechart.service.ISiteChartMainService;
import cn.lonsun.system.sitechart.webutil.ProvinceOfChina;
import cn.lonsun.system.sitechart.webutil.SearchEnginesUtil;
import cn.lonsun.system.sitechart.webutil.StatDateUtil;
import cn.lonsun.util.LoginPersonUtil;

@Controller
@RequestMapping(value = "/visitSource", produces = { "application/json;charset=UTF-8" })
public class SiteChartSourceController extends BaseController {

	@Autowired
	private ISiteChartMainService siteChartMainService; 
	
	/**
	 * 
	 * @Title: sourceAnalysis
	 * @Description: 访问来源页
	 * @param model
	 * @return   Parameter
	 * @return  String   return type
	 * @throws
	 */
	@RequestMapping("sourceAnalysis")
	public String sourceAnalysis(ModelMap model){
		model.put("TODAY",StatDateUtil.yesterDay());
		return "system/sitechart/v_source";
	}
	
	/**
	 * 
	 * @Title: locationAnalysis
	 * @Description: 访问地区页面
	 * @param model
	 * @return   Parameter
	 * @return  String   return type
	 * @throws
	 */
	@RequestMapping("locationAnalysis")
	public String locationAnalysis(ModelMap model){
		model.put("TODAY",StatDateUtil.getToday2Str());
		return "system/sitechart/v_location";
	}
	
	/**
	 * 
	 * @Title: pageSource
	 * @Description: 来访页
	 * @param model
	 * @return   Parameter
	 * @return  String   return type
	 * @throws
	 */
	@RequestMapping("pageSource")
	public String pageSource(ModelMap model){
		model.put("TODAY",StatDateUtil.getToday2Str());
		return "system/sitechart/v_pageSource";
	}
	
	/**
	 * 
	 * @Title: targetPage
	 * @Description: 受访页
	 * @param model
	 * @return   Parameter
	 * @return  String   return type
	 * @throws
	 */
	@RequestMapping("targetPage")
	public String targetPage(ModelMap model){
		model.put("TODAY",StatDateUtil.getToday2Str());
		return "system/sitechart/v_targetPage";
	}
	
	/**
	 * 
	 * @Title: getSourceType
	 * @Description:访问来源类型报表
	 * @param ssvo
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getSourceType")
	@ResponseBody
	public Object getSourceType(SearchVO ssvo){
		Long siteId=LoginPersonUtil.getSiteId();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String stD=null;
		String edD=null;
		Date start = null;
		Date end=null;
		if(ssvo.getSt()!=null){
			stD=sdf.format(ssvo.getSt());
		}else{
			stD=sdf.format(new Date());
		}
		if(null!=ssvo.getEd()){
			//含本日
			Calendar calendar = new GregorianCalendar(); 
		    calendar.setTime(ssvo.getEd()); 
		    calendar.add(Calendar.DATE,1);
		    end=calendar.getTime();
			edD=sdf.format(end);
		}

		try {
			start = ssvo.getSt()==null?sdf.parse(sdf.format(new Date())):ssvo.getSt();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<KVP> sourceList = siteChartMainService.getSource(start, end, siteId, null);
		List<String> timeList = StatDateUtil.getDayForHour(ssvo.getSt(),ssvo.getEd());

		//原for循环中查询数据库效率较低，现一次性查出做处理
		List<TypeKVP> totalList = siteChartMainService.getVisitGroupBySourceType(stD, edD, siteId);
		Map<String,List<KVP>> kvpMap = new HashMap<String, List<KVP>>();
		for(TypeKVP typeKVP :totalList){
			KVP kvp = new KVP();
			kvp.setName(typeKVP.getName());
			kvp.setValue(typeKVP.getValue());
			String sourceType = typeKVP.getType();
			List<KVP> list;
			if(kvpMap.containsKey(sourceType)){
				list = kvpMap.get(sourceType);
			}else{
				list = new ArrayList<KVP>();
			}
			list.add(kvp);
			kvpMap.put(sourceType,list);
		}

		Map<String,Object> map=new HashMap<String, Object>();
		List<ChartStatVO> data=new ArrayList<ChartStatVO>();
		List<String> pieName=new ArrayList<String>();
		if(sourceList!=null){
			for(KVP li:sourceList){
				pieName.add(li.getName());

//				List<KVP> list = siteChartMainService.getVisitBySourceType(stD, edD, siteId, li.getName());
				List<KVP> list = kvpMap.get(li.getName());
				ChartStatVO vo=new ChartStatVO();
				vo.setName(li.getName());
				vo.setType("line");
				List<String> d=new ArrayList<String>();
				k:for(String t:timeList){
					for(KVP l:list){
						if(t.contains(l.getName())){
							d.add(String.valueOf(l.getValue()));
							continue k;
						}
					}
					d.add("0");
				}
				vo.setData(d);
				data.add(vo);
			}
		}
		map.put("sourceList", sourceList);
		map.put("timeList", timeList);
		map.put("data", data);
		map.put("pieName", pieName);
		
		return getObject(map);
		
	}

	/**
	 * 
	 * @Title: getSearchEngine
	 * @Description:搜索引擎报表
	 * @param ssvo
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getSearchEngine")
	@ResponseBody
	public Object getSearchEngine(SearchVO ssvo){
		Map<String,Object> map=new HashMap<String, Object>();
		Long siteId=LoginPersonUtil.getSiteId();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String stD=null;
		String edD=null;
		Date start = null;
		Date end=null;
		if(ssvo.getSt()!=null){
			stD=sdf.format(ssvo.getSt());
		}else{
			stD=sdf.format(new Date());
		}
		if(null!=ssvo.getEd()){
			//含本日
			Calendar calendar = new GregorianCalendar(); 
		    calendar.setTime(ssvo.getEd()); 
		    calendar.add(Calendar.DATE,1);
		    end=calendar.getTime();
			edD=sdf.format(end);
		}

		try {
			start = ssvo.getSt()==null?sdf.parse(sdf.format(new Date())):ssvo.getSt();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<KVP> engineList=siteChartMainService.getSource(start, end, siteId, "searchEngine");
		List<String> timeList = StatDateUtil.getDayForHour(ssvo.getSt(),ssvo.getEd());

		//原for循环中查询数据库效率较低，现一次性查出做处理
		List<TypeKVP> totalList = siteChartMainService.getVisitGroupByEngine(stD, edD, siteId);
		Map<String,List<KVP>> kvpMap = new HashMap<String, List<KVP>>();
		for(TypeKVP typeKVP :totalList){
			KVP kvp = new KVP();
			kvp.setValue(typeKVP.getValue());
			kvp.setName(typeKVP.getName());
			String searchEngine = typeKVP.getType();
			List<KVP> list;
			if(kvpMap.containsKey(searchEngine)){
				list = kvpMap.get(searchEngine);
			}else{
				list = new ArrayList<KVP>();
			}
			list.add(kvp);
			kvpMap.put(searchEngine,list);
		}
		
		List<ChartStatVO> data=new ArrayList<ChartStatVO>();
		List<String> pieName=new ArrayList<String>();
		if(engineList!=null&&engineList.size()>0){
			for(KVP li:engineList){
				pieName.add(SearchEnginesUtil.initSE.get(li.getName()));
//				List<KVP> list = siteChartMainService.getVisitByEngine(stD, edD, siteId, li.getName());
				List<KVP> list = kvpMap.get(li.getName());
				ChartStatVO vo=new ChartStatVO();
				vo.setName(SearchEnginesUtil.initSE.get(li.getName()));
				vo.setType("line");
				List<String> d=new ArrayList<String>();
				k:for(String t:timeList){
					for(KVP l:list){
						if(t.contains(l.getName())){
							d.add(String.valueOf(l.getValue()));
							continue k;
						}
					}
					d.add("0");
				}
				vo.setData(d);
				data.add(vo);
				li.setName(SearchEnginesUtil.initSE.get(li.getName()));
			}
		}else{
			timeList.clear();
		}
		map.put("engineList",engineList);
		map.put("timeList", timeList);
		map.put("data", data);
		map.put("pieName", pieName);
		return getObject(map);
		
	}
	
	/**
	 * 
	 * @Title: getVisitByProvince
	 * @Description:访问城市报表
	 * @param vo
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getVisitByProvince")
	@ResponseBody
	public Object getVisitByProvince(SearchVO vo){
		Map<String,Object> map=new HashMap<String, Object>();
		Date start = null;
		Date end=null;
		boolean type=false;
		String mapText="来访量(SV)";
		if("pv".equals(vo.getSearchKey())){
			type=true;
			mapText="浏览量(PV)";
		}
		if(null!=vo.getSt()) {
			start=vo.getSt();
		}else{
			start=new Date(StatDateUtil.getToday());
		}
		if(null!=vo.getEd()){
			//含本日
			Calendar calendar = new GregorianCalendar(); 
		    calendar.setTime(vo.getEd()); 
		    calendar.add(Calendar.DATE,1);
		    end=calendar.getTime();
		}
		Long siteId=LoginPersonUtil.getSiteId();
		List<LocationVO> lcationPv = siteChartMainService.getPvByLocation(siteId, start, end, null, type,null);
		List<String> poc=ProvinceOfChina.province;
		List<KVP> pData=new ArrayList<KVP>();
		Long mapMax=0L;
		k:for(String p:poc){
			KVP kvp=new KVP();
			for(LocationVO pv:lcationPv){
				if(pv.getLocation().contains(p)){
					if(mapMax<pv.getVisits()){
						mapMax=pv.getVisits();
					}
					kvp.setName(p);
					kvp.setValue(pv.getVisits());
					pData.add(kvp);
					continue k;
				}
			}
			kvp.setName(p);
			kvp.setValue(0L);
			pData.add(kvp);
		}
		
		Long cityCount=siteChartMainService.getCountVisitByCity(siteId, start, end, type);
		Long toOther=0l;
		List<String> cityList=new ArrayList<String>();
		List<LocationVO> lcationCity= siteChartMainService.getPvByLocation(siteId, start, end, "city", type,5);
		List<KVP> cityData=new ArrayList<KVP>();
		if(null!=lcationCity){
			for(LocationVO c:lcationCity){
				cityList.add(c.getLocation());
				KVP kvp=new KVP();
				kvp.setName(c.getLocation());
				kvp.setValue(c.getVisits());
				cityData.add(kvp);
				toOther+=c.getVisits();
			}
		}
		Long other=cityCount-toOther;
		if(other>0){
			cityList.add("其他");
			KVP kvp=new KVP();
			kvp.setName("其他");
			kvp.setValue(other);
			cityData.add(kvp);
		}
		
		map.put("mapText", mapText);
		map.put("mapMax", mapMax);
		map.put("mapData", pData);
		map.put("cityList", cityList);
		map.put("cityData", cityData);
		return getObject(map);
	}
	
	@RequestMapping("searchKey")
	public String searchKey(ModelMap model){
		model.put("TODAY",StatDateUtil.getToday2Str());
		return "system/sitechart/v_searchKey";
	}
	
	/**
	 * 
	 * @Title: getSearchKeyPage
	 * @Description: 搜索词
	 * @param st
	 * @param ed
	 * @param key
	 * @param pageIndex
	 * @param pageSize
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getSearchKeyPage")
	@ResponseBody
	public Object getSearchKeyPage(String st,String ed,String key,Long pageIndex,Integer pageSize){
		Long siteId=LoginPersonUtil.getSiteId();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		if(AppUtil.isEmpty(st)&&AppUtil.isEmpty(ed)){
			st=ed=StatDateUtil.getToday2Str();
		}
		if(!AppUtil.isEmpty(ed)){
			try {
				Long t=sdf.parse(ed).getTime()+(24*60*60*1000);
				Date date=new Date(t);
				ed=sdf.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Pagination page = siteChartMainService.getSearchKey(siteId, st, ed, key, pageIndex, pageSize);
		return getObject(page);
	}
	
	/**
	 * 
	 * @Title: getSearchKeyTotal
	 * @Description: 获取全部搜索词
	 * @param st
	 * @param ed
	 * @param key
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getSearchKeyTotal")
	@ResponseBody
	public Object getSearchKeyTotal(String st,String ed,String key){
		Long siteId=LoginPersonUtil.getSiteId();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		if(AppUtil.isEmpty(st)&&AppUtil.isEmpty(ed)){
			st=ed=StatDateUtil.getToday2Str();
		}
		if(!AppUtil.isEmpty(ed)){
			try {
				Long t=sdf.parse(ed).getTime()+(24*60*60*1000);
				Date date=new Date(t);
				ed=sdf.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		SearchKeyTotalVO total = siteChartMainService.getSearchKeyTotal(siteId, st, ed, key);
		return getObject(total);
	}
	
	/**
	 * 
	 * @Title: getPageSource
	 * @Description: 访问来源报表
	 * @param st
	 * @param ed
	 * @param key
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getPageSource")
	@ResponseBody
	public Object getPageSource(String st,String ed,String key){
		Long siteId=LoginPersonUtil.getSiteId();
		Map<String,Object>map=new HashMap<String, Object>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		boolean isPv=false;
		if("pv".equals(key)) isPv=true;
		Date stD=null;
		Date edD=null;
		try {
			if(AppUtil.isEmpty(st)){
				st=StatDateUtil.getToday2Str();
				stD=sdf.parse(st);
			}else{
				stD=sdf.parse(st);
			}
			if(!AppUtil.isEmpty(ed)){
				Long t=sdf.parse(ed).getTime()+(24*60*60*1000);
				edD=new Date(t);
				ed=sdf.format(edD);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Long pageSourceCount=siteChartMainService.getPageSourceCount(siteId, stD, edD, isPv,true);
		List<KVP> pageSourceStat = siteChartMainService.getPageSource(siteId, stD, edD, isPv, 5,"sourceHost");

		//原for循环中查询数据库效率较低，现一次性查出做处理
		List<TypeKVP> totalList = siteChartMainService.getPageSourceGroupByTarget(st, ed, isPv, siteId,"source_host");
		Map<String,List<KVP>> kvpMap = new HashMap<String, List<KVP>>();
		for(TypeKVP typeKVP :totalList){
			KVP kvp = new KVP();
			kvp.setValue(typeKVP.getValue());
			kvp.setName(typeKVP.getName());
			String sourceHost = typeKVP.getType();
			List<KVP> list;
			if(kvpMap.containsKey(sourceHost)){
				list = kvpMap.get(sourceHost);
			}else{
				list = new ArrayList<KVP>();
			}
			list.add(kvp);
			kvpMap.put(sourceHost,list);
		}

		Long toOther=0l;
		List<String> pageSourceMenu=new ArrayList<String>();
		int i=0;
		List<ChartStatVO> trendData=new ArrayList<ChartStatVO>();
		List<String> timeList = StatDateUtil.getDayForHour(stD,edD);
		List<String> trendName=new ArrayList<String>();
		if(null!=pageSourceStat&&pageSourceStat.size()>0){
			for(KVP k:pageSourceStat){
				if(AppUtil.isEmpty(k.getName())) k.setName("标签或浏览器输入地址");
				pageSourceMenu.add(k.getName());
				toOther+=k.getValue();
				if(i<3){
					ChartStatVO vo=new ChartStatVO();
					vo.setName(k.getName());
					vo.setType("line");
					List<String> d=new ArrayList<String>();
					trendName.add(k.getName());
//					List<KVP> pageSourceTrend = siteChartMainService.getPageSourceTrend(st, ed, isPv, siteId, k.getName(),"source_host");
					List<KVP> pageSourceTrend = kvpMap.get(k.getName());
					k:for(String t:timeList){
						for(KVP l:pageSourceTrend){
							if(t.contains(l.getName())){
								d.add(String.valueOf(l.getValue()));
								continue k;
							}
						}
						d.add("0");
					}
					vo.setData(d);
					trendData.add(vo);
					i++;
				}
			}
		}
		if(pageSourceCount>toOther){
			KVP kvp=new KVP();
			kvp.setName("其他");
			kvp.setValue(pageSourceCount-toOther);
			pageSourceStat.add(kvp);
			pageSourceMenu.add("其他");
		}
		
		map.put("pageSourceMenu", pageSourceMenu);
		map.put("pageSource", pageSourceStat);
		map.put("trendName", trendName);
		map.put("timeList", timeList);
		map.put("trendData", trendData);
		return getObject(map);
	}
	
	/**
	 * 
	 * @Title: getPageSourceDetail
	 * @Description: 来源页面详情报表
	 * @param st
	 * @param ed
	 * @param pageIndex
	 * @param pageSize
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getPageSourceDetail")
	@ResponseBody
	public Object getPageSourceDetail(String st,String ed,Long pageIndex,Integer pageSize){
		Long siteId=LoginPersonUtil.getSiteId();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date stD=null;
		Date edD=null;
		try {
			if(AppUtil.isEmpty(st)){
				st=StatDateUtil.getToday2Str();
				stD=sdf.parse(st);
			}else{
				stD=sdf.parse(st);
			}
			if(!AppUtil.isEmpty(ed)){
				Long t=sdf.parse(ed).getTime()+(24*60*60*1000);
				edD=new Date(t);
				ed=sdf.format(edD);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Pagination page = siteChartMainService.getPageSourceDetail(siteId, st, ed, pageIndex, pageSize,true,"referer");
		Long pvCount=siteChartMainService.getPageSourceCount(siteId, stD, edD, true,true);
		Long svCount=siteChartMainService.getPageSourceCount(siteId, stD, edD, false,true);
		if(svCount==0L) svCount=1L;
		@SuppressWarnings("unchecked")
		List<PageSourceVO> data=(List<PageSourceVO>) page.getData();
		List<PageSourceStatVO> list=new ArrayList<PageSourceStatVO>();
		java.text.DecimalFormat df=new java.text.DecimalFormat("###.##");   
		if(null!=data&&data.size()>0){
			for(PageSourceVO d:data){
				PageSourceStatVO vo=new PageSourceStatVO();
				vo.setPv(d.getPv());
				vo.setSv(d.getSv());
				vo.setReferer(d.getReferer());
				vo.setPvCent(String.valueOf((df.format((double)d.getPv()*100/pvCount))));
				vo.setSvCent(String.valueOf((df.format((double)d.getSv()*100/svCount))));
				list.add(vo);
			}
		}
		page.setData(list);
		return getObject(page);
	}
	
	/**
	 * 
	 * @Title: getLocationPage
	 * @Description: 访问城市详情报表
	 * @param st
	 * @param ed
	 * @param pageIndex
	 * @param pageSize
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getLocationPage")
	@ResponseBody
	public Object getLocationPage(String st,String ed,Long pageIndex,Integer pageSize){
		Long siteId=LoginPersonUtil.getSiteId();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date stD=null;
		Date edD=null;
		if(AppUtil.isEmpty(st)){
			st=StatDateUtil.getToday2Str();
			try {
				stD=sdf.parse(st);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			try {
				stD=sdf.parse(st);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(!AppUtil.isEmpty(ed)){
			try {
				Long t=sdf.parse(ed).getTime()+(24*60*60*1000);
				edD=new Date(t);
				ed=sdf.format(edD);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Pagination page = siteChartMainService.getLocationPage(siteId, st, ed, pageIndex, pageSize);
		Long pvCount=siteChartMainService.getPageSourceCount(siteId, stD, edD, true,false);
		Long svCount=siteChartMainService.getPageSourceCount(siteId, stD, edD, false,false);
		if(svCount==0L) svCount=1L;
		@SuppressWarnings("unchecked")
		List<LocationPageVO> data=(List<LocationPageVO>) page.getData();
		java.text.DecimalFormat df=new java.text.DecimalFormat("###.##");   
		if(null!=data&&data.size()>0){
			for(LocationPageVO d:data){
				d.setPvCent(String.valueOf((df.format((double)d.getPv()*100/pvCount))));
				d.setSvCent(String.valueOf((df.format((double)d.getSv()*100/svCount))));
			}
		}
		return getObject(page);
	}
	
	/**
	 * 
	 * @Title: getTargetPage
	 * @Description:受访页报表
	 * @param st
	 * @param ed
	 * @param key
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getTargetPage")
	@ResponseBody
	public Object getTargetPage(String st,String ed,String key){
		Long siteId=LoginPersonUtil.getSiteId();
		Map<String,Object>map=new HashMap<String, Object>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		boolean isPv=false;
		if("pv".equals(key)) isPv=true;
		Date stD=null;
		Date edD=null;
		try {
			if(AppUtil.isEmpty(st)){
				st=StatDateUtil.getToday2Str();
				stD=sdf.parse(st);
			}else{
				stD=sdf.parse(st);
			}
			if(!AppUtil.isEmpty(ed)){
				Long t=sdf.parse(ed).getTime()+(24*60*60*1000);
				edD=new Date(t);
				ed=sdf.format(edD);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Long pageSourceCount=siteChartMainService.getPageSourceCount(siteId, stD, edD, isPv,false);
		List<KVP> pageSourceStat = siteChartMainService.getPageSource(siteId, stD, edD, isPv, 5,"url");

		//原for循环中查询数据库效率较低，现一次性查出做处理
		List<TypeKVP> totalList = siteChartMainService.getPageSourceGroupByTarget(st, ed, isPv, siteId,"url");
		Map<String,List<KVP>> kvpMap = new HashMap<String, List<KVP>>();
		for(TypeKVP typeKVP :totalList){
			KVP kvp = new KVP();
			kvp.setValue(typeKVP.getValue());
			kvp.setName(typeKVP.getName());
			String url = typeKVP.getType();
			List<KVP> list;
			if(kvpMap.containsKey(url)){
				list = kvpMap.get(url);
			}else{
				list = new ArrayList<KVP>();
			}
			list.add(kvp);
			kvpMap.put(url,list);
		}

		Long toOther=0l;
		List<String> pageSourceMenu=new ArrayList<String>();
		int i=0;
		List<ChartStatVO> trendData=new ArrayList<ChartStatVO>();
		List<String> timeList = StatDateUtil.getDayForHour(stD,edD);
		List<String> trendName=new ArrayList<String>();
		if(null!=pageSourceStat&&pageSourceStat.size()>0){
			for(KVP k:pageSourceStat){
				if(AppUtil.isEmpty(k.getName())) k.setName("标签或浏览器输入地址");
				pageSourceMenu.add(k.getName());
				toOther+=k.getValue();
				if(i<3){
					ChartStatVO vo=new ChartStatVO();
					vo.setName(k.getName());
					vo.setType("line");
					List<String> d=new ArrayList<String>();
					trendName.add(k.getName());
//					List<KVP> pageSourceTrend = siteChartMainService.getPageSourceTrend(st, ed, isPv, siteId, k.getName(),"url");
					List<KVP> pageSourceTrend = kvpMap.get(k.getName());
					k:for(String t:timeList){
						for(KVP l:pageSourceTrend){
							if(t.contains(l.getName())){
								d.add(String.valueOf(l.getValue()));
								continue k;
							}
						}
						d.add("0");
					}
					vo.setData(d);
					trendData.add(vo);
					i++;
				}
			}
		}
		if(pageSourceCount>toOther){
			KVP kvp=new KVP();
			kvp.setName("其他");
			kvp.setValue(pageSourceCount-toOther);
			pageSourceStat.add(kvp);
			pageSourceMenu.add("其他");
		}
		
		map.put("pageSourceMenu", pageSourceMenu);
		map.put("pageSource", pageSourceStat);
		map.put("trendName", trendName);
		map.put("timeList", timeList);
		map.put("trendData", trendData);
		return getObject(map);
	}
	
	/**
	 * 
	 * @Title: getTargetPageDetail
	 * @Description: 受访详情报表
	 * @param st
	 * @param ed
	 * @param pageIndex
	 * @param pageSize
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getTargetPageDetail")
	@ResponseBody
	public Object getTargetPageDetail(String st,String ed,Long pageIndex,Integer pageSize){
		Long siteId=LoginPersonUtil.getSiteId();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date stD=null;
		Date edD=null;
		try {
			if(AppUtil.isEmpty(st)){
				st=StatDateUtil.getToday2Str();
				stD=sdf.parse(st);
			}else{
				stD=sdf.parse(st);
			}
			if(!AppUtil.isEmpty(ed)){
				Long t=sdf.parse(ed).getTime()+(24*60*60*1000);
				edD=new Date(t);
				ed=sdf.format(edD);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Pagination page = siteChartMainService.getPageSourceDetail(siteId, st, ed, pageIndex, pageSize,false,"url");
		Long pvCount=siteChartMainService.getPageSourceCount(siteId, stD, edD, true,false);
		Long svCount=siteChartMainService.getPageSourceCount(siteId, stD, edD, false,false);
		if(svCount==0L) svCount=1L;
		@SuppressWarnings("unchecked")
		List<PageSourceVO> data=(List<PageSourceVO>) page.getData();
		List<PageSourceStatVO> list=new ArrayList<PageSourceStatVO>();
		//计算百分比
		java.text.DecimalFormat df=new java.text.DecimalFormat("###.##");   
		if(null!=data&&data.size()>0){
			for(PageSourceVO d:data){
				PageSourceStatVO vo=new PageSourceStatVO();
				vo.setPv(d.getPv());
				vo.setSv(d.getSv());
				vo.setReferer(d.getReferer());
				vo.setPvCent(String.valueOf((df.format((double)d.getPv()*100/pvCount))));
				vo.setSvCent(String.valueOf((df.format((double)d.getSv()*100/svCount))));
				list.add(vo);
			}
		}
		page.setData(list);
		return getObject(page);
	}
}
