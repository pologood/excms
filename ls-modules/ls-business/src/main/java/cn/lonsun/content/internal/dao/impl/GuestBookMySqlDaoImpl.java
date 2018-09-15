package cn.lonsun.content.internal.dao.impl;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.GuestListVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-11-1<br/>
 */
@Repository("guestBookMySqlDao")
public class GuestBookMySqlDaoImpl extends GuestBookDaoImpl{
    @Override
    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO) {
        StringBuffer sql = new StringBuffer("select 100*(v.c/u.c) as rate,u.receive_id as organId from(")
                .append("select count(*) c,g.receive_id  from cms_base_content b,guestbook_table g ")
                .append("where b.id=g.base_content_id and b.record_status=? and b.site_id=?")
                .append(" and b.type_code=? and b.is_publish=1");
        List<Object> values = new ArrayList<Object>();
        values.add( AMockEntity.RecordStatus.Normal.toString());
        values.add(queryVO.getSiteId());
        values.add( BaseContentEO.TypeCode.guestBook.toString());
        if (!StringUtils.isEmpty(queryVO.getStartStr()) && !StringUtils.isEmpty(queryVO.getEndStr())) {
            sql.append(" and b.publish_date>=str_to_date(?,'%Y-%m-%d %T') and b.publish_date<=str_to_date(?,'%Y-%m-%d %T') ");
            values.add( queryVO.getStartStr());
            values.add( queryVO.getEndStr());
        }
        sql.append(" group by g.receive_id) u , (select count(*) c,g.receive_id  from cms_base_content b,guestbook_table g ")
                .append("where b.id=g.base_content_id and b.record_status=? and  g.deal_status in('handled','replyed') ")
                .append("and b.site_id=? and b.type_code=? and b.is_publish=1");

        values.add( AMockEntity.RecordStatus.Normal.toString());
        values.add(queryVO.getSiteId());
        values.add( BaseContentEO.TypeCode.guestBook.toString());
        if (!StringUtils.isEmpty(queryVO.getStartStr()) && !StringUtils.isEmpty(queryVO.getEndStr())) {
            sql.append(" and b.publish_date>=str_to_date(?,'%Y-%m-%d %T') and b.publish_date<=str_to_date(?,'%Y-%m-%d %T') ");
            values.add( queryVO.getStartStr());
            values.add( queryVO.getEndStr());
        }
        sql.append(" group by g.receive_id)v where u.receive_id=v.receive_id order by v.c/u.c desc ");

        List<String> fields = new ArrayList<String>();
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return (List<ContentChartVO>) getBeansBySql(sql.toString(), values.toArray(), ContentChartVO.class, fields.toArray(arr));
    }

    @Override
    public List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO) {
        StringBuffer sql = new StringBuffer(" select o.class_Code as type, count(*) as count")
                .append(" from cms_base_content c, guestbook_table o where c.id=o.base_content_Id and c.type_Code=?")
                .append(" and c.record_Status=? and c.is_publish=1 and c.site_id=?");
        List<Object> values = new ArrayList<Object>();
        values.add(BaseContentEO.TypeCode.guestBook.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(queryVO.getSiteId());
        if (!StringUtils.isEmpty(queryVO.getStartStr()) && !StringUtils.isEmpty(queryVO.getEndStr())) {
            sql.append(" and c.publish_date>=str_to_date(?,'%Y-%m-%d %T') and c.publish_date<=str_to_date(?,'%Y-%m-%d %T') ");
            values.add(queryVO.getStartStr());
            values.add(queryVO.getEndStr());
        }
        sql.append(" group by o.class_Code");
        List<String> fields = new ArrayList<String>();
        fields.add("type");
        fields.add("count");
        String[] arr = new String[fields.size()];
        List<ContentChartVO> list = (List<ContentChartVO>) getBeansBySql(sql.toString(), values.toArray(), ContentChartVO.class, fields.toArray(arr));
        return list;
    }

    @Override
    public Pagination getGuestPage(StatisticsQueryVO vo) {
        StringBuffer sql = new StringBuffer("select u.c recCount,v.c dealCount,(u.c-v.c) undoCount,100*(v.c/u.c) rate,u.organId as organId from(")
                .append(" select count(*) c,g.receive_id organId from cms_base_content c, guestbook_table g")
                .append(" where c.id=g.base_content_id and c.record_status=? and c.is_publish=1 and c.site_id=? and c.type_code=?");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(vo.getSiteId());
        values.add(vo.getTypeCode());
        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append(" and c.create_date>=str_to_date(?,'%Y-%m-%d %T') ");
            values.add(vo.getStartDate());
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=str_to_date(?,'%Y-%m-%d %T')");
            values.add(vo.getEndDate());
        }
        sql.append(" group by g.create_organ_id) u,(")
                .append("select count(*) c,g.receive_id organId from cms_base_content c, guestbook_table g ")
                .append(" where c.id=g.base_content_id and c.record_status=? and c.is_publish=1 and c.site_id=? and c.type_code=?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(vo.getSiteId());
        values.add(vo.getTypeCode());

        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=str_to_date(?,'%Y-%m-%d %T')");
            sql.append("  and c.publish_date>=str_to_date(?,'%Y-%m-%d %T')");
            values.add( vo.getStartDate());
            values.add( vo.getStartDate());
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=str_to_date(?,'%Y-%m-%d %T')");
            sql.append("  and c.publish_date<=str_to_date(?,'%Y-%m-%d %T')");
            values.add( vo.getEndDate());
            values.add( vo.getEndDate());
        }
        sql.append(" and  g.deal_status in('handled','replyed') group by g.receive_id ) v ")
                .append(" where u.organId=v.organId");
        List<String> fields = new ArrayList<String>();
        fields.add("recCount");
        fields.add("dealCount");
        fields.add("undoCount");
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return getPaginationBySql(vo.getPageIndex(), vo.getPageSize(), sql.toString(), values.toArray(), GuestListVO.class, fields.toArray(arr));
    }

    @Override
    public List<GuestListVO> getGuestList(StatisticsQueryVO vo) {

        StringBuffer sql = new StringBuffer("select u.c recCount,v.c dealCount,(u.c-v.c) undoCount,100*(v.c/u.c) rate,u.organId as organId from(")
                .append(" select count(*) c,g.receive_id organId from cms_base_content c, guestbook_table g")
                .append(" where c.id=g.base_content_id and  c.record_status=? and c.is_publish=1 and c.site_id=? and c.type_code=?");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(vo.getSiteId());
        values.add(vo.getTypeCode());
        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.publish_date>=str_to_date(?,'%Y-%m-%d %T')");
            values.add( vo.getStartDate());
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.publish_date<=str_to_date(?,'%Y-%m-%d %T')");
            values.add( vo.getEndDate());
        }
        if (!StringUtils.isEmpty(vo.getColumnIds())) {
            sql.append(" and c.column_id in ('" + vo.getColumnIds() + "')");
        }
        sql.append(" group by g.receive_id) u left join (")
                .append("select count(*) c,g.receive_id organId from cms_base_content c, guestbook_table g ")
                .append(" where c.id=g.base_content_id and c.record_status=? and  c.is_publish=1 and c.site_id=? and c.type_code=?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(vo.getSiteId());
        values.add(vo.getTypeCode());
        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.publish_date>=str_to_date(?,'%Y-%m-%d %T')");
            values.add( vo.getStartDate());
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.publish_date<=str_to_date(?,'%Y-%m-%d %T')");
            values.add( vo.getEndDate());
        }
        if (!StringUtils.isEmpty(vo.getColumnIds())) {
            sql.append(" and c.column_id in ('" + vo.getColumnIds() + "')");
        }
        sql.append(" and  g.deal_status in('handled','replyed') group by g.receive_id ) v ")
                .append(" on u.organId=v.organId order by v.c desc");
        List<String> fields = new ArrayList<String>();
        fields.add("recCount");
        fields.add("dealCount");
        fields.add("undoCount");
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return (List<GuestListVO>) getBeansBySql(sql.toString(), values.toArray(), GuestListVO.class, fields.toArray(arr));
    }




}
