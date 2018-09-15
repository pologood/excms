package cn.lonsun.wechat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.entity.UserGroupEO;
import cn.lonsun.wechatmgr.internal.entity.WeChatUserEO;
import cn.lonsun.wechatmgr.internal.service.IUserGroupService;
import cn.lonsun.wechatmgr.internal.service.IWeChatUserService;
import cn.lonsun.wechatmgr.internal.wechatapiutil.ApiUtil;
import cn.lonsun.wechatmgr.internal.wechatapiutil.SynUsers;
import cn.lonsun.wechatmgr.internal.wechatapiutil.UserGroup;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;

/**
 * 
 * @ClassName: WeChatUserController
 * @Description: 关注用户管理
 * @author Hewbing
 * @date 2016年4月7日 下午5:23:14
 *
 */
@Controller
@RequestMapping("/weChat/userMgr")
public class WeChatUserController extends BaseController {
	
	@Autowired
	private IUserGroupService userGroupService;
	@Autowired
	private IWeChatUserService weChatUserService;
    @Autowired
    private TaskExecutor taskExecutor;
    
    @Value("${fileServer}")
    private String fileServer;
    
	@RequestMapping("index")
	public String index(){
		return "/wechat/user_index";
	}
	
	/**
	 * 
	 * @Title: userPage
	 * @Description: 关注用户
	 * @param userVO
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	
	@RequestMapping("userPage")
	@ResponseBody
	public Object userPage(WeChatUserVO userVO){
		Long siteId=LoginPersonUtil.getSiteId();
		if(null==siteId){
			return ajaxErr("权限不足");
		}else{
			userVO.setSiteId(siteId);
			Pagination page=weChatUserService.getUserPage(userVO);
			List<UserGroupEO> glist = userGroupService.getListBySite(siteId);
			Map<String,String> map=new HashMap<String,String>();
			for(UserGroupEO gl:glist){
				map.put(gl.getGroupid().toString(),gl.getName());
			}
			@SuppressWarnings("unchecked")
			List<WeChatUserEO> ulist=(List<WeChatUserEO>) page.getData();
			for(WeChatUserEO u:ulist){
				String gpn=map.get(u.getGroupid().toString());
				u.setGroupName(gpn==null?"未分组":gpn);
			}
			page.setData(ulist);
			return getObject(page);
		}
	}
	
	@RequestMapping("sendMsg")
	@ResponseBody
	public Object sendMsg(String content,String openid){
//		Long siteId=LoginPersonUtil.getSiteId();
//		 ApiUtil.getUserList(siteId);//ApiUtil.sendMsg(null);//ApiUtil.customSend(content,openid);
		return getObject(fileServer);
	}
	
	/**
	 * 
	 * @Title: createGroup
	 * @Description: 创建分组
	 * @param groupName
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("createGroup")
	@ResponseBody
	public Object createGroup(String groupName){
		JSONObject json = ApiUtil.createGroup(groupName);
		return json;
	}
	/**
	 * 
	 * @Title: getGroups
	 * @Description: 获取分组
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getGroups")
	@ResponseBody
	public Object getGroups(){
		Long siteId=LoginPersonUtil.getSiteId();
		List<UserGroupEO> list = userGroupService.getListBySite(siteId);
		return getObject(list);
	}
	
	/**
	 * 
	 * @Title: batchUpdateGroup
	 * @Description: 批量修改分组
	 * @param openids
	 * @param groupId
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("batchUpdateGroup")
	@ResponseBody
	public Object batchUpdateGroup(String[] openids,Long groupId){
		JSONObject json = ApiUtil.batchUpdateGroup(openids, groupId);
		return json;
	}
	/**
	 * 
	 * @Title: moveTo
	 * @Description: 用户移动分组
	 * @param ids
	 * @param groupid
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("moveTo")
	@ResponseBody
	public Object moveTo(@RequestParam(value="ids[]",required=false)String[] ids,Long groupid){
		JSONObject json = ApiUtil.batchUpdateGroup(ids, groupid);
		if(json.getInt("errcode")==0){
			weChatUserService.updateGroupByOpenid(ids, groupid);
			return json;
		}else{
			return json;
		}
	}
	/**
	 * 
	 * @Title: userGroup
	 * @Description: 
	 * @return   Parameter
	 * @return  String   return type
	 * @throws
	 */
	@RequestMapping("userGroup")
	public String userGroup(){
		return "/wechat/user_group";
		
	}
	
