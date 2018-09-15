package cn.lonsun.system.sitechart.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.solr.service.IIndexKeyWordsService;
import cn.lonsun.solr.vo.KeyWordsCountVO;
import cn.lonsun.system.sitechart.service.ISiteChartMainService;
import cn.lonsun.system.sitechart.vo.KVP;
import cn.lonsun.system.sitechart.vo.LocationVO;
import cn.lonsun.system.sitechart.vo.SourceType;
import cn.lonsun.system.sitechart.webutil.SearchEnginesUtil;
import cn.lonsun.system.sitechart.webutil.StatDateUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("siteChartIndex")
public class SiteChartIndexController extends BaseController {

	@Autowired
	private ISiteChartMainService siteChartMainService;
	@Autowired
	private IIndexKeyWordsService indexKeyWordsService;

	@RequestMapping("index")
	@ResponseBody
	public Object index(int scope){
		Map<String,Object> map=new HashMap<String, Object>();
		Long siteId=LoginPersonUtil.getSiteId();
		Long todayTime=StatDateUtil.getToday();
		Date today=new Date(todayTime);
		if(scope == 0) {
			List<LocationVO> visitLocation = siteChartMainService.getPvByLocation(siteId,today,null,"city",false,11);
			List<String> cityList=new ArrayList<String>();
			List<Long> cityData=new ArrayList<Long>();
			if(visitLocation!=null && !visitLocation.isEmpty()){
				for(LocationVO vl:visitLocation){
					cityList.add(vl.getLocation());
					cityData.add(vl.getVisits());
				}
			}
			map.put("cityList", cityList);
			map.put("cityData", cityData);
		} else if(scope == 1) {
			//关键词搜索
			List<String> kwList=new ArrayList<String>();
			List<Long> kwNumList=new ArrayList<Long>();
			List<KeyWordsCountVO> list = indexKeyWordsService.getKeyWordsCount(siteId, null, null);
			if(null!=list){
				for(KeyWordsCountVO li:list){
					if(!StringUtils.isEmpty(li.getKeyWord())) {
						kwList.add(li.getKeyWord());
						kwNumList.add(li.getCounts());
					}
				}
			}
			map.put("kwList", kwList);
			map.put("kwNumList", kwNumList);
		} else if(scope == 2) {
			//搜索来源
			List<String> syList=new ArrayList<String>();
			List<KVP> outVisitSource=new ArrayList<KVP>();
			List<KVP> sourceType = siteChartMainService.getSource(today, null, siteId, "sourceType");
			List<KVP> searchEngine = siteChartMainService.getSource(today, null, siteId, "searchEngine");
			if(null!=sourceType&&null!=searchEngine&&searchEngine.size()>0){
				for(KVP st:sourceType){
					if(!StringUtils.isEmpty(st.getName())){
						syList.add(st.getName());
						if(SourceType.SE.equals(st.getName())){
							for(KVP se:searchEngine){
								if(!StringUtils.isEmpty(se.getName()) && !StringUtils.isEmpty(SearchEnginesUtil.initSE.get(se.getName()))) {
									se.setName(SearchEnginesUtil.initSE.get(se.getName()));
									outVisitSource.add(se);
									syList.add(se.getName());
								}
							}
						}else{
							outVisitSource.add(st);
						}
					}
				}
			}else{
				if(null!=sourceType){
					for(KVP st:sourceType){
						if(!StringUtils.isEmpty(st.getName())) {
							syList.add(st.getName());
						}
					}
				}
			}
			map.put("syList", syList);
			map.put("innerVisitSource", sourceType);
			map.put("outVisitSource", outVisitSource);
		} else if(scope == 3) {
			List<KVP> visitTrend = siteChartMainService.getVisitTrend(StatDateUtil.getToday2Str(), null, false, siteId);
			List<Long> trendData=new ArrayList<Long>();
			List<String> timeList = StatDateUtil.getTodayForHour();
			k:for(String t:timeList){
				for(KVP vt:visitTrend){
					if(t.contains(vt.getName())){
						trendData.add(vt.getValue());
						continue k;
					}
				}
				trendData.add(0L);
			}
			map.put("trendData", trendData);
		}
		return getObject(map);
	}
}
