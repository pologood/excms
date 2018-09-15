package cn.lonsun.lsrobot.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lsrobot.entity.LsRobotFilterEO;

import java.util.Map;

public interface ILsRobotFilterDao extends IBaseDao<LsRobotFilterEO> {

    /**
     * 查询分页
     * @param pageinfo
     * @param param
     * @return
     * @author zhongjun
     */
    public Pagination getPage(PageQueryVO pageinfo, Map<String, Object> param);

    public String[] getAllKeyWords();

}
