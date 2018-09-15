package cn.lonsun.log.internal.service.impl;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.BrowserUtils;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.log.internal.dao.ILogDao;
import cn.lonsun.log.internal.entity.LogEO;
import cn.lonsun.log.internal.service.ILogService;

/**
 * Created by yy on 2014/8/12.
 */
@Service("rbacLogService")
public class LogServiceImpl extends MockService<LogEO> implements
        ILogService {
    @Autowired
    private ILogDao rbacLogDao;

    @Override
    public void saveLog(String description, String caseType,String operation) {
        HttpServletRequest request = ContextHolderUtils.getRequest();
        LogEO logEO = new LogEO();
        logEO.setCaseType(caseType);
        logEO.setOperation(operation);
        logEO.setDescription(description);
        logEO.setOperationIp(IpUtil.getIpAddr(request));
        logEO.setCreateUser(ContextHolderUtils.getPersonName());
        logEO.setOrganName(ContextHolderUtils.getOrganName());
        logEO.setUid(ContextHolderUtils.getUid());
        logEO.setUserAgent(request.getHeader("user-agent"));
        logEO.setRequestUri(request.getRequestURI());
        logEO.setMethod(request.getMethod());
        logEO.setBroswer(BrowserUtils.checkBrowse(request));
        rbacLogDao.save(logEO);
    }
    @Override
    public void deleteLog(Long logId){
        rbacLogDao.delete(LogEO.class, logId);
    }
    
    @Override
    public Pagination getPage(Long pageIndex,Integer pageSize, Date startDate, Date endDate, String type, String key) {
    	return (Pagination) rbacLogDao.getPage(pageIndex, pageSize, startDate, endDate, type, key);
    }
	@Override
	public List<LogEO> getAllLogs(Date startDate, Date endDate, String type,
			String key) {
		return rbacLogDao.getAllLogs(startDate, endDate, type, key);
	}
}
