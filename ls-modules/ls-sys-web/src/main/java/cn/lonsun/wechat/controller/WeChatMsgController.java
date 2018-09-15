package cn.lonsun.wechat.controller;

import cn.lonsun.system.sitechart.vo.ChartStatVO;
import cn.lonsun.wechatmgr.internal.service.IWeChatMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.entity.SubscribeMsgEO;
import cn.lonsun.wechatmgr.internal.service.ISubscribeMsgService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/weChat/msgMgr")
public class WeChatMsgController extends BaseController {

	@Autowired
	private ISubscribeMsgService subscribeMsgService;

	@Resource
	private IWeChatMsgService weChatMsgService;

	@RequestMapping("index")
	public String index(){
		return "/wechat/msg_index";
	}

	/**
	 *
	 * @Title: getSubMsg
	 * @Description: 关注回复
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getSubMsg")
	@ResponseBody
	public Object getSubMsg(){
		Long siteId=LoginPersonUtil.getSiteId();
		if(null==siteId){
			return ajaxErr("权限不足");
		}else{
			SubscribeMsgEO subMsg = subscribeMsgService.getMsgBySite(siteId);
			if(subMsg==null){
				subMsg=new SubscribeMsgEO();
			}
			return getObject(subMsg);
		}
	}

	@RequestMapping("saveSubMsg")
	@ResponseBody
	public Object saveSubMsg(SubscribeMsgEO subMsg){
		Long siteId=LoginPersonUtil.getSiteId();
		if(null==siteId){
			return ajaxErr("权限不足");
		}else{
			subMsg.setSiteId(siteId);
			subscribeMsgService.save(subMsg);
			return getObject();
		}
	}


	@RequestMapping("statistics")
	public String statistics(){
		return "/wechat/msg_statistics";
	}


	/**
	 * 一周数据统计
	 * @return
	 */
	@RequestMapping("getWeekCharts")
	@ResponseBody
	public Object getWeekCharts(){
		Long siteId=LoginPersonUtil.getSiteId();
		if(null==siteId){
			return ajaxErr("权限不足");
		}
		Map<String,Object> map=new HashMap<String, Object>();
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		List<String> days = new ArrayList<String>();
		Date beginDate = new Date();
		for(int i=6;i>=0;i--){
			Calendar date = Calendar.getInstance();
			date.setTime(beginDate);
			date.set(Calendar.DATE, date.get(Calendar.DATE) - i);
			days.add(dft.format(date.getTime()));
		}
		List<String> allCounts = weChatMsgService.getWeekCount(days,null,siteId);
		List<String> replyCounts = weChatMsgService.getWeekCount(days,1,siteId);
		List<ChartStatVO> data=new ArrayList<ChartStatVO>();
		ChartStatVO cs = new ChartStatVO();
		cs.setName("消息总数");
		cs.setType("line");
		cs.setData(allCounts);
		data.add(cs);
		ChartStatVO rc = new ChartStatVO();
		rc.setName("已回总数");
		rc.setType("line");
		rc.setData(replyCounts);
		data.add(rc);
		map.put("xAxis", days);
		map.put("series", data);
		return getObject(map);
	}

	/**
	 * 一周数据统计
	 * @return
	 */
	@RequestMapping("getUnitsCount")
	@ResponseBody
	public Object getUnitsCount(){
		Long siteId=LoginPersonUtil.getSiteId();
		if(null==siteId){
			return ajaxErr("权限不足");
		}
		return getObject(weChatMsgService.getUnitsCount(siteId));
	}
}
