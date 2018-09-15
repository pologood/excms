package cn.lonsun.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.internal.dao.IDataimportMemberImportDao;
import cn.lonsun.internal.entity.DataimportMemberImportEO;
import cn.lonsun.internal.service.IDataimportMemberImportService;
import cn.lonsun.target.datamodel.organperson.MemberExportQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * Created by lonsun on 2018-2-22.
 */
@Service
public class DataimportMemberImportServiceImpl extends MockService<DataimportMemberImportEO> implements IDataimportMemberImportService {
    @Autowired
    private IDataimportMemberImportDao dataimportMemberImportDao;
    /**
     * 根据oldId查询
     * @param oldId
     * @return
     */
    @Override
    public List<DataimportMemberImportEO> getDataByOldId(Long oldId) {
        return dataimportMemberImportDao.getDataByOldId(oldId);
    }

    @Override
    public Pagination getRelationPage(MemberExportQueryVO memberExportQueryVO) {
        return dataimportMemberImportDao.getRelationPage(memberExportQueryVO);
    }
}
