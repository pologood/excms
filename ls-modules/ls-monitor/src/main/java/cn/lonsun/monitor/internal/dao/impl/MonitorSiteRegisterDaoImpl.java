package cn.lonsun.monitor.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.internal.dao.IMonitorSiteRegisterDao;
import cn.lonsun.monitor.internal.entity.MonitorSiteRegisterEO;
import cn.lonsun.monitor.internal.vo.MonitorSiteRegisterVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 日常监测站点注册Dao实现类<br/>
 */

@Repository
public class MonitorSiteRegisterDaoImpl extends BaseDao<MonitorSiteRegisterEO> implements IMonitorSiteRegisterDao {


    /**
     * 查询站点以及其日常监测开通信息
     * @return
     */
    @Override
    public List<MonitorSiteRegisterVO> getSiteRegisterInfos(Long siteId){
        String sql = "select r.indicator_Id as siteId,r.name as siteName," +
                "m.is_registered as isRegistered,m.registered_code as registeredCode,m.registered_time as registeredTime "
                + " from cms_site_mgr r left join monitor_site_register m on r.indicator_Id = m.site_Id where 1=1 ";
        List<Object> values = new ArrayList<Object>();
        if(siteId!=null&&siteId>0){
            sql += " and r.indicator_id = ? ";
            values.add(siteId);
        }

        List<String> queryField = new ArrayList<String>();
        queryField.add("siteId");
        queryField.add("siteName");
        queryField.add("isRegistered");
        queryField.add("registeredCode");
        queryField.add("registeredTime");

        return (List<MonitorSiteRegisterVO>)getBeansBySql(sql,values.toArray(),MonitorSiteRegisterVO.class,queryField.toArray(new String[]{}));
    }


    /**
     * 查询站点以及其日常监测开通信息
     * @return
     */
    @Override
    public Pagination getSiteRegisterInfos(Long pageIndex, Integer pageSize){
        String sql = "select r.indicator_Id as siteId,r.name as siteName," +
                "m.is_registered as isRegistered,m.registered_code as registeredCode,m.registered_time as registeredTime "
                + " from cms_site_mgr r left join monitor_site_register m on r.indicator_Id = m.site_Id where 1=1 ";
        List<String> queryField = new ArrayList<String>();
        queryField.add("siteId");
        queryField.add("siteName");
        queryField.add("isRegistered");
        queryField.add("registeredCode");
        queryField.add("registeredTime");

        return getPaginationBySql(pageIndex,pageSize,sql,new String[]{},MonitorSiteRegisterVO.class,queryField.toArray(new String[]{}));
    }

}
