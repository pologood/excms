package cn.lonsun.webservice.indicator.impl;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.indicator.IIndicatorService;
import cn.lonsun.webservice.vo.indicator.IndicatorVO;
import org.springframework.stereotype.Service;

@Service("indirectIndicatorService")
public class IndicatorServiceImpl implements IIndicatorService {
	
	/**
	 * WebService编码
	 * 
	 * @author xujh
	 * @date 2014年11月5日 下午4:19:39
	 * @version V1.0
	 */
	private enum Codes {
		Indicator_getAppIndicators,
		Indicator_getAppIndicatorsByType,
		Indicator_getDesktopIndicators,
		Indicator_getAllApps;
	}

	@Override
	public IndicatorVO[] getAppIndicators(Long userId, Long organId,
			Long parentId) {
		String code = Codes.Indicator_getAppIndicators.toString();
		Object[] params = new Object[] { userId,organId,parentId };
		return (IndicatorVO[]) WebServiceCaller.getSimpleObject(code, params,
				IndicatorVO[].class);
	}

	@Override
	public IndicatorVO[] getAppIndicatorsByType(Long userId, Long organId,
			Long parentId, String type) {
		String code = Codes.Indicator_getAppIndicatorsByType.toString();
		Object[] params = new Object[] { userId,organId,parentId ,type};
		return (IndicatorVO[]) WebServiceCaller.getSimpleObject(code, params,
				IndicatorVO[].class);
	}

	@Override
	public IndicatorVO[] getDesktopIndicators(Long userId, Long organId,
			String codes) {
		String code = Codes.Indicator_getDesktopIndicators.toString();
		Object[] params = new Object[] { userId,organId,codes };
		return (IndicatorVO[]) WebServiceCaller.getSimpleObject(code, params,
				IndicatorVO[].class);
	}

	@Override
	public IndicatorVO[] getAllApps() {
		String code = Codes.Indicator_getAllApps.toString();
		return (IndicatorVO[]) WebServiceCaller.getSimpleObject(code, new Object[]{},
				IndicatorVO[].class);
	}

}
