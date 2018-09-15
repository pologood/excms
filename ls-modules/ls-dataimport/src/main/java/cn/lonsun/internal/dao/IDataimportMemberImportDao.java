package cn.lonsun.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.internal.entity.DataimportMemberImportEO;
import cn.lonsun.target.datamodel.organperson.MemberExportQueryVO;

import java.util.List;

/**
 *
 * Created by lonsun on 2018-2-22.
 */
public interface IDataimportMemberImportDao extends IMockDao<DataimportMemberImportEO> {
    List<DataimportMemberImportEO> getDataByOldId(Long oldId);

    Pagination getRelationPage(MemberExportQueryVO memberExportQueryVO);
}
