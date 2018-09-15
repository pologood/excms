package cn.lonsun.site.template.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.site.template.internal.dao.ITplConfDao;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-8-25 11:53
 */
@Repository("tplConfDao")
public class TplConfDao extends BaseDao<TemplateConfEO> implements ITplConfDao {

    private final static String TREE_DEL_SQL = "DELETE FROM CMS_TEMPLATE_CONF WHERE ID IN(SELECT ID FROM CMS_TEMPLATE_CONF B START WITH B.PARENT_ID = ? CONNECT BY PRIOR B.ID = B.PARENT_ID)";

    private final static String DEL_SQL = "DELETE FROM CMS_TEMPLATE_CONF WHERE ID = ?";

    private final static String UPDATE_TEMP_FILE_SQL = "UPDATE CMS_TEMPLATE_CONF SET TEMP_FILE = ? WHERE ID = ?";

    @Override
    public Pagination getPageEOList(ParamDto paramDto) {

        Long pageIndex = paramDto.getPageIndex();
        Integer pageSize = paramDto.getPageSize();
        String tempName = paramDto.getTempName();
        String staionId = paramDto.getStationId() + "";
        String tempType = paramDto.getTempType();
        StringBuilder hql = new StringBuilder("from TemplateConfEO where tempType = ? and stationId like ? ");
        Object[] values = new Object[]{tempType, "%" + SqlUtil.prepareParam4Query(staionId) + "%"};
        if (!AppUtil.isEmpty(tempName)) {
            String name = tempName.trim();
            hql.append(" and tempName like ? ");
            values = new Object[]{tempType, "%" + SqlUtil.prepareParam4Query(staionId) + "%", "%" + SqlUtil.prepareParam4Query(name) + "%"};
        }
        hql.append(" and type='" + paramDto.getTplType() + "'");
        Pagination page = getPagination(pageIndex, pageSize, hql.toString(), values);
        return page;
    }

    @Override
    public Object getEOList(Long siteId, String type) {
        StringBuilder hql = new StringBuilder("from TemplateConfEO where 1=1");
        if (!type.equals(TemplateConfEO.Type.Virtual.toString())) {
            hql.append(" and siteId=" + siteId);
        }
        //临时 硬代码排除
        hql.append(" and (tempType<>'special_column' and tempType<>'special_content') ");

        /*List<Object> values = new ArrayList<Object>();
        String[] typeArr = StringUtils.split(type, ",");
        hql.append(" and (");
        for (int i = 0, l = typeArr.length; i < l; i++) {
            if (i > 0) {
                hql.append(" or ");
            }
            hql.append(" type=?");
            values.add(typeArr[i]);
        }
        hql.append(" )");*/

        hql.append("order by createDate desc ");
        return this.getEntitiesByHql(hql.toString(), new Object[]{});
    }

    @Override
    public Object getByTemptype(Long siteId, String tempType) {
        StringBuilder hql = new StringBuilder("from TemplateConfEO where tempType=?");
        hql.append(" and siteId=?");
        return this.getEntitiesByHql(hql.toString(), new Object[]{tempType, siteId});
    }

    @Override
    public List<TemplateConfEO> getSpecialById(Long siteId, Long specialId, String tempType) {

        List<Object> values = new ArrayList<Object>();
        StringBuilder hql = new StringBuilder("from TemplateConfEO where 1=1");

        if (!AppUtil.isEmpty(siteId)) {
            hql.append(" and siteId=?");
            values.add(siteId);
        }

        if (!AppUtil.isEmpty(specialId)) {
            hql.append(" and specialId=?");
            values.add(specialId);
        }

        if (!AppUtil.isEmpty(tempType)) {
            hql.append(" and tempType=?");
            values.add(tempType);
        }

        return this.getEntitiesByHql(hql.toString(), values.toArray());
    }

    @Override
    public Object getVrtpls() {
        StringBuilder hql = new StringBuilder("from TemplateConfEO where type=? and leaf=1");
        return this.getEntitiesByHql(hql.toString(), new Object[]{TemplateConfEO.Type.Virtual.toString()});
    }

    @Override
    public Object getEOById(Long id) {

        List<TemplateConfEO> list = this.getEntitiesByHql("from TemplateConfEO T where T.id = ?", new Object[]{id});
        if (list.size() > 0)
            return list.get(0);
        else
            return new TemplateConfEO();
    }

    @Override
    public Object addEO(TemplateConfEO eo) {
        return this.save(eo);
    }

    @Override
    public Object delEO(Long id) {
        //this.executeUpdateBySql(TREE_DEL_SQL, new Object[]{id});//删除子元素
        return this.executeUpdateBySql(DEL_SQL, new Object[]{id});
    }

    @Override
    public Object editEO(TemplateConfEO eo) {
        return null;
    }

    @Override
    public void updateTempFile(String path, Long id) {
        this.executeUpdateBySql(UPDATE_TEMP_FILE_SQL, new Object[]{path, id});
    }

    @Override
    public Object getList(String stationId, String tempType) {
        String hql = "from TemplateConfEO where tempType = ? and siteId like ? ";
        Object[] values = new Object[]{tempType, "%" + SqlUtil.prepareParam4Query(stationId) + "%"};
        return this.getEntitiesByHql(hql, values);
    }

}