	/**
	 * 
	 * @Title: groupPage
	 * @Description: 分组列表
	 * @param pageIndex
	 * @param pageSize
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("groupPage")
	@ResponseBody
	public Object groupPage(Long pageIndex,Integer pageSize){
		Long siteId=LoginPersonUtil.getSiteId();
		return getObject(userGroupService.getpage(siteId, pageIndex, pageSize));
	}
	
	/**
	 * 
	 * @Title: editGroup
	 * @Description: 编辑分组
	 * @param id
	 * @param map
	 * @return   Parameter
	 * @return  String   return type
	 * @throws
	 */
	@RequestMapping("editGroup")
	public String editGroup(Long id,ModelMap map){
		map.put("ID", id);
		return "/wechat/edit_group";
	}
	
	/**
	 * 
	 * @Title: getGroup
	 * @Description:获取分组详情
	 * @param id
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getGroup")
	@ResponseBody
	public Object getGroup(Long id){
		UserGroupEO _eo = userGroupService.getEntity(UserGroupEO.class, id);
		if(AppUtil.isEmpty(_eo)){
			_eo=new UserGroupEO();
		}
		return getObject(_eo);
	}
	
	/**
	 * 
	 * @Title: saveGroup
	 * @Description: 保存分组
	 * @param _eo
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("saveGroup")
	@ResponseBody
	public Object saveGroup(UserGroupEO _eo){
		Long siteId=LoginPersonUtil.getSiteId();
		_eo.setSiteId(siteId);
		if(_eo.getGroupid()!=null){
			JSONObject json = ApiUtil.updateGroup(_eo.getGroupid(), _eo.getName());
			if(json.getInt("errcode")!=0){
				return ajaxErr("修改出错");
			}
		}else{
			JSONObject json = ApiUtil.createGroup(_eo.getName());
			if(json.toString().contains("errcode")){
				return ajaxErr("新增出错");
			}else{
				UserGroup ug=(UserGroup) JSONObject.toBean(json.getJSONObject("group"),UserGroup.class);
				_eo.setGroupid(ug.getId());
			}
		}
		userGroupService.saveOrUpdateEntity(_eo);
		return getObject();
	}
	/**
	 * 
	 * @Title: deleteGroup
	 * @Description: 删除分组
	 * @param id
	 * @param groupid
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("deleteGroup")
	@ResponseBody
	public Object deleteGroup(Long id,Long groupid){
		JSONObject json = ApiUtil.deleteGroup(groupid);
		if("{}".equals(json.toString())||json.getInt("errcode")==0){
			userGroupService.delete(UserGroupEO.class, id);
			return getObject();
		}else{
			return ajaxErr("删除出错");
		}
	}
	
	/**
	 * 
	 * @Title: synUsers
	 * @Description:同步用户
	 * @param request
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("synUsers")
	@ResponseBody
	public Object synUsers(HttpServletRequest request){
		Long siteId=LoginPersonUtil.getSiteId();
		Long userId=LoginPersonUtil.getUserId();
		SynUsers synUser=new SynUsers();
		synUser.setSiteId(siteId);
		synUser.setUserId(userId);
		Thread thread = new Thread(synUser);
        thread.start();

//		weChatUserService.removeBySite(siteId);
//		List<String> uList=ApiUtil.getUserList();
//		for(String li:uList){
//			WeChatUserEO uInfo = ApiUtil.getUserInfo(li, siteId);
//			uInfo.setSiteId(siteId);
//			uInfo.setCreateDate(new Date(uInfo.getSubscribe_time()*1000));
//			weChatUserService.saveEntity(uInfo);
//		}
		return getObject();
	}
	
	/**
	 * 
	 * @Title: synGroup
	 * @Description: 同步分组
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("synGroup")
	@ResponseBody
	public Object synGroup(){
		Long siteId=LoginPersonUtil.getSiteId();
		userGroupService.deleteBySite(siteId);
		 JSONArray uList = ApiUtil.getGroupList();
		 List<UserGroup> glist=JSONArray.toList(uList, UserGroup.class);
		for(UserGroup li:glist){
			UserGroupEO _eo=new UserGroupEO();
			_eo.setName(li.getName());
			_eo.setGroupid(li.getId());
			_eo.setSiteId(siteId);
			userGroupService.saveEntity(_eo);
		}
		return getObject();
	}
}
