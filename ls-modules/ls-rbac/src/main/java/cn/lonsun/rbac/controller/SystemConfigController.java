
package cn.lonsun.rbac.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.PropertiesReader;
import cn.lonsun.rbac.utils.PropertiesContainer;
import cn.lonsun.rbac.utils.PropertiesContainer.FileNames;
import cn.lonsun.rbac.utils.PropertiesFilePaths;
import cn.lonsun.rbac.vo.SystemConfigVO;

/**
 * 系统环境设置控制器
* @Description: 
* @author xujh 
* @date 2014年9月23日 下午10:11:40
* @version V1.0
 */
@Controller
@RequestMapping(value="systemConfig",produces = {"application/json;charset=UTF-8"})
public class SystemConfigController extends BaseController{
	//配置文件名称
	String fileName = FileNames.SystemProperties.getValue();
	
	/**
	 * 配置管理页面
	 * @return
	 */
	@RequestMapping("developEditPage")
	public String developEditPage(){
		return "/app/mgr/developer/setting";
	}
	
	/**
	 * 配置管理页面
	 * @return
	 */
	@RequestMapping("configPage")
	public String configPage(){
		return "/app/mgr/setting_global";
	}
	
	/**
	 * 获取所有的配置
	 * @return
	 */
	@RequestMapping("getConfigs")
	@ResponseBody
	public Object getConfigs(HttpServletRequest request){
		PropertiesContainer container = PropertiesContainer.getInstance();
		Map<String,String> properties = container.getProperties(fileName);
		//如果没有取到，那么到配置文件中读取，并保存到容器中
		if(properties==null||properties.size()<=0){
			reload(request);
			properties = container.getProperties(fileName);
		}
		Set<String> keys = properties.keySet();
		//转码
		for(String key:keys){
			String value = properties.get(key);
			String encode = cn.lonsun.core.base.util.StringUtils.getEncoding(value).toUpperCase();
			if(!encode.equals("UTF-8")){
				try {
					value = new String(value.getBytes(encode),"UTF-8");
					properties.put(key,value);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return getObject(properties);
	}
	
	/**
	 * 
	 * @param request
	 */
	public void reload(HttpServletRequest request){
		PropertiesContainer container = PropertiesContainer.getInstance();
		//如果没有取到，那么到配置文件中读取，并保存到容器中
		String realPath = request.getSession(false).getServletContext().getRealPath(PropertiesFilePaths.SYSTEM_ENVIRONMENT_PATH);
		Map<String, String> map = PropertiesReader.getInstance(realPath).getValues();
		container.put(fileName, map);
	}
	
	/**
	 * 更新配置
	 * @param request
	 * @param configs
	 * @return
	 */
	@RequestMapping("update")
	@ResponseBody
	public Object update(HttpServletRequest request,SystemConfigVO config){
		String realPath = request.getSession(false).getServletContext().getRealPath(PropertiesFilePaths.SYSTEM_ENVIRONMENT_PATH);
		PropertiesReader reader = PropertiesReader.getInstance(realPath);
		Map<String,String> configs = new HashMap<String,String>();
		configs.put("appendixSize", config.getAppendixSize());
		String appendixEncryption = "false".equals(config.getAppendixEncryption())?"0":config.getAppendixEncryption();
		configs.put("appendixEncryption", appendixEncryption);
		String emailContentEncryption = "false".equals(config.getEmailContentEncryption())?"0":config.getEmailContentEncryption();
		configs.put("emailContentEncryption", emailContentEncryption);
		String knowledgeEncryption = "false".equals(config.getKnowledgeEncryption())?"0":config.getKnowledgeEncryption();
		configs.put("knowledgeEncryption", knowledgeEncryption);
		configs.put("logRecovery", config.getLogRecovery());
		configs.put("sign", config.getSign());
		configs.put("smsReminder", config.getSmsReminder());
		configs.put("signatures", config.getSignatures());
		configs.put("signaturesSupplier", config.getSignaturesSupplier());
		configs.put("signaturesSupplier", config.getSignaturesSupplier());
		configs.put("maxCapacity", config.getMaxCapacity());
		reader.update(configs);
		//重新加载PropertiesContainer中的配置
		reload(request);
		PropertiesContainer container = PropertiesContainer.getInstance();
		Map<String,String> properties = container.getProperties(fileName);
		return getObject(properties);
	}

}
