package cn.lonsun.msg.submit.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.msg.submit.dao.IMsgSubmitDao;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitEO;
import cn.lonsun.msg.submit.entity.CmsMsgToColumnEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.statistics.SubmitListVO;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Repository
public class MsgSubmitDao extends BaseDao<CmsMsgSubmitEO> implements IMsgSubmitDao {

    @Override
    public List<CmsMsgSubmitEO> getEOs() {
        return this.getEntitiesByHql("from CmsMsgSubmitEO", new Object[]{});
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        StringBuffer hql = new StringBuffer(" from CmsMsgSubmitEO where 1=1");

        if(null != dto.getClassifyId()) {
            hql.append(" and classifyId = " + dto.getClassifyId());
        }

        if(null != dto.getStatus()) {
            hql.append(" and status = " + dto.getStatus());
        }

        if(!StringUtils.isEmpty(dto.getTempName())) {
            hql.append(" and createUnitName like '%" + SqlUtil.prepareParam4Query(dto.getTempName()) + "%' escape '\\'");
        }

        if(!StringUtils.isEmpty(dto.getStartDate())) {
            hql.append(" and publishDate >= to_date('" + dto.getStartDate() + "','yyyy-mm-dd hh24:mi:ss')");
        }

        if(!StringUtils.isEmpty(dto.getEndDate())) {
            hql.append(" and publishDate <= to_date('" + dto.getEndDate() + "','yyyy-mm-dd hh24:mi:ss')");
        }

        if(!RoleAuthUtil.isCurUserColumnAdmin() && !LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
            hql.append(" and createUnitId = " + LoginPersonUtil.getUnitId());
        }

        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), dto), new Object[]{});
    }

    @Override
    public List<CmsMsgSubmitEO> getEOs(ParamDto dto) {
        Map<String,Object> pmap = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer(" from CmsMsgSubmitEO where 1=1");

        if(!AppUtil.isEmpty(dto.getKeyValue()) && !AppUtil.isEmpty(dto.getKeys())) {
            hql.append(" and (");
            String[] str = dto.getKeys().split(",");
            for(int i = 0 ; i < str.length ; i++) {
                if(i == 0)
                    hql.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(dto.getKeyValue()) + "%' escape '\\'");
                else {
                    hql.append(" or ");
                    hql.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(dto.getKeyValue()) + "%' escape '\\'");
                }
            }

            hql.append(" ) ");
        }

        if(null != dto.getClassifyId()) {
            hql.append(" and classifyId = :classifyId");
            pmap.put("classifyId",dto.getClassifyId());
        }

        if(null != dto.getStatus()) {
            hql.append(" and status = :status");
            pmap.put("status",dto.getStatus());
        }
