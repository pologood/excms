package cn.lonsun.rbac.internal.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.dao.IPlatformDao;
import cn.lonsun.rbac.internal.entity.PlatformEO;
import cn.lonsun.rbac.internal.service.IPlatformService;

/**
 * 平台管理服务接口实现
 *
 * @author xujh
 * @version 1.0
 * 2015年4月24日
 *
 */
@Service
public class PlatformServiceImpl extends BaseService<PlatformEO> implements
		IPlatformService {
	@Autowired
	private IPlatformDao platformDao;
	
	private static PlatformEO currentPlatform = null;
	
	@Override
	public PlatformEO getCurrentPlatform(){
		if(PlatformServiceImpl.currentPlatform==null){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("isExternal", Boolean.FALSE);
			List<PlatformEO> platforms = getEntities(PlatformEO.class, map);
			if(platforms==null||platforms.size()<=0){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "初始化数据错误，未初始化本平台编码.");
			}
			if(platforms.size()>1){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "初始化数据错误，本平台设置了多个编码.");
			}
			PlatformServiceImpl.currentPlatform = platforms.get(0);
		}
		return PlatformServiceImpl.currentPlatform;
	}

	@Override
	public void save(PlatformEO platform) {
		//isExternal，只有一条记录可以为false-本平台交换平台只能有一个
		if(!platform.getIsExternal()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("isExternal", Boolean.FALSE);
			List<PlatformEO> platforms = getEntities(PlatformEO.class, map);
			if(platforms!=null&&platforms.size()>0){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "本平台的数据交换中心配置已存在");
			}
		}
		//验证code是否已存在
		if(isCodeExisted(platform.getCode(),null)){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "编码已存在");
		}
		saveEntity(platform);
	}

	@Override
	public void update(PlatformEO platform) {
		//验证code是否已存在
		if(isCodeExisted(platform.getCode(),platform.getId())){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "编码已存在");
		}
		updateEntity(platform);
	}

	@Override
	public boolean isCodeExisted(String code,Long exceptId) {
		return platformDao.isCodeExisted(code, exceptId);
	}

	@Override
	public Pagination getPagination(PageQueryVO query) {
		return platformDao.getPagination(query);
	}
}
