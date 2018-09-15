package cn.lonsun.internal.service.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.internal.dao.IErrorPublicContentDao;
import cn.lonsun.internal.dao.IPublicUnitRelationDao;
import cn.lonsun.internal.entity.ErrorContentEO;
import cn.lonsun.internal.entity.ErrorPublicContentEO;
import cn.lonsun.internal.entity.PublicUnitRelationEO;
import cn.lonsun.internal.service.IErrorPublicContentService;
import cn.lonsun.internal.service.IPublicUnitRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入失败的内容记录
 * @author zhongjun
 */
@Service("publicUnitRelationService")
public class PublicUnitRelationServiceImpl extends MockService<PublicUnitRelationEO> implements IPublicUnitRelationService {

    @Autowired
    private IPublicUnitRelationDao publicUnitRelationDao;

    @Override
    public List<PublicUnitRelationEO> getByOldId(Long siteId, String oldId) {
        return publicUnitRelationDao.getByOldId(siteId, oldId);
    }

    @Override
    public void saveRelation(String oldId, Long newId, String oldName, String newName) {
        publicUnitRelationDao.save(new PublicUnitRelationEO(newId, newName, oldId, oldName));
    }

    @Override
    public void deleteAll() {
        publicUnitRelationDao.executeUpdateByHql("delete from PublicUnitRelationEO", new Object[]{});
    }

    @Override
    public void deleteMany(String... oldId) {
        publicUnitRelationDao.deleteByOldId(oldId);
    }
}
