/*
 * IndexController.java         2015年8月20日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.charts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.solr.service.IIndexKeyWordsService;
import cn.lonsun.solr.vo.KeyWordsCountVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * TODO <br/>
 * 
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年8月20日 <br/>
 */
@Controller
@RequestMapping("/charts")
public class ChartsController extends BaseController {
	private static final Logger logger = LoggerFactory
			.getLogger(ChartsController.class);

	@Autowired
	private IIndexKeyWordsService indexKeyWordsService;
    @Autowired
    private IBaseContentService baseService;
    
	@RequestMapping("index")
	public String desktop(HttpServletRequest request,
			HttpServletResponse response, Model model) {

		return "/charts/index";
	}

	@RequestMapping("statistics")
	public String statistics(ModelMap map){
		Long siteId = LoginPersonUtil.getSiteId();
		SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
		String cnzzSite=siteEO.getStationId();
		String password=siteEO.getStationPwd();
		map.put("cnzzSite", cnzzSite);
		map.put("password", password);
		return "/desktop/statistics";
		
	}
	@RequestMapping("getData")
	@ResponseBody
	public Object getData() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> llList = new ArrayList<String>();
		List<CnzzVisit> fwlyList = new ArrayList<CnzzVisit>();
		List<CnzzVisit> ssyqList = new ArrayList<CnzzVisit>();
		List<String> syList = new ArrayList<String>();
		List<String> prv=new ArrayList<String>();
		List<Integer> num=new ArrayList<Integer>();
		List<String> kwList=new ArrayList<String>();
		List<Long> kwNumList=new ArrayList<Long>();
		Long siteId = LoginPersonUtil.getSiteId();
		//关键词搜索
		List<KeyWordsCountVO> list = indexKeyWordsService.getKeyWordsCount(siteId, null, null);
		if(null!=list){
			for(KeyWordsCountVO li:list){
				kwList.add(li.getKeyWord());
				kwNumList.add(li.getCounts());
			}
		}
		map.put("kwList", kwList);
		map.put("kwNumList", kwNumList);
		try {
			// 流量分析
			String llfx = CnzzChartsUtil.getChartsData(CnzzChartsUtil.FLOW_ANALYSIS, siteId, null, null);
			logger.debug("getChartsData >>> " + llfx);
			System.out.println(llfx);
			if (!llfx.contains("error")) {
				JSONObject ll = JSONObject.fromObject(llfx);
				JSONArray items = ll.getJSONObject("data").getJSONObject("fluxList").getJSONArray("items");
				@SuppressWarnings("unchecked")
				List<FlowAnalysis> flowAnalysis = (List<FlowAnalysis>) JSONArray.toCollection(items, FlowAnalysis.class);
				for (FlowAnalysis li : flowAnalysis) {
					if ("-".equals(li.getPv()))
						continue;
					llList.add(li.getPv());
				}
				map.put("llList", llList);
			}
		} catch (Exception e) {
			map.put("llList", llList);
			map.put("fwlyList", fwlyList);
			map.put("ssyqList", ssyqList);
			// 索引
			map.put("syList", syList);	
			//e.printStackTrace();
			return getObject(map);
		}
		
