package cn.lonsun.site.contentModel.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.site.contentModel.internal.dao.IContentModelSpecialDao;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Created by Administrator on 2017/9/26.
 */
@Repository
public class ContentModelSpecialDaoImpl extends MockDao<ContentModelEO> implements IContentModelSpecialDao  {
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
