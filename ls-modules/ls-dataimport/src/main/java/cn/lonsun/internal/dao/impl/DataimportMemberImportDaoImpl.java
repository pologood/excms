package cn.lonsun.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.internal.dao.IDataimportMemberImportDao;
import cn.lonsun.internal.entity.DataimportMemberImportEO;
import cn.lonsun.target.datamodel.organperson.MemberExportQueryVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by lonsun on 2018-2-22.
 */
@Repository
public class DataimportMemberImportDaoImpl extends MockDao<DataimportMemberImportEO> implements IDataimportMemberImportDao {
    @Override
    public List<DataimportMemberImportEO> getDataByOldId(Long oldId) {
        StringBuffer stringBuffer =new StringBuffer();
        stringBuffer.append("from  DataimportMemberImportEO d where d.oldMemberId=? and d.recordStatus=?");
        List<Object> param =new ArrayList<Object>();
        param.add(oldId);
        param.add(AMockEntity.RecordStatus.Normal.toString());

        return getEntitiesByHql(stringBuffer.toString(),param.toArray());
    }

    @Override
    public Pagination getRelationPage(MemberExportQueryVO memberExportQueryVO) {
        StringBuffer stringBuffer =new StringBuffer();
        stringBuffer.append("from  DataimportMemberImportEO d where  d.recordStatus=?");
        List<Object> param =new ArrayList<Object>();
        param.add(AMockEntity.RecordStatus.Normal.toString());
        if(AppUtil.isEmpty(memberExportQueryVO.getUid())){
            stringBuffer.append(" and d.uid =?");
            param.add(memberExportQueryVO.getUid());
        }
        if(AppUtil.isEmpty(memberExportQueryVO.getName())){
            stringBuffer.append(" and d.oldMemberName lie ?");
            param.add("%"+SqlUtil.prepareParam4Query(memberExportQueryVO.getUid())+"%");
        }


        return getPagination(memberExportQueryVO.getPageIndex(),memberExportQueryVO.getPageSize(),stringBuffer.toString(),param.toArray());
    }
}
