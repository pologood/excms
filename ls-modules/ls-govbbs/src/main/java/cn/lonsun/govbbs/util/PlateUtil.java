package cn.lonsun.govbbs.util;


import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;

public class PlateUtil {

	public static String topPlate = "BBS";

	public static String defaultOrder = "createDate desc";

	/**
	 * 获取主题排序
	 * @param plateId
	 * @param asKey 表 別名
	 * @return
	 */
	public static String getPlateOrderKey(Long plateId,String asKey){
		String defaultOd = asKey+"."+defaultOrder;
		if(plateId != null) {
			BbsPlateEO plate = CacheHandler.getEntity(BbsPlateEO.class, plateId);
			if (plateId != null && plate != null) {
				if (plate != null && plate.getSortField() != null) {
					return asKey + "." + getField(plate.getSortField()) + " " + getSortMode(plate.getSortMode());
				}
			}
		}
		return defaultOd;
	}

	/**
	 * sortMode  0 desc 1 asc
	 * @param sortMode
	 * @return
	 */
	private static String getSortMode(Integer sortMode) {
		return sortMode == null?"desc":(1 == sortMode ?"asc":"desc");
	}

	/**
	 *  * sortField   0 创建时间   1 审核时间 2 浏览数量 3 回复数量
	 * @param sortField
	 * @return
	 */
	private static String getField(Integer sortField) {
		String field = "createDate";
		switch (sortField){
			case 0:
				field = "createDate";
				break;
			case 1:
				field = "auditTime";
				break;
			case 2:
				field = "viewCount";
				break;
			case 3:
				field = "replyCount";
				break;
		}
		return field;
	}
}