/*
        if(!StringUtils.isEmpty(dto.getStartDate())) {
            hql.append(" and publishDate >= :startDate");
            pmap.put("startDate",dto.getStartDate());
        }

        if(!StringUtils.isEmpty(dto.getEndDate())) {
            hql.append(" and publishDate >= :endDate");
            pmap.put("endDate",dto.getEndDate());
        }*/

        if(!StringUtils.isEmpty(dto.getStartDate())) {
            hql.append(" and publishDate >= to_date(:startDate,'yyyy-mm-dd hh24:mi:ss')");
            pmap.put("startDate",dto.getStartDate());
        }

        if(!StringUtils.isEmpty(dto.getEndDate())) {
            hql.append(" and publishDate <= to_date(:endDate,'yyyy-mm-dd hh24:mi:ss')");
            pmap.put("endDate",dto.getEndDate());
        }

        if(!RoleAuthUtil.isCurUserColumnAdmin() && !LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
            hql.append(" and createUnitId = :createUnitId");
            pmap.put("endDate",LoginPersonUtil.getUnitId());
        }

        return this.getEntitiesByHql(hql.toString(),pmap);
    }

    @Override
    public Long getCountByClassifyId(Long classifyId) {
        return this.getCount("from CmsMsgSubmitEO where classifyId =?",new Object[] {classifyId});
    }

    @Override
    public Long getCountChart(ContentChartQueryVO queryVO) {
        StringBuffer sql=new StringBuffer("select count(*) count from CMS_MSG_SUBMIT t where 1=1 ");
        if(null!=queryVO.getSiteId()){
            sql.append(" and t.site_id=" + queryVO.getSiteId());
        }
        if(!StringUtils.isEmpty(queryVO.getStartStr())&&!StringUtils.isEmpty(queryVO.getEndStr())){
            sql.append(" and publish_date >= to_date('" + queryVO.getStartStr() + "','yyyy-mm-dd hh24:mi:ss')")
            .append(" and publish_date <= to_date('" + queryVO.getEndStr() + "','yyyy-mm-dd hh24:mi:ss')");
        }
        List<String> fields = new ArrayList<String>();
        fields.add("count");
        String[] arr = new String[fields.size()];
        List<SubmitListVO>list=(List<SubmitListVO> )getBeansBySql(sql.toString(), new Object[]{}, SubmitListVO.class, fields.toArray(arr));
        if(list!=null&&list.size()>0){
           return  Long.parseLong(String.valueOf(list.get(0).getCount()));
        }
        return null;
    }

    @Override
    public List<ContentChartVO> getContentChart(ContentChartQueryVO queryVO) {
        StringBuffer hql=new StringBuffer("select t.createUnitId as organId ,o.name as organName,count(t.id) as count from CmsMsgSubmitEO t,OrganEO o" )
                .append(" where t.createUnitId=o.organId ");
        Map<String, Object> map = new HashMap<String, Object>();
        if(null!=queryVO.getSiteId()){
            hql.append(" and t.siteId=:siteId");
            map.put("siteId", queryVO.getSiteId());
        }
        if(!AppUtil.isEmpty(queryVO.getStartDate())&&!AppUtil.isEmpty(queryVO.getEndDate())){
            hql.append(" and t.publishDate>=:startDate and t.publishDate<=:endDate");
            map.put("startDate", queryVO.getStartDate());
            map.put("endDate", queryVO.getEndDate());
        }
        hql.append(" group by t.createUnitId,o.name");
        hql.append(" order by count(t.id) desc");
        return (List<ContentChartVO>) getBeansByHql(hql.toString(), map, ContentChartVO.class, queryVO.getNum());
    }

    @Override
    public List<ContentChartVO> getContentChart1(ContentChartQueryVO queryVO) {
        StringBuffer hql=new StringBuffer("select t.createOrganId as organId ,o.name as organName,count(t.id) as count from CmsMsgSubmitEO t,OrganEO o" )
                .append(" where t.createOrganId=o.organId ");
        Map<String, Object> map = new HashMap<String, Object>();
        if(null!=queryVO.getSiteId()){
            hql.append(" and t.siteId=:siteId");
            map.put("siteId", queryVO.getSiteId());
        }
        if(!AppUtil.isEmpty(queryVO.getStartDate())&&!AppUtil.isEmpty(queryVO.getEndDate())){
            hql.append(" and t.publishDate>=:startDate and t.publishDate<=:endDate");
            map.put("startDate", queryVO.getStartDate());
            map.put("endDate", queryVO.getEndDate());
        }
        hql.append(" group by t.createOrganId,o.name");
        hql.append(" order by count(t.id) desc");
        return (List<ContentChartVO>) getBeansByHql(hql.toString(), map, ContentChartVO.class, queryVO.getNum());
    }

    @Override
    public List<ContentChartVO> getContentChart2(ContentChartQueryVO queryVO) {
        StringBuffer hql=new StringBuffer("select t.createUserId as organId,p.name as OrganName,count(t.id) as count from CmsMsgSubmitEO t " )
                .append(",PersonEO p where t.createUserId=p.userId ");
        Map<String, Object> map = new HashMap<String, Object>();
        if(null!=queryVO.getSiteId()){
            hql.append(" and t.siteId=:siteId");
            map.put("siteId", queryVO.getSiteId());
        }
        if(!AppUtil.isEmpty(queryVO.getStartDate())&&!AppUtil.isEmpty(queryVO.getEndDate())){
            hql.append(" and t.publishDate>=:startDate and t.publishDate<=:endDate");
            map.put("startDate", queryVO.getStartDate());
            map.put("endDate", queryVO.getEndDate());
        }
        hql.append(" group by t.createUserId,p.name");
        hql.append(" order by count(t.id) desc");
        return (List<ContentChartVO>) getBeansByHql(hql.toString(), map, ContentChartVO.class, queryVO.getNum());
    }

    @Override
    public Pagination getDetailPage(Long pageIndex,Integer pageSize,Long uId, String uName) {
        StringBuffer hql=new StringBuffer(" select m.id as msgId,m.name as msgName,t.columnId as columnId,t.createDate as createDate,t.createOrganId as createOrganId,t.createUserId as createUserId " )
                                  .append(" from CmsMsgSubmitEO m, CmsMsgToColumnEO t where m.id=t.msgId and m.status=2 and m.useCount>0 and m.siteId="+LoginPersonUtil.getSiteId());
        if(uId!=null&& !StringUtils.isEmpty(uName)){
            hql.append(" and m.").append(uName).append("=").append(uId);
        }
        hql.append(" order by m.id desc,t.createDate desc");
        Pagination page=getPagination(pageIndex,pageSize,hql.toString(),new Object[]{},CmsMsgToColumnEO.class);
        return page;
    }

    @Override
    public List<ContentChartVO> getEmpContentChart(ContentChartQueryVO queryVO) {
        StringBuffer hql=new StringBuffer("select t.createUnitId as organId ,o.name as organName,count(t.id) as count from CmsMsgSubmitEO t,OrganEO o" )
               .append(" where t.createUnitId=o.organId  and t.useCount>0");
        Map<String, Object> map = new HashMap<String, Object>();
        if(null!=queryVO.getSiteId()){
            hql.append(" and t.siteId=:siteId");
            map.put("siteId", queryVO.getSiteId());
        }
        if(!AppUtil.isEmpty(queryVO.getStartDate())&&!AppUtil.isEmpty(queryVO.getEndDate())){
            hql.append(" and t.publishDate>=:startDate and t.publishDate<=:endDate");
            map.put("startDate", queryVO.getStartDate());
            map.put("endDate", queryVO.getEndDate());
        }
        hql.append(" group by t.createUnitId,o.name");
        hql.append(" order by count(t.id) desc");
        return (List<ContentChartVO>) getBeansByHql(hql.toString(), map, ContentChartVO.class, queryVO.getNum());
    }

    @Override
    public List<ContentChartVO> getEmpContentChart1(ContentChartQueryVO queryVO) {
        StringBuffer hql=new StringBuffer("select t.createOrganId as organId ,o.name as organName,count(t.id) as count from CmsMsgSubmitEO t,OrganEO o" )
                .append(" where t.createOrganId=o.organId  and t.useCount>0");
        Map<String, Object> map = new HashMap<String, Object>();
        if(null!=queryVO.getSiteId()){
            hql.append(" and t.siteId=:siteId");
            map.put("siteId", queryVO.getSiteId());
        }
        if(!AppUtil.isEmpty(queryVO.getStartDate())&&!AppUtil.isEmpty(queryVO.getEndDate())){
            hql.append(" and t.publishDate>=:startDate and t.publishDate<=:endDate");
            map.put("startDate", queryVO.getStartDate());
            map.put("endDate", queryVO.getEndDate());
        }
        hql.append(" group by t.createOrganId,o.name");
        hql.append(" order by count(t.id) desc");
        return (List<ContentChartVO>) getBeansByHql(hql.toString(), map, ContentChartVO.class, queryVO.getNum());
    }

    @Override
    public List<ContentChartVO> getEmpContentChart2(ContentChartQueryVO queryVO) {
        StringBuffer hql=new StringBuffer("select t.createUserId as organId ,p.name as OrganName,count(t.id) as count from CmsMsgSubmitEO t" )
                .append(",PersonEO p where t.createUserId=p.userId and t.useCount>0 ");
        Map<String, Object> map = new HashMap<String, Object>();
        if(null!=queryVO.getSiteId()){
            hql.append(" and t.siteId=:siteId");
            map.put("siteId", queryVO.getSiteId());
        }
        if(!AppUtil.isEmpty(queryVO.getStartDate())&&!AppUtil.isEmpty(queryVO.getEndDate())){
            hql.append(" and t.publishDate>=:startDate and t.publishDate<=:endDate");
            map.put("startDate", queryVO.getStartDate());
            map.put("endDate", queryVO.getEndDate());
        }
        hql.append(" group by t.createUserId,p.name");
        hql.append(" order by count(t.id) desc");
        return (List<ContentChartVO>) getBeansByHql(hql.toString(), map, ContentChartVO.class, queryVO.getNum());
    }
    @Override
    public List<SubmitListVO> getSubmitList(StatisticsQueryVO vo){
        StringBuffer sql=new StringBuffer("select u.c count,v.c employCount,(u.c-v.c) unEmployCount,(v.c/u.c)*100 rate,u.organId as organId from(")
                .append(" select count(*) c,c.create_unit_id organId from CMS_MSG_SUBMIT c ")
                .append(" where c.site_id=" + vo.getSiteId());
        if(!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.publish_date>=to_date('" + vo.getStartDate()+"','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getEndDate())){
            sql.append("  and c.publish_date<=to_date('" + vo.getEndDate()+"','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getYearAndMonth())){
            sql.append("  and c.publish_date>=to_date('" + vo.getYearAndMonth()+"-01 00:00:00','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getYearAndMonth())){
            sql.append("  and c.publish_date<=to_date('" + this.getDayByYearAndMonth(vo.getYearAndMonth())+" 23:59:59','yyyy-mm-dd:hh24:mi:ss')");
        }

        sql.append(" group by c.create_unit_id) u left join (")
                .append("select count(*) c,c.create_unit_id organId from CMS_MSG_SUBMIT c ")
                .append(" where  c.site_id=" + vo.getSiteId()+"  and c.use_count>0");
        if(!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.publish_date>=to_date('" + vo.getStartDate()+"','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getEndDate())){
            sql.append("  and c.publish_date<=to_date('" + vo.getEndDate()+"','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getYearAndMonth())){
            sql.append("  and c.publish_date>=to_date('" + vo.getYearAndMonth()+"-01 00:00:00','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getYearAndMonth())){
            sql.append("  and c.publish_date<=to_date('" + this.getDayByYearAndMonth(vo.getYearAndMonth())+" 23:59:59','yyyy-mm-dd:hh24:mi:ss')");
        }
        sql.append("   group by c.create_unit_id ) v")
                .append(" on u.organId=v.organId");
        List<String> fields = new ArrayList<String>();
        fields.add("count");
        fields.add("employCount");
        fields.add("unPublishCount");
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return (List<SubmitListVO> )getBeansBySql(sql.toString(), new Object[]{}, SubmitListVO.class, fields.toArray(arr));

    }

    @Override
    public List<SubmitListVO> getSubmitList1(StatisticsQueryVO vo) {
        StringBuffer sql=new StringBuffer("select u.c count,v.c employCount,(u.c-v.c) unEmployCount,(v.c/u.c)*100 rate,u.organId as organId from(")
                .append(" select count(*) c,c.create_organ_id organId from CMS_MSG_SUBMIT c ")
                .append(" where c.site_id=" + vo.getSiteId());
        if(!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.publish_date>=to_date('" + vo.getStartDate()+"','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getEndDate())){
            sql.append("  and c.publish_date<=to_date('" + vo.getEndDate()+"','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getYearAndMonth())){
            sql.append("  and c.publish_date>=to_date('" + vo.getYearAndMonth()+"-01 00:00:00','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getYearAndMonth())){
            sql.append("  and c.publish_date<=to_date('" + this.getDayByYearAndMonth(vo.getYearAndMonth())+" 23:59:59','yyyy-mm-dd:hh24:mi:ss')");
        }
        sql.append(" group by c.create_organ_id) u left join (")
                .append("select count(*) c,c.create_organ_id organId from CMS_MSG_SUBMIT c ")
                .append(" where  c.site_id=" + vo.getSiteId()+" and c.use_count>0");
        if(!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.publish_date>=to_date('" + vo.getStartDate()+"','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getEndDate())){
            sql.append("  and c.publish_date<=to_date('" + vo.getEndDate()+"','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getYearAndMonth())){
            sql.append("  and c.publish_date>=to_date('" + vo.getYearAndMonth()+"-01 00:00:00','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getYearAndMonth())){
            sql.append("  and c.publish_date<=to_date('" + this.getDayByYearAndMonth(vo.getYearAndMonth())+" 23:59:59','yyyy-mm-dd:hh24:mi:ss')");
        }
        sql.append("   group by c.create_organ_id ) v")
                .append(" on u.organId=v.organId");
        List<String> fields = new ArrayList<String>();
        fields.add("count");
        fields.add("employCount");
        fields.add("unPublishCount");
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return (List<SubmitListVO> )getBeansBySql(sql.toString(), new Object[]{}, SubmitListVO.class, fields.toArray(arr));

    }


    @Override
    public List<SubmitListVO> getSubmitList2(StatisticsQueryVO vo) {
        StringBuffer sql=new StringBuffer("select u.c count,v.c employCount,(u.c-v.c) unEmployCount,(v.c/u.c)*100 rate,u.userId as userId from(")
                .append(" select count(*) c,c.create_user_id userId from CMS_MSG_SUBMIT c ")
                .append(" where c.site_id=" + vo.getSiteId());
        if(!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.publish_date>=to_date('" + vo.getStartDate()+"','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getEndDate())){
            sql.append("  and c.publish_date<=to_date('" + vo.getEndDate()+"','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getYearAndMonth())){
            sql.append("  and c.publish_date>=to_date('" + vo.getYearAndMonth()+"-01 00:00:00','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getYearAndMonth())){
            sql.append("  and c.publish_date<=to_date('" + this.getDayByYearAndMonth(vo.getYearAndMonth())+" 23:59:59','yyyy-mm-dd:hh24:mi:ss')");
        }
        sql.append(" group by c.create_user_id) u left join (")
                .append("select count(*) c,c.create_user_id userId from CMS_MSG_SUBMIT c ")
                .append(" where  c.site_id=" + vo.getSiteId()+" and c.use_count>0");
        if(!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.publish_date>=to_date('" + vo.getStartDate()+"','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getEndDate())){
            sql.append("  and c.publish_date<=to_date('" + vo.getEndDate()+"','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getYearAndMonth())){
            sql.append("  and c.publish_date>=to_date('" + vo.getYearAndMonth()+"-01 00:00:00','yyyy-mm-dd:hh24:mi:ss')");
        }
        if(!StringUtils.isEmpty(vo.getYearAndMonth())){
            sql.append("  and c.publish_date<=to_date('" + this.getDayByYearAndMonth(vo.getYearAndMonth())+" 23:59:59','yyyy-mm-dd:hh24:mi:ss')");
        }
        sql.append("   group by c.create_user_id) v")
                .append(" on u.userId=v.userId");
        List<String> fields = new ArrayList<String>();
        fields.add("count");
        fields.add("employCount");
        fields.add("unPublishCount");
        fields.add("rate");
        fields.add("userId");
        String[] arr = new String[fields.size()];
        Calendar c = Calendar.getInstance();
        c.set(2018, 2, 0);
        c.get(Calendar.DAY_OF_MONTH);
        return (List<SubmitListVO> )getBeansBySql(sql.toString(), new Object[]{}, SubmitListVO.class, fields.toArray(arr));
    }
    //获取某年某月有多少天
    public String getDayByYearAndMonth(String yearAndMonth){
        int year = Integer.parseInt(yearAndMonth.substring(0,4));
        int month = Integer.parseInt(yearAndMonth.substring(5,7));
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0);
        int day =  c.get(Calendar.DAY_OF_MONTH);
        String date = yearAndMonth + "-" + day;
        return date;
    }

}
