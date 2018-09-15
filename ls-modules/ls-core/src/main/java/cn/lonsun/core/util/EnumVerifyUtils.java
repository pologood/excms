package cn.lonsun.core.util;

import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;

public abstract class EnumVerifyUtils {
	
	private EnumVerifyUtils(){
		throw new Error();
	}
	
	/**
	 * 校验status是否是ABaseEntity.Status中的一个
	 * @param status
	 * @return
	 */
	public static boolean isRecordStatusIllegal(String status){
		boolean isIllegal = false;
		RecordStatus[] arr = RecordStatus.values();
		int length = arr.length;
		for(int i=0;i<length;i++){
			if(status.equals(arr[i].toString())){
				isIllegal = true;
				break;
			}
		}
		return isIllegal;
	}
}
