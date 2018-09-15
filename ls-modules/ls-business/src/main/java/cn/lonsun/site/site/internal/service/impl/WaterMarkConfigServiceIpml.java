package cn.lonsun.site.site.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.site.site.internal.dao.IWaterMarkConfigDao;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;
import cn.lonsun.site.site.internal.service.IWaterMarkConfigService;
import cn.lonsun.util.FileUploadUtil;

@Service("waterMarkConfigService")
public class WaterMarkConfigServiceIpml extends MockService<WaterMarkConfigEO> implements
		IWaterMarkConfigService {

	@Autowired
	IWaterMarkConfigDao waterMarkConfigDao;
	
	@Override
	public WaterMarkConfigEO getConfigBySiteId(Long siteId) {
		return waterMarkConfigDao.getConfigBySiteId(siteId);
	}

	@Override
	public void saveConfigEO(WaterMarkConfigEO eo) {
		WaterMarkConfigEO configEO=new WaterMarkConfigEO();
		if(eo.getId()==null){
			if(eo.getType()==1){//图片
				configEO.setHeight(eo.getHeight());
				configEO.setWidth(eo.getWidth());
				configEO.setPicPath(eo.getPicPath());
			}else{//文字
				configEO.setFontColor(eo.getFontColor());
				configEO.setFontFamily(eo.getFontFamily());
				configEO.setFontSize(eo.getFontSize());
				configEO.setIsBold(eo.getIsBold());
				configEO.setWordContent(eo.getWordContent());
			}
			configEO.setType(eo.getType());
			configEO.setSiteId(eo.getSiteId());
			configEO.setEnableStatus(eo.getEnableStatus());
			configEO.setPosition(eo.getPosition());
			configEO.setRotate(eo.getRotate());
			configEO.setTransparency(eo.getTransparency());
			saveEntity(configEO);
		}else{
			configEO=getEntity(WaterMarkConfigEO.class,eo.getId());
			if(eo.getType()==1){//图片

				configEO.setHeight(eo.getHeight());
				configEO.setWidth(eo.getWidth());
				configEO.setPicPath(eo.getPicPath());
				configEO.setPicName(eo.getPicName());

				configEO.setFontColor(null);
				configEO.setFontFamily(null);
				configEO.setFontSize(null);
				configEO.setIsBold(0);
				configEO.setWordContent(null);
			}else{//文字
				configEO.setFontColor(eo.getFontColor());
				configEO.setFontFamily(eo.getFontFamily());
				configEO.setFontSize(eo.getFontSize());
				configEO.setIsBold(eo.getIsBold());
				configEO.setWordContent(eo.getWordContent());

				configEO.setHeight(null);
				configEO.setWidth(null);
				configEO.setPicPath(null);
				configEO.setPicName(null);

			}
			configEO.setType(eo.getType());
			configEO.setSiteId(eo.getSiteId());
			configEO.setEnableStatus(eo.getEnableStatus());
			configEO.setPosition(eo.getPosition());
			configEO.setRotate(eo.getRotate());
			configEO.setTransparency(eo.getTransparency());
			updateEntity(configEO);
		}
			FileUploadUtil.saveFileCenterEO(eo.getPicPath());
	}

}
