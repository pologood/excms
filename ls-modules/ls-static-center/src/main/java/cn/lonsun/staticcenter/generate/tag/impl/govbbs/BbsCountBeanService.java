package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.govbbs.internal.dao.IBbsPostDao;
import cn.lonsun.govbbs.internal.vo.StaticWebVO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.util.DateUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by lonsun on 2017-1-6.
 */

@Component
public class BbsCountBeanService extends AbstractBeanService {
    @Autowired
    private IBbsPostDao bbsPostDao;
    @Autowired
    private IOrganService organService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Integer num = paramObj.getInteger("num");
        Long siteId=paramObj.getLong("siteId");
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID,siteId);
        if(siteConfigEO == null && StringUtils.isEmpty(siteConfigEO.getUnitIds())){
            return null;
        }
        IndicatorEO pindicatorEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
        String uri="";
        if(null!=pindicatorEO&& !AppUtil.isEmpty(pindicatorEO.getUri())){
            uri=pindicatorEO.getUri();
        }
        Map<String,Object> map =new HashMap<String, Object>();
        map.put("uri",uri);
        OrganEO unit = organService.getEntity(OrganEO.class, Long.parseLong(siteConfigEO.getUnitIds()));
        List<StaticWebVO> list = new ArrayList<StaticWebVO>();
        if(!AppUtil.isEmpty(num)){
            list=getNumList(num,unit);

        }
          else {

            list =getList(unit);
        }
        map.put("list",list);
        return map ;
    }


    public  List<StaticWebVO> getNumList(Integer num, OrganEO unit){
        Calendar calendar = Calendar.getInstance();
        Date today = DateUtil.getToday();
//        calendar.setTime(today);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE,0);
//        calendar.set(Calendar.SECOND,0);
//        calendar.set(Calendar.MILLISECOND,0);

        Date  startDate=new Date(today.getTime()+24*3600*1000);

        Date endDate=new Date(today.getTime()-24*3600*1000*28L);


        calendar.setTime(today);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 28);
        today = calendar.getTime();

        List<Object> values = new ArrayList<Object>();
        StringBuffer sql=new StringBuffer();
        sql.append("select * from ( select  r.ORGAN_ID as organId,min(r.NAME_) as name,  count (CASE WHEN c.IS_ACCEPT=1 THEN 1 ELSE null END) as replyCount, count(CASE WHEN c.IS_ACCEPT=0 THEN 1 ELSE null END ) as unReplyCount, count(CASE WHEN c.HANDLE_TIMES> c.OVERDUE_TIMES THEN 1 ELSE null END ) as outCount,count(CASE WHEN c.HANDLE_TIMES> YELLOW_TIMES and c.HANDLE_TIMES< c.RED_TIMES  THEN 1 ELSE null END ) as yellowCount,count(CASE WHEN c.HANDLE_TIMES> c.RED_TIMES THEN 1 ELSE null END ) as readCount, count(CASE WHEN c.POST_ID is not null THEN 1 ELSE null END) as count,  TRUNC (count (CASE WHEN c.IS_ACCEPT=1 THEN 1 ELSE null END)/count(CASE WHEN b.POST_ID is not null THEN 1 ELSE 0 END),2)*100  as rate   from   rbac_organ r left join    (select * from CMS_BBS_POST m  where m.CREATE_DATE>? and m.CREATE_DATE<?  and m.IS_PUBLISH=1    and  (m.IS_ACCEPT is null or m.IS_ACCEPT = 1 or (m.IS_ACCEPT = 0 and m.CREATE_DATE >= ?))) c  on r.ORGAN_ID =c.ACCEPT_UNIT_ID  left join CMS_BBS_REPLY b on c.REPLY_ID =b.REPLY_ID where        r.RECORD_STATUS='"+ AMockEntity.RecordStatus.Normal.toString()+"' and r.TYPE_ = '"+ OrganEO.Type.Organ.toString()+"' and r.DN_ like '%"+unit.getDn()+"' and r.DN_ !=?  ");
        sql.append( "group by r.ORGAN_ID)  t");
        sql.append(" order by t.count desc,t.rate desc");

        values.add(endDate);
        values.add(startDate);
        values.add(today);
        values.add(unit.getDn());
        List<StaticWebVO> list =(List<StaticWebVO>) bbsPostDao.getBeansBySql(sql.toString(),values.toArray(), StaticWebVO.class);
        if(null!=list&&list.size()>0){
            list=list.subList(0,num);
        }
        return list;
    }


    public  List<StaticWebVO> getList(OrganEO unit){
        Context context = ContextHolder.getContext();
        String  begintime =context.getParamMap().get("beginDate");

        String endtime = context.getParamMap().get("endDate");

        List<Object> values = new ArrayList<Object>();
        StringBuffer sql=new StringBuffer();
        Date today = DateUtil. getToday();
        Calendar instance = Calendar.getInstance();
        instance.setTime(today);
        instance.set(Calendar.DATE, instance.get(Calendar.DATE) - 28);
        today = instance.getTime();

        sql.append("select * from ( select  r.ORGAN_ID as organId,min(r.NAME_) as name,  count (CASE WHEN c.IS_ACCEPT=1 THEN 1 ELSE null END) as replyCount, count(CASE WHEN c.IS_ACCEPT=0 THEN 1 ELSE null END ) as unReplyCount, count(CASE WHEN c.HANDLE_TIMES> c.OVERDUE_TIMES THEN 1 ELSE null END ) as outCount,count(CASE WHEN c.HANDLE_TIMES> YELLOW_TIMES and c.HANDLE_TIMES< c.RED_TIMES  THEN 1 ELSE null END ) as yellowCount,count(CASE WHEN c.HANDLE_TIMES> c.RED_TIMES THEN 1 ELSE null END ) as readCount, count(CASE WHEN c.POST_ID is not null THEN 1 ELSE null END) as count,  TRUNC (count (CASE WHEN c.IS_ACCEPT=1 THEN 1 ELSE null END)/count(CASE WHEN b.POST_ID is not null THEN 1 ELSE 0 END),2)*100  as rate   from   rbac_organ r left join    (select * from CMS_BBS_POST m  where 1=1 and   m.IS_PUBLISH=1   and  (m.IS_ACCEPT is null or m.IS_ACCEPT = 1 or (m.IS_ACCEPT = 0 and m.CREATE_DATE >= ?))  ");
        values.add(today);
        if(AppUtil.isEmpty(begintime)&& AppUtil.isEmpty(endtime)){
            Date startDate = DateUtil.getToday();
            Date endDate= DateUtil.getToday();
         sql.append("  and m.CREATE_DATE>? and m.CREATE_DATE<?");
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(new Date());
//            calendar.set(Calendar.HOUR_OF_DAY, 0);
//            calendar.set(Calendar.MINUTE,0);
//            calendar.set(Calendar.SECOND,0);
//            calendar.set(Calendar.MILLISECOND,0);

            startDate=new Date(startDate.getTime()+24*3600*1000);
            endDate=new Date(endDate.getTime()-24*3600*1000*28L);
            values.add(endDate);

            values.add(startDate);


        }

        else {
           if(!AppUtil.isEmpty(begintime)){
               sql.append("  and m.CREATE_DATE>to_date(?,'yyyy-mm-dd hh24:mi:ss')");
               values.add(begintime);

           }

            if(!AppUtil.isEmpty(endtime)){
                sql.append("  and  m.CREATE_DATE<to_date(?,'yyyy-mm-dd hh24:mi:ss')");
                values.add(endtime);

            }



        }


        sql.append(" ) c on r.ORGAN_ID =c.ACCEPT_UNIT_ID  left join CMS_BBS_REPLY b on c.REPLY_ID =b.REPLY_ID where        r.RECORD_STATUS='"+ AMockEntity.RecordStatus.Normal.toString()+"' and r.TYPE_ = '"+ OrganEO.Type.Organ.toString()+"' and r.DN_ like '%"+unit.getDn()+"' and  r.DN_ !=? ");
        values.add(unit.getDn());
        sql.append( "group by r.ORGAN_ID)  t");
        sql.append(" order by t.count desc,t.rate desc");


        List<StaticWebVO> list =(List<StaticWebVO>) bbsPostDao.getBeansBySql(sql.toString(),values.toArray(), StaticWebVO.class);

        return list;

    }


}
