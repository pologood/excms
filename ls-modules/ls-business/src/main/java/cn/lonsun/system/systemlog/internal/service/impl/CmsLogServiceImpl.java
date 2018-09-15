package cn.lonsun.system.systemlog.internal.service.impl;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.BrowserUtils;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.systemlog.internal.dao.ICmsLogDao;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.system.systemlog.internal.service.ICmsLogService;
import cn.lonsun.util.LoginPersonUtil;

@Service("cmsLogService")
public class CmsLogServiceImpl extends MockService<CmsLogEO> implements
        ICmsLogService {
    @Autowired
    private ICmsLogDao cmsLogDao;

    @Override
    public void recLog(String description, String caseType,String operation) {
    	HttpServletRequest request = null ;
    	try{
        	request = LoginPersonUtil.getRequest();
    	}catch(Exception e){
    		e.printStackTrace();
    		return;
    	}
        CmsLogEO logEO = new CmsLogEO();
        logEO.setCaseType(caseType);
        logEO.setOperation(operation);
        logEO.setDescription(description);
        logEO.setOperationIp(IpUtil.getIpAddr(request));
        logEO.setCreateUser(LoginPersonUtil.getPersonName());
        logEO.setOrganName(LoginPersonUtil.getUnitName());
        logEO.setUid(LoginPersonUtil.getUserName()==null?"上传组件":LoginPersonUtil.getUserName());
        logEO.setUserAgent(request.getHeader("user-agent"));
        logEO.setRequestUri(request.getRequestURI());
        logEO.setMethod(request.getMethod());
        logEO.setBroswer(BrowserUtils.checkBrowse(request));
        logEO.setSiteId(LoginPersonUtil.getSiteId());
        cmsLogDao.save(logEO);
    }
    @Override
    public void deleteLog(Long logId){
    	cmsLogDao.delete(CmsLogEO.class, logId);
    }
    
    @Override
    public Pagination getPage(Long pageIndex,Integer pageSize, Date startDate, Date endDate, String type, String key,Long siteId) {
    	return (Pagination) cmsLogDao.getPage(pageIndex, pageSize, startDate, endDate, type, key,siteId);
    }
	@Override
	public List<CmsLogEO> getAllLogs(Date startDate, Date endDate, String type,
			String key,Long siteId) {
		return cmsLogDao.getAllLogs(startDate, endDate, type, key,siteId);
	}
}