		//
		try{
		String rel=CnzzChartsUtil.getChartsData(CnzzChartsUtil.VISIT_LOCATION, siteId, null, null);
		JSONObject json = JSONObject.fromObject(rel);
		@SuppressWarnings("unchecked")
		Set<String> keyset = json.getJSONObject("data").getJSONObject("provinceList").getJSONObject("items").keySet();
		@SuppressWarnings("unchecked")
		Collection<JSONObject> values = json.getJSONObject("data").getJSONObject("provinceList").getJSONObject("items").values();
		String[] arr=keyset.toArray(new String[keyset.size()]);
		JSONObject[] val = values.toArray(new JSONObject[values.size()]);
		if(arr.length>=11){
			for(int i=0;i<11;i++){
				prv.add(arr[i]);
			}
		}else{
			for(int i=0;i<arr.length;i++){
				prv.add(arr[i]);
			}
			for(int i=arr.length;i<=11;i++){
				prv.add("");
			}
		}
		
		if(val.length>=11){
			for(int i=0;i<11;i++){
				num.add(val[i].getInt("pv"));
			}
		}else{
			for(int i=0;i<val.length;i++){
				num.add(val[i].getInt("pv"));
			}
		}

		map.put("provList", prv);
		map.put("proNumList", num);
		System.out.println(prv);
		System.out.println(num);
		} catch (Exception e) {
			map.put("llList", llList);
			map.put("fwlyList", fwlyList);
			map.put("ssyqList", ssyqList);
			map.put("syList", syList);	
			//e.printStackTrace();
			return getObject(map);
		}	
		
		try {
			String fwly = CnzzChartsUtil.getChartsData(
					CnzzChartsUtil.VISIT_SOURCE, siteId, null, null);// 来源
			String ssyq = CnzzChartsUtil.getChartsData(
					CnzzChartsUtil.SEARCH_ENGINES, siteId, null, null);// 引擎
			if (!fwly.contains("error") && !ssyq.contains("error")) {
				// 访问来源处理
				JSONObject fw = JSONObject.fromObject(fwly);
				JSONObject _fw = fw.getJSONObject("data").getJSONObject("flash");
				@SuppressWarnings("unchecked")
				List<Integer> fwly_val = (List<Integer>) JSONArray.toCollection((JSONArray) _fw.getJSONArray("data").get(0),Integer.class);
				@SuppressWarnings("unchecked")
				List<String> fwly_name = (List<String>) JSONArray.toCollection((JSONArray) _fw.getJSONArray("title").get(0),String.class);
				System.out.println(fwly_val);
				System.out.println(fwly_name);
				// 位置反转
				Collections.rotate(fwly_val, 2);
				Collections.rotate(fwly_name, 2);
				System.out.println(fwly_val);
				System.out.println(fwly_name);
				for (int i = 0; i < fwly_val.size(); i++) {
					CnzzVisit cv = new CnzzVisit();
					cv.setValue(fwly_val.get(i));
					cv.setName(fwly_name.get(i));
					fwlyList.add(cv);
					syList.add(fwly_name.get(i));
				}
				map.put("fwlyList", fwlyList);

				// 搜索引擎处理
				JSONObject ss = JSONObject.fromObject(ssyq);
				JSONObject _ss = ss.getJSONObject("data").getJSONObject("flash");
				@SuppressWarnings("unchecked")
				List<Integer> ssyq_val = (List<Integer>) JSONArray.toCollection((JSONArray) _ss.getJSONArray("data").get(0),Integer.class);
				@SuppressWarnings("unchecked")
				List<String> ssyq_name = (List<String>) JSONArray.toCollection((JSONArray) _ss.getJSONArray("title").get(0),String.class);
				for (int i = 0; i < fwly_val.size(); i++) {
					if ("搜索引擎".equals(fwly_name.get(i)))
						continue;
					CnzzVisit cv = new CnzzVisit();
					cv.setValue(fwly_val.get(i));
					cv.setName(fwly_name.get(i));
					ssyqList.add(cv);
				}
				for (int i = 0; i < ssyq_val.size(); i++) {
					CnzzVisit cv = new CnzzVisit();
					cv.setValue(ssyq_val.get(i));
					cv.setName(ssyq_name.get(i));
					ssyqList.add(cv);
					syList.add(ssyq_name.get(i));
				}
				map.put("ssyqList", ssyqList);
				// 索引
				map.put("syList", syList);

			}
		} catch (Exception e) {
			map.put("llList", llList);
			map.put("fwlyList", fwlyList);
			map.put("ssyqList", ssyqList);
			map.put("syList", syList);	
			//e.printStackTrace();
			return getObject(map);
		}


		return getObject(map);
	}	
}