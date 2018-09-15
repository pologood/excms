package cn.lonsun.system.sitechart.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.sitechart.internal.entity.SiteChartMainEO;
import cn.lonsun.system.sitechart.internal.entity.SiteChartTrendEO;
import cn.lonsun.system.sitechart.service.ISiteChartMainService;
import cn.lonsun.system.sitechart.service.ISiteChartTrendService;
import cn.lonsun.system.sitechart.vo.ChartStatVO;
import cn.lonsun.system.sitechart.vo.CountStatVO;
import cn.lonsun.system.sitechart.vo.LocationVO;
import cn.lonsun.system.sitechart.vo.MainCountVO;
import cn.lonsun.system.sitechart.vo.SearchVO;
import cn.lonsun.system.sitechart.vo.VisitDeatilPageVo;
import cn.lonsun.system.sitechart.webutil.SearchEnginesUtil;
import cn.lonsun.system.sitechart.webutil.StatDateUtil;
import cn.lonsun.util.LoginPersonUtil;

@Controller
@RequestMapping(value = "/siteChartMain", produces = { "application/json;charset=UTF-8" })
public class SiteChartMainController extends BaseController {

	@Autowired
	private ISiteChartMainService siteChartMainService;
	@Autowired
	private ISiteChartTrendService siteChartTrendService;
	
	@RequestMapping("getIndexMain")
	@ResponseBody
	public Object getIndexMain(){
		List<LocationVO> mainList = siteChartMainService.getLocationList(null, null, null, 11);
		//List<StatVO> mainList = siteChartMainService.getTraffic(null, null, null);
		return getObject(mainList);
	}
	
	
	@RequestMapping("vMain")
	public String vMain(ModelMap model){
		model.put("TODAY",StatDateUtil.getToday2Str());
		return "system/sitechart/v_main";
	}
	
	/**
	 * 
	 * @Title: getSurvey
	 * @Description: 统计概览
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getSurvey")
	@ResponseBody
	public Object getSurvey(){
		Long siteId=LoginPersonUtil.getSiteId();
		List<MainCountVO> list=new ArrayList<MainCountVO>();
		
		MainCountVO vTady = siteChartTrendService.getMainCount(siteId, StatDateUtil.getToday(), null);
		vTady.setMold("今日");
		list.add(vTady);
		
		MainCountVO vYesterday = siteChartTrendService.getMainCount(siteId, StatDateUtil.getYesterday(), StatDateUtil.getToday());
		vYesterday.setMold("昨日");
		list.add(vYesterday);
		
		/*需要时可开启今日预计*/
//		MainCountVO vTadyExpect=new MainCountVO();
//		vTadyExpect.setMold("今日预计");
//		MainCountVO v30Day = siteChartTrendService.getMainCount(siteId, StatDateUtil.get30Time(), StatDateUtil.getToday());
//		CountStatVO countStatVO30Day = siteChartTrendService.getCount(siteId, StatDateUtil.get30Time(), StatDateUtil.getToday());
//		MainCountVO v7Day = siteChartTrendService.getMainCount(siteId, StatDateUtil.get7Time(), StatDateUtil.getToday());
//		CountStatVO countStatVO7Day = siteChartTrendService.getCount(siteId, StatDateUtil.get7Time(), StatDateUtil.getToday());
//		Long count30Day=countStatVO30Day.getNum()==null?1L:countStatVO30Day.getNum();
//		Long count7Day=countStatVO7Day.getNum()==null?1L:countStatVO7Day.getNum();
//		vTadyExpect.setIp(Long.parseLong(String.valueOf(VisitExpect.getExpection(v30Day.getIp(), count30Day, v7Day.getIp(), count7Day))));
//		vTadyExpect.setPv(Long.parseLong(String.valueOf(VisitExpect.getExpection(v30Day.getPv(), count30Day, v7Day.getPv(), count7Day))));
//		vTadyExpect.setUv(Long.parseLong(String.valueOf(VisitExpect.getExpection(v30Day.getUv(), count30Day, v7Day.getUv(), count7Day))));
//		vTadyExpect.setNuv(Long.parseLong(String.valueOf(VisitExpect.getExpection(v30Day.getNuv(), count30Day, v7Day.getNuv(), count7Day))));
//		vTadyExpect.setSv(Long.parseLong(String.valueOf(VisitExpect.getExpection(v30Day.getSv(), count30Day, v7Day.getSv(), count7Day))));
//		list.add(vTadyExpect);
		
		MainCountVO vYesterday2Now = siteChartTrendService.getMainCount(siteId, StatDateUtil.getYesterday(), StatDateUtil.getYesterday2Now());
		vYesterday2Now.setMold("昨日此时");
		list.add(vYesterday2Now);		

