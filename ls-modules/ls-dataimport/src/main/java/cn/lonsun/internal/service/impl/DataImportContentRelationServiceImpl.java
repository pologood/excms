package cn.lonsun.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.internal.dao.IDataImportContentRelationDao;
import cn.lonsun.internal.entity.DataImportContentRelationEO;
import cn.lonsun.internal.service.IDataImportContentRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 综合信息内容对应关系
 * @author liuk
 */
@Service
public class DataImportContentRelationServiceImpl extends MockService<DataImportContentRelationEO> implements IDataImportContentRelationService {

    @Autowired
    private IDataImportContentRelationDao dataImportContentRelationDao;


}
