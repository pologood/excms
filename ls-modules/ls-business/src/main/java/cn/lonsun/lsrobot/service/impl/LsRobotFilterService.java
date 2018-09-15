package cn.lonsun.lsrobot.service.impl;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lsrobot.dao.ILsRobotFilterDao;
import cn.lonsun.lsrobot.entity.LsRobotFilterEO;
import cn.lonsun.lsrobot.service.ILsRobotFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("lsRobotFilterService")
public class LsRobotFilterService extends BaseService<LsRobotFilterEO> implements ILsRobotFilterService {

    @Autowired
    private ILsRobotFilterDao lsRobotFilterDao;

    @Override
    public Pagination getPage(PageQueryVO pageInfo, Map<String, Object> param){
        return lsRobotFilterDao.getPage(pageInfo, param);
    }

    @Override
    public String[] getAllKeyWords() {

        return lsRobotFilterDao.getAllKeyWords();
    }
}
