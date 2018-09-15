package cn.lonsun.demo.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.demo.dao.IDemoDao;
import cn.lonsun.demo.entity.DemoEO;
import cn.lonsun.demo.service.IDemoService;
import cn.lonsun.demo.vo.DemoQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-6<br/>
 */
@Service("demoService")
public class DemoServiceImpl extends MockService<DemoEO> implements IDemoService {

    @Autowired
    private IDemoDao demoDao;

    @Override
    public Pagination getDemoPage(DemoQueryVO queryVO) {
        return demoDao.getDemoPage(queryVO);
    }

    @Override
    public List<DemoEO> getDemoListByCodeAndName(String code, String name) {
        return demoDao.getDemoListByCodeAndName(code,name);
    }

    @Override
    public DemoEO getDemoInfoById(Long id) {
        return demoDao.getDemoInfoById(id);
    }

    @Override
    public Object saveDemoInfo(DemoEO eo) {
        return demoDao.saveDemoInfo(eo);
    }

    @Override
    public void updateDemoInfo(DemoEO eo) {
        demoDao.updateDemoInfo(eo);
    }

    @Override
    public void deleteDemoInfo(Long id) {
        demoDao.deleteDemoInfo(id);
    }
}
