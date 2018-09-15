package cn.lonsun.wechatmgr.internal.dao.impl;

import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
* Created by lonsun on 2016-11-7.
*/
@Repository("weChatMsgMySqlDao")
public class WeChatMsgMySqlDaoImpl extends WeChatMsgDaoImpl {

    @Override
    public List<Object> getWeekCount(List<String> days,Integer isRep,Long siteId) {
        List<Object> param = new ArrayList<Object>();
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        if(!(days != null && days.size() > 0)){
            return null;
        }
        Date start =new Date();
        Date end =start;
        String sql="select";
        for(int i = 0;i< days.size();i++){
            try {
                start = dateFormat.parse(days.get(i));
                calendar.setTime(start);
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            sql +=" sum(case when t.create_date >= ? and t.create_date< ?";
            param.add(start);
            param.add(calendar.getTime());

            if(isRep != null && (isRep == 0 || isRep == 1)){
                sql +=" and t.is_rep = "+isRep+"";
            }
            sql +=" then 1 else 0 end)";
            if(i != days.size() -1){
                sql +=",";
            }
        }
        sql+= " from cms_wechat_msg t where t.record_status = 'Normal'";
        if(siteId != null){
            sql +=" and t.site_id = "+siteId+"";
        }
        return (List<Object>)getObjectsBySql(sql,param.toArray());
    }



}
