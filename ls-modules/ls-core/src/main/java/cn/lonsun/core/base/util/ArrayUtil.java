package cn.lonsun.core.base.util;

import java.util.List;

/**
 * 数组工具
 *  
 * @author xujh 
 * @date 2014年11月1日 下午2:27:37
 * @version V1.0
 */
public class ArrayUtil {
	
	/**
	 * 判断数组中是否存在equals的对象
	 *
	 * @param array
	 * @param element
	 * @return
	 */
	public static boolean contains(Object[] array,Object element){
		boolean isContains = false;
		if(array!=null&&array.length>0){
			for(Object e:array){
				if(e!=null&&e.equals(element)){
					isContains = true;
					break;
				}
			}
		}
		return isContains;
	}
	
	/**
	 * 验证数组是否为空
	 *
	 * @param arr
	 * @return
	 */
	public static boolean isEmpty(Object[] arr){
		return arr==null||arr.length<=0;
	}

	/**
	 * 将list中所有的值按顺序存储到数组arr中
	 * @param arr
	 * @param list
	 */
	public static void toArray(Object[] arr, List<?> list) {
		if (list == null || list.size() <= 0 || arr == null) {
			return;
		}
		int size = list.size();
		for (int i = 0; i < size; i++) {
			arr[i] = list.get(i);
		}
	}
}
