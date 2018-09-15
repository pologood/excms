package cn.lonsun.content.onlineDeclaration.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.onlineDeclaration.internal.dao.IDeclarationRecordDao;
import cn.lonsun.content.onlineDeclaration.internal.entity.DeclarationRecordEO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-13<br/>
 */
@Repository("declarationRecordDao")
public class DeclarationRecordDaoImpl extends MockDao<DeclarationRecordEO> implements IDeclarationRecordDao {
    @Override
    public Pagination getPage(PageQueryVO pageVO, Long declarationId) {
        StringBuffer hql=new StringBuffer("select id as id, declarationId as declarationId,createOrganId as createOrganId")
                .append(",remark as remark ,createDate as createDate,transToId as transToId" )
                .append(" from DeclarationRecordEO where 1=1 and declarationId="+declarationId);
       return getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), new Object[]{}, DeclarationRecordEO.class);
    }

}
