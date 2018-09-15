package cn.lonsun.site.contentModel.internal.dao.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.contentModel.internal.dao.IContentModelDao;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-8<br/>
 */
@Repository("contentModelDao")
public class ContentModelDaoImpl extends MockDao<ContentModelEO> implements IContentModelDao {
    @Override
    public Pagination getPage(Long pageIndex, Integer pageSize, String name, Long siteId, Integer isPublic) {
        //String hql=" from ContentModelEO where 1=1 and recordStatus='"+ AMockEntity.RecordStatus.Normal.toString()+"' and siteId="+siteId;
        StringBuffer hql = new StringBuffer(" select r.id as id,r.name as name,r.code as code,r.description as description")
            .append(",r.siteId as siteId ,r.content as content,m.type as type,m.tplId as tplId")
            .append(",m.articalTempId as articalTempId, m.modelTypeCode as modelTypeCode,m.columnTempId as columnTempId ")
            .append(",m.wapColumnTempId as wapColumnTempId,m.wapArticalTempId as wapArticalTempId")
            .append(" from ContentModelEO  r, ModelTemplateEO m ")
            .append(" where 1=1 and r.id=m.modelId and r.recordStatus=? and m.recordStatus=? and m.type=1 ");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        if (isPublic == 1) {
            hql.append(" and r.isPublic=1");
        } else {
            hql.append("  and r.siteId=? and r.isPublic=0");
            values.add(siteId);
        }

        if (!StringUtils.isEmpty(name)) {
            hql.append(" and r.name like ?");
            values.add("%".concat(name.trim()).concat("%"));
        }
        if (pageIndex == null || pageIndex < 0) {
            pageIndex = 0L;
        }
        if (pageSize == null || pageSize < 0) {
            pageSize = 10;
        }
        hql.append(" order by r.createDate desc");
        Pagination page = getPagination(pageIndex, pageSize, hql.toString(), values.toArray(), ContentModelVO.class);
        if (page != null && page.getData() != null && page.getData().size() > 0) {
            List<ContentModelVO> list = (List<ContentModelVO>) page.getData();
            for (ContentModelVO vo : list) {
                if (!StringUtils.isEmpty(vo.getModelTypeCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("column_type", vo.getModelTypeCode());
                    if (dictVO != null) {
                        vo.setModelTypeName(dictVO.getKey());
                    }
                }
                if (null == vo.getIsPublic() || vo.getIsPublic() == 0) {
                    TemplateConfEO t = null;
                    if (vo.getColumnTempId() != null) {
                        t = CacheHandler.getEntity(TemplateConfEO.class, vo.getColumnTempId());
                        if (t != null) {
                            vo.setColumnTempName(t.getName());
                        }
                    }
                    if (vo.getArticalTempId() != null) {
                        t = CacheHandler.getEntity(TemplateConfEO.class, vo.getArticalTempId());
                        if (t != null) {
                            vo.setArticalTempName(t.getName());
                        }
                    }
                    if (vo.getWapArticalTempId() != null) {
                        t = CacheHandler.getEntity(TemplateConfEO.class, vo.getWapArticalTempId());
                        if (t != null) {
                            vo.setWapArticalTempName(t.getName());
                        }
                    }
                    if (vo.getWapColumnTempId() != null) {
                        t = CacheHandler.getEntity(TemplateConfEO.class, vo.getWapColumnTempId());
                        if (t != null) {
                            vo.setWapColumnTempName(t.getName());
                        }
                    }
                }
            }
            page.setData(list);
        }
        return page;
    }

    @Override
    public ContentModelVO getVO(Long id, Long siteId) {
        String hql = " select r.id as id,r.name as name,r.code as code,r.description as description"
            + ",r.siteId as siteId ,r.content as content,m.type as type,m.tplId as tplId,m.articalTempId as articalTempId,m.processId as processId,m.processName as processName"
            + ", m.modelTypeCode as modelTypeCode,m.columnTempId as columnTempId, m.wapColumnTempId as wapColumnTempId,m.wapArticalTempId as wapArticalTempId"
            + " from ContentModelEO  r, ModelTemplateEO m "
            + " where 1=1 and r.id=m.modelId and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and m.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and m.type=1 "
            + " and r.id=" + id;

        List<ContentModelVO> list = (List<ContentModelVO>) getBeansByHql(hql, new Object[]{}, ContentModelVO.class);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new ContentModelVO();

    }

    @Override
    public boolean checkModelType(String modelTypeCode, Long siteId, Long modelId, Integer isPublic) {
        String hql = " select r.id as id,r.name as name,r.code as code,r.description as description"
            + ",r.siteId as siteId ,r.content as content,m.type as type"
            + ",m.tplId as tplId,m.articalTempId as articalTempId, m.modelTypeCode as modelTypeCode,m.columnTempId as columnTempId "
            + " from ContentModelEO  r, ModelTemplateEO m "
            + " where 1=1 and r.id=m.modelId and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and m.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and m.type=1 "
            + "  and m.modelTypeCode='" + modelTypeCode + "'";
        if (isPublic == 1) {
            hql += " and r.isPublic=1";
        } else {
            hql += "  and r.siteId=" + siteId + " and r.isPublic=0";
        }
        List<ContentModelVO> list = (List<ContentModelVO>) getBeansByHql(hql, new Object[]{}, ContentModelVO.class);
        if (list == null || list.size() <= 0) {
            return true;
        } else {
            if (modelId != null && modelId.equals(list.get(0).getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ContentModelVO> getByColumnTypeCode(Long siteId, String codes) {
        StringBuffer hql =new StringBuffer(" select r.id as id,r.name as name,r.code as code,r.description as description")
        .append( ",r.siteId as siteId ,r.content as content,m.type as type,m.tplId as tplId")
        .append( ",m.articalTempId as articalTempId, m.modelTypeCode as modelTypeCode,m.columnTempId as columnTempId ")
        .append(" from ContentModelEO  r, ModelTemplateEO m ")
        .append( " where 1=1 and r.id=m.modelId and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and m.recordStatus='")
        .append( AMockEntity.RecordStatus.Normal.toString() + "' and m.type=1 ")
        .append( " and r.siteId=" + siteId);
        if (!StringUtils.isEmpty(codes)) {
            hql.append(" and m.modelTypeCode = '" + codes + "'");
        }
        hql.append(" order by r.createDate desc");
        List<ContentModelVO> list = (List<ContentModelVO>) getBeansByHql(hql.toString(), new Object[]{}, ContentModelVO.class);
        return list;
    }

    @Override
    public List<ContentModelVO> getByCodes(Long siteId, String[] codes) {

        StringBuffer hql = new StringBuffer();
        hql.append(" select r.id as id,r.name as name,r.code as code,r.description as description");
        hql.append(",r.siteId as siteId ,r.content as content,m.type as type");
        hql.append(",m.tplId as tplId,m.articalTempId as articalTempId, m.modelTypeCode as modelTypeCode,m.columnTempId as columnTempId ");
        hql.append(" from ContentModelEO  r, ModelTemplateEO m ");
        hql.append(" where 1=1 and r.id=m.modelId and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and m.recordStatus='");
        hql.append(AMockEntity.RecordStatus.Normal.toString() + "' and m.type=1 ");
        hql.append(" and r.siteId=" + siteId);

        if (codes != null && codes.length > 0) {
            StringBuffer t = new StringBuffer();
            for (int i = 0, l = codes.length; i < l; i++) {
                t.append("'").append(codes[i]).append("',");
            }
            t.deleteCharAt(t.length() - 1);
            hql.append(" and r.code in (" + t + ")");
        } else {
            hql.append(" and r.code in ('000000')");
        }

        hql.append(" order by r.createDate desc");
        List<ContentModelVO> list = (List<ContentModelVO>) getBeansByHql(hql.toString(), new Object[]{}, ContentModelVO.class);
        return list;
    }

}
