package cn.lonsun.util;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.system.systemlog.internal.service.ICmsLogService;

/**
 * 
 * @ClassName: SysLog
 * @Description: 操作日志
 * @author Hewbing
 * @date 2015年12月31日 上午9:30:36
 *
 */
public class SysLog {

	private static ICmsLogService cmsLogService=SpringContextHolder.getBean("cmsLogService");
	
	/**
	 * 
	 * @Title: log
	 * @Description: TODO
	 * @param desc 操作描述
	 * @param caseType	业务对象类型,例如：UserEO
	 * @param opr 操作类型 如CmsLogEO.Operation.Add.toString() (增加操作)
	 * @return  void   return type
	 * @throws
	 */
	public static void log(String desc,String caseType,String opr){
		cmsLogService.recLog(desc, caseType, opr);
	}
}
