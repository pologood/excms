package cn.lonsun.wechat.controller;

import cn.lonsun.core.base.entity.AMockEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.entity.KeyWordsMsgEO;
import cn.lonsun.wechatmgr.internal.entity.SubscribeMsgEO;
import cn.lonsun.wechatmgr.internal.service.IKeyWordsMsgService;
import cn.lonsun.wechatmgr.internal.service.ISubscribeMsgService;
import cn.lonsun.wechatmgr.vo.KeyWordsVO;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value= "/weChat/autoMsg", produces = { "application/json;charset=UTF-8" })
public class WeChatAutoMsgController extends BaseController {

	@Autowired
	private ISubscribeMsgService subscribeMsgService;
	
	@Autowired
	private IKeyWordsMsgService keyWordsMsgService;
	
	/**
	 * 
	 * @Title: index
	 * @Description: 自动回复列表页
	 * @return   Parameter
	 * @return  String   return type
	 * @throws
	 */
	@RequestMapping("index")
	public String index(){
		return "/wechat/auto_msg_index";
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

	/**
	 *
	 * @Title: getSubMsg
	 * @Description: 关注回复
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getJudge")
	@ResponseBody
	public Object getJudge(){
		Long siteId=LoginPersonUtil.getSiteId();
		if(null==siteId){
			return ajaxErr("权限不足");
		}else{
			Map<String,Object> map =new HashMap<String, Object>();
			map.put("siteId",siteId);
			map.put("msgType",SubscribeMsgEO.MSGTYPE.judge.toString());
			map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
			SubscribeMsgEO subMsg = subscribeMsgService.getEntity(SubscribeMsgEO.class,map);
			if(subMsg==null){
				subMsg=new SubscribeMsgEO();
			}
			return getObject(subMsg);
		}
	}
	//保存关注回复
	@RequestMapping("saveSubMsg")
	@ResponseBody
	public Object saveSubMsg(SubscribeMsgEO subMsg){
		Long siteId=LoginPersonUtil.getSiteId();
		if(null==siteId){
			return ajaxErr("权限不足");
		}else{
			subMsg.setSiteId(siteId);
			subscribeMsgService.saveOrUpdateEntity(subMsg);
			return getObject();
		}
	}
	//保存评价
	@RequestMapping("saveJudge")
	@ResponseBody
	public Object saveJudge(SubscribeMsgEO subMsg){
		subMsg.setMsgType(SubscribeMsgEO.MSGTYPE.judge.toString());
		Long siteId=LoginPersonUtil.getSiteId();
		if(null==siteId){
			return ajaxErr("权限不足");
		}else{
			subMsg.setSiteId(siteId);
			subscribeMsgService.saveOrUpdateEntity(subMsg);
			return getObject();
		}
	}


	/**
	 * 
	 * @Title: keyWordsPage
	 * @Description: 关键词页
	 * @param vo
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("keyWordsPage")
	@ResponseBody
	public Object keyWordsPage(KeyWordsVO vo){
		Object pg = getObject(keyWordsMsgService.getPage(vo));
		return pg;
	}
	
	/**
	 * 
	 * @Title: editKeywords
	 * @Description: 编辑关键词
	 * @param id
	 * @param map
	 * @return   Parameter
	 * @return  String   return type
	 * @throws
	 */
	@RequestMapping("editKeywords")
	public String editKeywords(Long id,ModelMap map){
		map.put("ID", id);
		return "/wechat/edit_keywords";
	}
	
	/**
	 * 
	 * @Title: saveKeyWords
	 * @Description: 保存关键词
	 * @param msgEO
	 * @param artList
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("saveKeywords")
	@ResponseBody
	public Object saveKeyWords(KeyWordsMsgEO msgEO,@RequestParam(value="artList[]",required=false)Long[] artList){
		keyWordsMsgService.saveKeywords(msgEO, artList);
		return getObject();
	}
	
	/**
	 * 
	 * @Title: deleteKeyWords
	 * @Description: 删除关键词
	 * @param ids
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("deleteKeywords")
	@ResponseBody
	public Object deleteKeyWords(@RequestParam(value="ids[]",required=false)Long[] ids){
		keyWordsMsgService.delete(KeyWordsMsgEO.class, ids);
		return getObject();
	}
	
	/**
	 * 
	 * @Title: getKeywords
	 * @Description: 获取关键词
	 * @param id
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getKeywords")
	@ResponseBody
	public Object getKeywords(Long id){
		return getObject(keyWordsMsgService.getKeywords(id));
	}
}