		MainCountVO v90Day = siteChartTrendService.getMainCount(siteId, StatDateUtil.get90Time(), null);
		CountStatVO countStatVO2 = siteChartTrendService.getCount(siteId, StatDateUtil.get90Time(), StatDateUtil.getToday());
		Long count90Day=countStatVO2.getNum()/24;
		if(count90Day==0l) count90Day=1L;
		MainCountVO v90DayAvg=new MainCountVO();
		v90DayAvg.setIp(Long.parseLong(String.valueOf(Math.round(v90Day.getIp()/count90Day))));
		v90DayAvg.setPv(Long.parseLong(String.valueOf(Math.round(v90Day.getPv()/count90Day))));
		v90DayAvg.setUv(Long.parseLong(String.valueOf(Math.round(v90Day.getUv()/count90Day))));
		v90DayAvg.setNuv(Long.parseLong(String.valueOf(Math.round(v90Day.getNuv()/count90Day))));
		v90DayAvg.setSv(Long.parseLong(String.valueOf(Math.round(v90Day.getSv()/count90Day))));
		v90DayAvg.setMold("近90日平均"); 
		list.add(v90DayAvg);
		
		MainCountVO vAll = siteChartTrendService.getMainCount(siteId, null, null);
		vAll.setMold("历史累计");
		list.add(vAll);
		
		return getObject(list);
	}
	
	/**
	 * 
	 * @Title: visitChart
	 * @Description: 访问趋势
	 * @param vo
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("visitChart")
	@ResponseBody
	public Object visitChart(SearchVO vo){
		Long st=null;
		Long ed=null;
		if(vo.getSt()==null){
			st=StatDateUtil.get7Time();
		}else{
			st=vo.getSt().getTime();
		}
		if(vo.getEd()!=null){
			ed=vo.getEd().getTime()+24*60*60*1000;//含当日
		}
		Map<String,Object> map=new HashMap<String, Object>();
		
		Long siteId=LoginPersonUtil.getSiteId();
		List<String> xAxis=null;
		if(vo.getSt()==null){
			xAxis=StatDateUtil.get7DayForHour();
		}else{
			xAxis=StatDateUtil.getDayForHour(vo.getSt(), vo.getEd());
		}
		List<SiteChartTrendEO> trendList = siteChartTrendService.getList(siteId, st, ed);
		boolean mark=false;
		String nowForHour=StatDateUtil.getNowForHour();
		List<String> pv=new ArrayList<String>();
		List<String> uv=new ArrayList<String>();
		List<String> nuv=new ArrayList<String>();
		List<String> ip=new ArrayList<String>();
		List<String> sv=new ArrayList<String>();
		k:for(String x:xAxis){
			for(SiteChartTrendEO l:trendList){
				if(x.equals((l.getDay()+" "+l.getHour()))){
					pv.add(String.valueOf(l.getPv()));
					uv.add(String.valueOf(l.getUv()));
					nuv.add(String.valueOf(l.getNuv()));
					ip.add(String.valueOf(l.getIp()));
					sv.add(String.valueOf(l.getSv()));
					continue k;
				}
			}
			if(nowForHour.equals(x)&&!mark){
				mark=true;
			}
			if(mark){
				pv.add("-");
				uv.add("-");
				nuv.add("-");
				ip.add("-");
				sv.add("-");
			}else{
				pv.add("0");
				uv.add("0");
				nuv.add("0");
				ip.add("0");
				sv.add("0");
			}
		}
		List<ChartStatVO> data=new ArrayList<ChartStatVO>();
		ChartStatVO pvData=new ChartStatVO();
		pvData.setName("PV");
		pvData.setType("line");
		pvData.setData(pv);
		data.add(pvData);
		
		ChartStatVO uvData=new ChartStatVO();
		uvData.setName("UV");
		uvData.setType("line");
		uvData.setData(uv);
		data.add(uvData);
		
		ChartStatVO nuvData=new ChartStatVO();
		nuvData.setName("NUV");
		nuvData.setType("line");
		nuvData.setData(nuv);
		data.add(nuvData);
		
		ChartStatVO ipData=new ChartStatVO();
		ipData.setName("IP");
		ipData.setType("line");
		ipData.setData(ip);
		data.add(ipData);
		
		ChartStatVO svData=new ChartStatVO();
		svData.setName("SV");
		svData.setType("line");
		svData.setData(sv);
		data.add(svData);
		
		map.put("xAxis", xAxis);
		map.put("series", data);
		return getObject(map);
		
	}
	
	@RequestMapping("visitDetail")
	public String visitDetail(ModelMap model){
		model.put("TODAY",StatDateUtil.getToday2Str());
		return "system/sitechart/v_detail";
	}
	
	/**
	 * 
	 * @Title: getVisitDetail
	 * @Description: 访问明细分页列表
	 * @param pageVO
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getVisitDetail")
	@ResponseBody
	public Object getVisitDetail(VisitDeatilPageVo pageVO){
		if(null!=pageVO.getEd()){
			//含本日
			Calendar calendar = new GregorianCalendar(); 
		    calendar.setTime(pageVO.getEd()); 
		    calendar.add(Calendar.DATE,1);
		    pageVO.setEd(calendar.getTime());
		}
		Pagination page = siteChartMainService.getVisitPage(pageVO);
		@SuppressWarnings("unchecked")
		List<SiteChartMainEO> data=(List<SiteChartMainEO>) page.getData();
		for(SiteChartMainEO d:data){
			d.setSearchEngine(SearchEnginesUtil.initSE.get(d.getSearchEngine()));
		}
		page.setData(data);
		return getObject(page);
		
	}
}
