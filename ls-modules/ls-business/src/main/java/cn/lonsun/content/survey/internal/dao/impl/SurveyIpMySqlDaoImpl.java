package cn.lonsun.content.survey.internal.dao.impl;

import cn.lonsun.content.survey.internal.entity.SurveyIpEO;
import cn.lonsun.core.base.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhushouyong on 2018-7-10.
 */
public class SurveyIpMySqlDaoImpl extends SurveyIpDaoImpl {


    @Override
    public List<SurveyIpEO> getSurveyIp(Long themeId, String ip, String time) {
        List<Object> values = new ArrayList<Object>();
        String hql = "from SurveyIpEO where themeId = ? and ipAddr = ?";
        values.add(themeId);
        values.add(ip);
        if(!StringUtils.isEmpty(time)){
            hql +=" and date_format(voteTime,'%Y-%m-%d') = ?";
            values.add(time);
        }
        return getEntitiesByHql(hql, values.toArray());
    }

}
