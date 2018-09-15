package cn.lonsun.util;

import java.util.ArrayList;
import java.util.List;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.system.globalconfig.internal.entity.GlobConfigItemEO;
import cn.lonsun.system.globalconfig.internal.service.IGlobConfigCateService;
import cn.lonsun.system.globalconfig.internal.service.IGlobConfigItemService;
import cn.lonsun.system.globalconfig.vo.ConfigVO;

/**
 * 
 * @ClassName: GlobalConfigUtil
 * @Description: 全局配置工具类
 * @author Hewbing
 * @date 2015年9月2日 下午4:12:32
 *
 */
public class GlobalConfigUtil {
	
	private static IGlobConfigCateService globConfigCateService=SpringContextHolder.getBean("globConfigCateService");
	private static IGlobConfigItemService globConfigItemService=SpringContextHolder.getBean("globConfigItemService");
	
	/**
	 * 
	 * @Description 获取允许登入IP的工具类
	 * @author Hewbing
	 * @date 2015年9月1日 下午2:38:08
	 * @return
	 */
	public static List<String> getAllowIps(){
		List<GlobConfigItemEO> allowIps = globConfigItemService.getListByCateKey("allow_ip");
		List<String> ips=new ArrayList<String>();
		for(GlobConfigItemEO item:allowIps){
			ips.add(item.getValue());
		}
		return ips;
	} 
	
	/**
	 * 
	 * @Description 获取禁止登入IP的工具类
	 * @author Hewbing
	 * @date 2015年9月1日 下午2:40:33
	 * @return
	 */
	public static List<String> getBanIps(){
		List<GlobConfigItemEO> banIps = globConfigItemService.getListByCateKey("ban_ip");
		List<String> ips=new ArrayList<String>();
		for(GlobConfigItemEO item:banIps){
			ips.add(item.getValue());
		}
		return ips;
	} 
	
	public static enum UploadType{
		PIC,//图片
		VIDEO,//视频
		AUDIO,//音频
		FILE,//文件
		SIZE,//上传大小
	}
	
	 /**
	  * 
	  * @Description 根据需要获取上传配置
	  * @author Hewbing
	  * @date 2015年9月6日 下午2:07:27
	  * @param type
	  * @return
	  */
	public static List<ConfigVO> getUploadConfig(UploadType... type){
		List<ConfigVO> list=new ArrayList<ConfigVO>();
		for(UploadType str:type){
			ConfigVO _vo=new ConfigVO();
			String _type="";
			if("PIC".equals(str.toString())) _type="pic_suffix";
			else if("VIDEO".equals(str.toString())) _type="video_suffix";
			else if("AUDIO".equals(str.toString())) _type="audio_suffix";
			else if("FILE".equals(str.toString())) _type="file_suffix";
			else if("SIZE".equals(str.toString())) _type="file_max_size";
			else throw new BaseRunTimeException(TipsMode.Message.toString(),"参数出错，请正确传参！");
			GlobConfigItemEO _itemEO = globConfigItemService.getEOByKey(_type);
			_vo.setKey(str.toString());
			_vo.setValue(_itemEO.getValue());
			list.add(_vo);
		}
		return list;
	}
	
	
}
