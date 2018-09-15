package cn.lonsun.content.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IKnowledgeBaseDao;
import cn.lonsun.content.internal.entity.KnowledgeBaseEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.KnowledgeBaseVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.query.*;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author zhangmf
 * @version 2018-05-23 17:27
 */
@Repository
public class KnowledgeBaseDaoImpl extends BaseDao<KnowledgeBaseEO> implements IKnowledgeBaseDao {
    @Override
    public Pagination getPage(ContentPageVO query) {
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
                + "b.titleColor as titleColor,b.isBold as isBold,b.isUnderline as isUnderline,b.isTilt as isTilt,"
                + "b.typeCode as typeCode,b.subTitle as subTitle,b.isNew as isNew,b.resources as resources,b.isHot as isHot,b.isTop as isTop,"
                + "b.topValidDate as topValidDate,b.isTitle as isTitle,b.isPublish as isPublish,b.publishDate as publishDate,"
                + "b.author as author,b.isAllowComments as isAllowComments,b.isJob as isJob,"
                + "b.redirectLink as redirectLink,b.jobIssueDate as jobIssueDate,"
                + "k.createDate as createDate,k.knowledgeBaseId as knowledgeBaseId,k.categoryCode as categoryCode,k.categoryName as categoryName,k.content as content,k.replyContent as replyContent"
                + " from BaseContentEO b,KnowledgeBaseEO k where b.id = k.contentId and b.recordStatus= ? and b.columnId = ? and b.siteId = ? and b.typeCode = ?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(query.getColumnId());
        values.add(query.getSiteId());
        values.add(query.getTypeCode());
        if (!AppUtil.isEmpty(query.getTitle())){
            hql.append(" and b.title like ?");
            values.add("%".concat(query.getTitle()).concat("%"));
        }
        //来源
        if (!AppUtil.isEmpty(query.getResources())){
            hql.append(" and b.resources like ?");
            values.add("%".concat(query.getResources().trim()).concat("%"));
        }
        //分类
//        if (!AppUtil.isEmpty(query.getCondition())){
//            hql.append(" and k.categoryCode = ?");
//            values.add(query.getCondition());
//        }
        //分类名称
        if (!AppUtil.isEmpty(query.getCondition())){
            hql.append(" and k.categoryName like ?");
            values.add("%".concat(query.getCondition().trim()).concat("%"));
        }
        //传入id
        if (!AppUtil.isEmpty(query.getIdArray())){
            Long[] idArray = query.getIdArray();
            for (int i = 0 ; i < idArray.length ; i++) {
                if (i == 0) {
                    hql.append(" and k.knowledgeBaseId in (").append(idArray[i]);
                }else {
                    hql.append(",").append(idArray[i]);
                }

                if (i == idArray.length - 1){
                    hql.append(")");
                }
            }
        }
        if(query.getIsPublish() != null){
            hql.append(" and b.isPublish = ?");
            values.add(query.getIsPublish());
        }
        if (query.getNoAuthority() == null || !query.getNoAuthority()) {
            if (!RoleAuthUtil.isCurUserColumnAdmin(query.getColumnId()) && !LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
                if (null != LoginPersonUtil.getOrganId()) {
                    hql.append(" and b.createOrganId=" + LoginPersonUtil.getOrganId());
                }
            }
        }
        hql.append(" order by b.isTop desc,b.createDate desc");
        return getPagination(query.getPageIndex(),query.getPageSize(), hql.toString(), values.toArray(),KnowledgeBaseVO.class);
    }

    @Override
    public void batchCompletelyDelete(Long[] contentIds) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("contentIds", contentIds);
        String hql = "delete from KnowledgeBaseEO where contentId in (:contentIds)";
        executeUpdateByJpql(hql, map);
    }

    @Override
    public KnowledgeBaseEO getKnowledgeBaseByContentId(Long contentId) {
        return null;
    }

    @Override
    public List<KnowledgeBaseVO> getKnowledgeBaseVOS(String code) {
        return null;
    }

    // 以下为参数处理的私有方法

    private int executeUpdateByJpql(final String hql, final Map<String, Object> params){
        // 创建一个JpqlQuery对象
        JpqlQuery jpqlQuery = new JpqlQuery(hql);
        // 添加参数
        jpqlQuery.setParameters(params);
        // 构造Query对象,并处理参数
        Query query = getQuery(jpqlQuery);
        return query.executeUpdate();

    }

    private Query getQuery(JpqlQuery jpqlQuery) {
        Query query = getCurrentSession().createQuery(jpqlQuery.getJpql());
        processQuery(query, jpqlQuery);
        return query;
    }

    @SuppressWarnings("rawtypes")
    private void processQuery(Query query, BaseQuery originQuery) {
        processQuery(query, originQuery.getParameters(),
                originQuery.getFirstResult(), originQuery.getMaxResults());
        fillParameters(query, originQuery.getParameters());
        query.setFirstResult(originQuery.getFirstResult());
        if (originQuery.getMaxResults() > 0) {
            query.setMaxResults(originQuery.getMaxResults());
        }
    }

    private void processQuery(Query query, QueryParameters parameters,
                              int firstResult, int maxResults) {
        fillParameters(query, parameters);
        query.setFirstResult(firstResult);
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }
    }

    private void fillParameters(Query query, QueryParameters params) {
        if (params == null) {
            return;
        }
        if (params instanceof PositionalParameters) {
            fillParameters(query, (PositionalParameters) params);
        } else if (params instanceof NamedParameters) {
            // logger.info("use NamedParameters >>> ");
            // System.out.println("use NamedParameters >>> ");
            fillParameters(query, (NamedParameters) params);
        } else {
            throw new UnsupportedOperationException("不支持的参数形式");
        }
    }

    private void fillParameters(Query query, PositionalParameters params) {
        Object[] paramArray = params.getParams();
        for (int i = 0; i < paramArray.length; i++) {
            query = query.setParameter(i, paramArray[i]);
        }
    }

    @SuppressWarnings("rawtypes")
    private void fillParameters(Query query, NamedParameters params) {
        // logger.info("start fillParameters ... ");
        // System.out.println("start fillParameters ... ");
        for (Map.Entry<String, Object> entry : params.getParams().entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Collection) {
                query.setParameterList(entry.getKey(), (Collection) value);
            } else if (value.getClass().isArray()) {
                query.setParameterList(entry.getKey(), (Object[]) value);
            } else {
                query.setParameter(entry.getKey(), value);
            }
        }
    }
}
