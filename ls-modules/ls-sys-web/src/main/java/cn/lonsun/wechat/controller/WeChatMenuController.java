package cn.lonsun.wechat.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.entity.WeChatMenuEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatMenuService;
import cn.lonsun.wechatmgr.internal.wechatapiutil.ApiUtil;

@Controller
@RequestMapping("/weChat/menuMgr")
public class WeChatMenuController extends BaseController {

	
    @Autowired
    private IWeChatMenuService weChatMenuService;
    
    @RequestMapping("index")
    public String index(){
		return "/wechat/menu_config";
    }
    
    /**
     * 
     * @Title: createMenuS
     * @Description: 创建微信菜单
     * @return   Parameter
     * @return  Object   return type
     * @throws
     */
	@RequestMapping("createMenu")
	@ResponseBody
	public Object createMenuS(){
		Long siteId=LoginPersonUtil.getSiteId();
		JSONObject json = ApiUtil.createMenu(siteId);
		return json;
	}
	
	/**
	 * 
	 * @Title: getMenus
	 * @Description: 获取菜单列表
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getMenus")
	@ResponseBody
	public Object getMenus(){
		Long siteId=LoginPersonUtil.getSiteId();
		return JSONArray.fromObject(weChatMenuService.getMenuBySite(siteId)).toString();
	}
	
	/**
	 * 
	 * @Title: menuEdit
	 * @Description: 菜单编辑
	 * @param map
	 * @param id
	 * @return   Parameter
	 * @return  String   return type
	 * @throws
	 */
	@RequestMapping("menuEdit")
	public String menuEdit(ModelMap map,Long id){
		Long siteId=LoginPersonUtil.getSiteId();
		map.put("ID", id);
		map.put("LEVE1", weChatMenuService.get1Leve(siteId));
		return "/wechat/menu_edit";
	}
	
	/**
	 * 
	 * @Title: getMenuById
	 * @Description: 获取菜单详情
	 * @param id
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getMenuById")
	@ResponseBody
	public Object getMenuById(Long id){
		return getObject(weChatMenuService.getEntity(WeChatMenuEO.class, id));
	}
	
	/**
	 * 
	 * @Title: saveMenu
	 * @Description: 保存菜单
	 * @param menu
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("saveMenu")
	@ResponseBody
	public Object saveMenu(WeChatMenuEO menu){
		menu.setSiteId(LoginPersonUtil.getSiteId());
		weChatMenuService.saveOrUpdateEntity(menu);
		return getObject();
	}
	
	/**
	 * 
	 * @Title: deleteMenu
	 * @Description: 删除菜单
	 * @param id
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("deleteMenu")
	@ResponseBody
	public Object deleteMenu(Long id){
		weChatMenuService.deleteMenu(id);
		return getObject();
	}
}
