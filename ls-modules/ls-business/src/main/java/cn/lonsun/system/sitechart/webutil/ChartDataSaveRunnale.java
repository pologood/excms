package cn.lonsun.system.sitechart.webutil;

import org.hibernate.SessionFactory;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.system.sitechart.internal.entity.SiteChartMainEO;
import cn.lonsun.system.sitechart.service.ISiteChartMainService;
import cn.lonsun.util.ConcurrentUtil;

public class ChartDataSaveRunnale implements Runnable {

	private static ISiteChartMainService siteChartMainService=SpringContextHolder.getBean("siteChartMainService");
	
	
	private SiteChartMainEO mainEO;
	
	
	@Override
	public void run() {

        SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
        boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
        try{
        siteChartMainService.saveSiteChart(mainEO);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
			ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
		}
        
	}

	public SiteChartMainEO getMainEO() {
		return mainEO;
	}

	public void setMainEO(SiteChartMainEO mainEO) {
		this.mainEO = mainEO;
	}


}
