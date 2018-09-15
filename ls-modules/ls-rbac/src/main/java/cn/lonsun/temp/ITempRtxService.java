package cn.lonsun.temp;

import java.util.Map;

public interface ITempRtxService {
	/**
	 * 第一级传入null
	 *
	 * @param organId
	 */
	public void update(Long organId,Map<String,String> mapDomain);
	public void addRoot(Long organId,String organName,int sortNum);

}
