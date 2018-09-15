package cn.lonsun.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.internal.entity.DataimportMemberImportEO;
import cn.lonsun.target.datamodel.organperson.MemberExportQueryVO;

import java.util.List;

/**
 *
 * Created by lonsun on 2018-2-22.
 */
public interface IDataimportMemberImportService extends IMockService<DataimportMemberImportEO> {
    List<DataimportMemberImportEO> getDataByOldId(Long oldId);

    Pagination getRelationPage(MemberExportQueryVO memberExportQueryVO);
}
