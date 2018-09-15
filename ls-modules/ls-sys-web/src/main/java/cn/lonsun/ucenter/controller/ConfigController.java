package cn.lonsun.ucenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.util.RegexUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.ldap.internal.entity.ConfigEO;
import cn.lonsun.ldap.internal.service.IConfigService;

/**
 * LDAP配置管理控制器
* @Description: 
* @author xujh 
* @date 2014年9月23日 下午10:06:34
* @version V1.0
 */
@Controller
@RequestMapping("/config")
public class ConfigController extends BaseController {

	@Autowired
	private IConfigService configService;
	
	@RequestMapping("index")
	public String page(){
		return "/system/ucenter/ldapconfig_list";
	}
	
	@RequestMapping("edit")
	public String edit(Long configId,Model m){
		m.addAttribute("configId", configId);
		return "/system/ucenter/ldapconfig_edit";
	}
	
	/**
	 * 获取所有的LDAP配置
	 *
	 * @return
	 */
	@RequestMapping("getPagination")
	@ResponseBody
	public Object getPagination(PageQueryVO query){
		return getObject(configService.getPagination(query));
	}
	
	/**
	 * 获取所有的LDAP配置
	 *
	 * @return
	 */
	@RequestMapping("getConfig")
	@ResponseBody
	public Object getConfig(Long configId){
		ConfigEO config = null;
		if(configId==null){
			config = new ConfigEO();
			Integer maxSortNum = configService.getMaxSortNum();
			config.setSortNum(maxSortNum+2);
		}else{
			config = configService.getEntity(ConfigEO.class, configId);
			config.setPassword(null);
		}
		return getObject(config);
	}
	
	/**
	 * 获取所有的LDAP配置
	 *
	 * @return
	 */
	@RequestMapping("saveConfig")
	@ResponseBody
	public Object saveConfig(ConfigEO config){
		//1.验证url格式以及是否已存在
		if(StringUtils.isEmpty(config.getUrl())){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入 LDAP 地址");
		}
//		if(!RegexUtil.isLdapUrl(config.getUrl())){
//			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入正确的 LDAP 地址");
//		}
		if(configService.isUrlExistedExceptSelf(config)){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "LDAP 地址已存在");
		}
		configService.saveEntity(config);
		return getObject();
	}
	
	/**
	 * 获取所有的LDAP配置
	 *
	 * @return
	 */
	@RequestMapping("updateConfig")
	@ResponseBody
	public Object updateConfig(ConfigEO config){
		//1.验证url格式以及是否已存在
		if(StringUtils.isEmpty(config.getUrl())){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入 LDAP 地址");
		}
		if(!RegexUtil.isLdapUrl(config.getUrl())){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入正确的 LDAP 地址");
		}
		if(configService.isUrlExistedExceptSelf(config)){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "LDAP 地址已存在");
		}
		String pwd = config.getPassword();
		if(StringUtils.isEmpty(pwd)){
			config.setPassword(configService.getPassword(config.getConfigId()));
		}
		configService.updateEntity(config);
		return getObject();
	}
	
	/**
	 * 获取所有的LDAP配置
	 *
	 * @param configId
	 * @return
	 */
	@RequestMapping("deleteConfig")
	@ResponseBody
	public Object deleteConfig(Long configId){
		configService.delete(ConfigEO.class, configId);
		return getObject();
	}
}
