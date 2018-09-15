package cn.lonsun.lsrobot.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lsrobot.entity.LsRobotFilterEO;

import java.util.Map;

public interface ILsRobotFilterService extends IBaseService<LsRobotFilterEO> {

    public Pagination getPage(PageQueryVO pageInfo, Map<String, Object> param);

    public String[] getAllKeyWords();

}
