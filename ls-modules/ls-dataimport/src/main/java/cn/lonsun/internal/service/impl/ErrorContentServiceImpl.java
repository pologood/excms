package cn.lonsun.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.internal.dao.IErrorContentDao;
import cn.lonsun.internal.entity.ErrorContentEO;
import cn.lonsun.internal.metadata.DataModule;
import cn.lonsun.internal.service.IErrorContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入失败的内容记录
 * @author zhongjun
 */
@Service
public class ErrorContentServiceImpl extends MockService<ErrorContentEO> implements IErrorContentService {

    @Autowired
    private IErrorContentDao errorContentDao;

    @Override
    public void saveFailureContents(List<ErrorContentEO> errorContentList) {
        errorContentDao.save(errorContentList);
    }

    /**
     * 获取某个数据类型的错误数据
     * @author zhongjun
     */
    @Override
    public List<ErrorContentEO> getFailureContents() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", ErrorContentEO.RecordStatus.Normal.toString());
        return errorContentDao.getEntities(ErrorContentEO.class, map);
    }

}
