package cn.lonsun.nlp.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.nlp.internal.dao.INlpKeyWordsArticleRelDao;
import cn.lonsun.nlp.internal.entity.NlpKeyWordsArticleRelEO;
import cn.lonsun.nlp.internal.vo.ContentVO;
import cn.lonsun.nlp.internal.vo.NlpKeyWordsVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * 关键词与文章对应关系
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
@Repository
public class NlpKeyWordsArticleRelDaoImpl extends BaseDao<NlpKeyWordsArticleRelEO> implements INlpKeyWordsArticleRelDao {

    @Override
    public void delByContentId(Long contentId) {
        String hql = " delete from NlpKeyWordsArticleRelEO where contentId = ? ";
        executeUpdateByHql(hql,new Object[]{contentId});
    }

    @Override
    public List<NlpKeyWordsArticleRelEO> getByContentId(Long contentId) {
        String hql = "from NlpKeyWordsArticleRelEO where contentId = ? ";
        return getEntitiesByHql(hql,new Object[]{contentId});
    }

    @Override
    public List<ContentVO> queryContentsByKeyWordId(Long[] keyWordId,Long siteId, Date st, Date ed,Integer num) {
        StringBuffer hql = new StringBuffer();
        Map<String,Object> param = new HashMap<String,Object>();
        hql.append("select distinct b.id as contentId , b.title as title,b.siteId as siteId,b.columnId as columnId, ");
        hql.append(" b.typeCode as typeCode,b.publishDate as publishDate,b.redirectLink as redirectLink ");
        hql.append(" from NlpKeyWordsArticleRelEO r, BaseContentEO b ");
        hql.append(" where r.contentId = b.id and b.recordStatus = :recordStatus and b.isPublish = :isPublish ");
        hql.append(" and r.keyWordId in (:keyWordId) and b.siteId = :siteId ");
        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        param.put("isPublish", 1);
        param.put("keyWordId", keyWordId);
        param.put("siteId", siteId);

        if(st!=null){
            hql.append(" and b.publishDate >= :st ");
            param.put("st", st);
        }

        if(ed!=null){
            hql.append(" and b.publishDate <= :ed ");
            param.put("ed", ed);
        }

        hql.append(" order by b.publishDate desc,b.id desc ");
        return (List<ContentVO>)getBeansByHql(hql.toString(),param, ContentVO.class,num);
    }

    @Override
    public Pagination queryContentPage(Long[] keyWordId, Long siteId, Date st, Date ed, Long pageIndex, Integer pageSize) {
        StringBuffer sql = new StringBuffer();
        List<Object> valus = new ArrayList<Object>();
        List<String> fields = new ArrayList<String>();
        sql.append(" from ( ");
        sql.append(" select distinct b.ID contentId , b.TITLE title,b.SITE_ID siteId,b.COLUMN_ID columnId, ");
        sql.append(" b.TYPE_CODE typeCode,b.PUBLISH_DATE publishDate,b.REDIRECT_LINK redirectLink ");
        sql.append(" from CMS_NLP_KEY_WORDS_ARTICLE_REL r, CMS_BASE_CONTENT b ");
        sql.append(" where r.CONTENT_ID = b.ID and b.RECORD_STATUS = ? and b.IS_PUBLISH = ? ");
        sql.append(" and r.KEY_WORD_ID in ("+ StringUtils.join(keyWordId,",")+") and b.SITE_ID = ? ");
        valus.add(AMockEntity.RecordStatus.Normal.toString());
        valus.add(1);
        valus.add(siteId);

        if(st!=null){
            sql.append(" and b.publish_Date >= ? ");
            valus.add(st);
        }

        if(ed!=null){
            sql.append(" and b.publish_Date <= ? ");
            valus.add(ed);
        }

        sql.append(" order by b.PUBLISH_DATE desc,b.ID desc ) ");

        fields.add("contentId");
        fields.add("title");
        fields.add("siteId");
        fields.add("contentId");
        fields.add("columnId");
        fields.add("typeCode");
        fields.add("publishDate");
        fields.add("redirectLink");
        return getPaginationBySql(pageIndex,pageSize,sql.toString(),valus.toArray(),ContentVO.class,fields.toArray(new String[]{}));
    }
}
