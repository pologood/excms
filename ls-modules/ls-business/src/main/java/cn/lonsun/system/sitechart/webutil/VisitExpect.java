package cn.lonsun.system.sitechart.webutil;

public class VisitExpect {

	/**
	 * 
	 * @Title: getExpection
	 * @Description: 站点统计获取访问期望值方法
	 * @param day30 30天统计次数（每小时统计一次）
	 * @param day30C 30天总访问量
	 * @param day7 7天统计次数（每小时统计一次）
	 * @param day7C 7天总访问量
	 * @return   Parameter
	 * @return  int   return type
	 * @throws
	 */
	public static int getExpection(Long day30,Long day30C,Long day7,Long day7C){
		if(day30C==null||day30C==0) day30C=1L;
		if(day7C==null||day7C==0) day7C=1L;
		int d30 = Math.round((day30/day30C)*24*day7/day30C);
		int d7 = Math.round((day7/day7C)*24*(day30-day7)/day30C);
		return d30+d7;
	}
}
