package cn.lonsun.content.onlinePetition.internal.dao.imp;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.onlinePetition.internal.dao.IRunRecordDao;
import cn.lonsun.content.onlinePetition.internal.entity.RunRecordEO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */
@Repository("runRecordDao")
public class RunRecordDaoImpl extends MockDao<RunRecordEO> implements IRunRecordDao {
    @Override
    public Pagination getPage(PageQueryVO pageVO,Long petitionId) {
        StringBuffer hql=new StringBuffer("select id as id, petitionId as petitionId,transUserName as transUserName ")
                .append(",transToName as transToName ,transIp as transIp,remark as remark ,createDate as createDate" )
                .append(" from RunRecordEO where 1=1 and petitionId="+petitionId);
        return getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), new Object[]{}, RunRecordEO.class);

    }
}
