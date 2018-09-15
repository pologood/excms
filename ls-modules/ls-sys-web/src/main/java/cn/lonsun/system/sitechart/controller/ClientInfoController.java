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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.system.sitechart.service.ISiteChartMainService;
import cn.lonsun.system.sitechart.vo.ChartStatVO;
import cn.lonsun.system.sitechart.vo.ClientVO;
import cn.lonsun.system.sitechart.vo.KVP;
import cn.lonsun.system.sitechart.vo.SearchVO;
import cn.lonsun.system.sitechart.webutil.LangAbbr;
import cn.lonsun.system.sitechart.webutil.StatDateUtil;
import cn.lonsun.util.LoginPersonUtil;

@Controller
@RequestMapping("clientInfo")
public class ClientInfoController extends BaseController {

	
	@Autowired
	private ISiteChartMainService siteChartMainService;
	
	@RequestMapping("index")
	public String index(ModelMap model){
		model.put("TODAY",StatDateUtil.getToday2Str());
		return "system/sitechart/v_clientInfo";
	}
	
	/**
	 * 
	 * @Title: getClientOs
	 * @Description: 终端操作系统报表
	 * @param vo
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getClientOs")
	@ResponseBody
	public Object getClientOs(SearchVO vo){
		Long siteId=LoginPersonUtil.getSiteId();
		Map<String,Object> map=new HashMap<String, Object>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String stD=null;
		String edD=null;
		Date start = null;
		Date end=null;
		if(vo.getSt()!=null){
			stD=sdf.format(vo.getSt());
		}else{
			stD=sdf.format(new Date());
		}
		if(null!=vo.getEd()){
			//含本日
			Calendar calendar = new GregorianCalendar(); 
		    calendar.setTime(vo.getEd()); 
		    calendar.add(Calendar.DATE,1);
		    end=calendar.getTime();
			edD=sdf.format(end);
		}
		try {
			start = vo.getSt()==null?sdf.parse(sdf.format(new Date())):vo.getSt();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<ClientVO> osList = siteChartMainService.getClientList(siteId, start, end,"os", false,null);
		List<String> menuOS=new ArrayList<String>();
		List<String> menuLine=new ArrayList<String>();
		List<KVP> osType=new ArrayList<KVP>();
		List<KVP> pcList=new ArrayList<KVP>();
		List<KVP> noPcList=new ArrayList<KVP>();
		List<KVP> osStat=new ArrayList<KVP>();
		Long pc=0L;
		Long noPc=0L;
		List<String> timeList = StatDateUtil.getDayForHour(start,end);
		List<ChartStatVO> data=new ArrayList<ChartStatVO>();
		int t=0;
		System.out.println(timeList);
		for(ClientVO os:osList){
			if("Other".equals(os.getTarget())){
				KVP kvp=new KVP();
				kvp.setName("其他");
				kvp.setValue(os.getNum());
				osType.add(kvp);
				osStat.add(kvp);
				menuOS.add("其他");
			}else{
				if("true".equals(os.getIsPC())){
					KVP kvp=new KVP();
					kvp.setName(os.getTarget());
					kvp.setValue(os.getNum());
					pc+=os.getNum();
					pcList.add(kvp);
					menuOS.add(os.getTarget());
				}else{
					KVP kvp=new KVP();
					kvp.setName(os.getTarget());
					kvp.setValue(os.getNum());
					noPc+=os.getNum();
					noPcList.add(kvp);
					menuOS.add(os.getTarget());
				}
				if(t<3){
					List<KVP> list = siteChartMainService.getVisitTrendByCod(stD, edD, siteId, false, "os", os.getTarget());
					menuLine.add(os.getTarget());
					ChartStatVO cs=new ChartStatVO();
					cs.setName(os.getTarget());
					cs.setType("line");
					List<String> d=new ArrayList<String>();
					k:for(String tl:timeList){
						for(KVP l:list){
							if(tl.contains(l.getName())){
								d.add(String.valueOf(l.getValue()));
								continue k;
							}
						}
						d.add("0");
					}
					cs.setData(d);
					data.add(cs);
					t++;
				}
			}
		}
		if(pc!=0L){
			KVP kvp1=new KVP();
			kvp1.setName("非移动端");
			kvp1.setValue(pc);
			osType.add(kvp1);
			menuOS.add("非移动端");
		}
		if(noPc!=0L){
			KVP kvp2=new KVP();
			kvp2.setName("移动端");
			kvp2.setValue(noPc);
			osType.add(kvp2);
			menuOS.add("移动端");
		}
		map.put("osType", osType);
		osStat.addAll(pcList);
		osStat.addAll(noPcList);
		map.put("osStat", osStat);
		map.put("menuOS", menuOS);
		map.put("timeList", timeList);
		map.put("lineData", data);
		map.put("menuLine", menuLine);
		return getObject(map);
	}
	
	/**
	 * 
	 * @Title: getBrowser
	 * @Description: 浏览器统计报表
	 * @param vo
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getBrowser")
	@ResponseBody
	public Object getBrowser(SearchVO vo){
		Long siteId=LoginPersonUtil.getSiteId();
		Map<String,Object> map=new HashMap<String, Object>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String stD=null;
		String edD=null;
		Date start = null;
		Date end=null;
		if(vo.getSt()!=null){
			stD=sdf.format(vo.getSt());
		}else{
			stD=sdf.format(new Date());
		}
		if(null!=vo.getEd()){
			//含本日
			Calendar calendar = new GregorianCalendar(); 
		    calendar.setTime(vo.getEd()); 
		    calendar.add(Calendar.DATE,1);
		    end=calendar.getTime();
			edD=sdf.format(end);
		}
		try {
			start = vo.getSt()==null?sdf.parse(sdf.format(new Date())):vo.getSt();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<KVP> browserList = siteChartMainService.getSource(start, end, siteId, "client");
		List<String> timeList = StatDateUtil.getDayForHour(start,end);
		List<ChartStatVO> lineData=new ArrayList<ChartStatVO>();
		List<String> browserMenu=new ArrayList<String>();
		List<String> browserLine=new ArrayList<String>();
		int t=0;
		for(KVP b:browserList){
			browserMenu.add(b.getName());
			if(t<3){
				browserLine.add(b.getName());
				List<KVP> cList = siteChartMainService.getVisitTrendByCod(stD, edD, siteId, false, "client", b.getName());
				ChartStatVO cs=new ChartStatVO();
				cs.setName(b.getName());
				cs.setType("line");
				List<String> d=new ArrayList<String>();
				k:for(String tl:timeList){
					for(KVP l:cList){
						if(tl.contains(l.getName())){
							d.add(String.valueOf(l.getValue()));
							continue k;
						}
					}
					d.add("0");
				}
				cs.setData(d);
				lineData.add(cs);
				t++;
			}
		}
		map.put("browserMenu", browserMenu);
		map.put("browserData", browserList);
		map.put("timeList", timeList);
		map.put("lineData", lineData);
		map.put("browserLine", browserLine);
		return getObject(map);
	}
	
	/**
	 * 
	 * @Title: getOtherInfo
	 * @Description: 分辨率与语言统计报表
	 * @param vo
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getOtherInfo")
	@ResponseBody
	public Object getOtherInfo(SearchVO vo){
		Long siteId=LoginPersonUtil.getSiteId();
		Map<String,Object> map=new HashMap<String, Object>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String stD=null;
		String edD=null;
		Date start = null;
		Date end=null;
		if(vo.getSt()!=null){
			stD=sdf.format(vo.getSt());
		}else{
			stD=sdf.format(new Date());
		}
		if(null!=vo.getEd()){
			//含本日
			Calendar calendar = new GregorianCalendar(); 
		    calendar.setTime(vo.getEd()); 
		    calendar.add(Calendar.DATE,1);
		    end=calendar.getTime();
			edD=sdf.format(end);
		}
		try {
			start = vo.getSt()==null?sdf.parse(sdf.format(new Date())):vo.getSt();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<KVP> reList = siteChartMainService.getRelList(siteId, start, end, "resolution", false, 5);
		List<KVP> langList = siteChartMainService.getRelList(siteId, start, end, "language", false, 5);
		List<String> reMenu=new ArrayList<String>();
		List<String> langMenu=new ArrayList<String>();
		List<KVP> reData=new ArrayList<KVP>();
		List<KVP> langData=new ArrayList<KVP>();
		Long num1=0L;
		Long num2=0L;
		Long other=0L;
		for(KVP re:reList){
				reMenu.add(re.getName());
				num1+=re.getValue();
				reData.add(re);
				
		}
		for(KVP lang:langList){
			String menu=LangAbbr.langAbbr.get((lang.getName()==null?"":lang.getName()).toLowerCase());
			if(menu==null){
				other+=lang.getValue();
			}else{
				langMenu.add(menu);
				lang.setName(menu);
				langData.add(lang);
			}
			num2+=lang.getValue();
		}
		Long count=siteChartMainService.getClientCount(siteId, start, end, false);
		if(count>num1){
			reMenu.add("其他");
			KVP kvp=new KVP();
			kvp.setName("其他");
			kvp.setValue(count-num1);
			reData.add(kvp);
		}
		if(count>num2||other>0){
			langMenu.add("其他");
			KVP kvp=new KVP();
			kvp.setName("其他");
			kvp.setValue(count-num2+other);
			langData.add(kvp);
		}
		map.put("reMenu", reMenu);
		map.put("reData", reData);
		map.put("langMenu", langMenu);
		map.put("langData", langData);
		return getObject(map);
	}
}
