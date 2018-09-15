package cn.lonsun.lsrobot.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lsrobot.dao.ILsRobotFilterDao;
import cn.lonsun.lsrobot.entity.LsRobotFilterEO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhongjun
 */
@Repository("lsRobotFilterDao")
public class LsRobotFilterDao extends BaseDao<LsRobotFilterEO> implements ILsRobotFilterDao {

    @Override
    public Pagination getPage(PageQueryVO pageinfo, Map<String, Object> param) {
        StringBuilder sql = new StringBuilder();
        sql.append("from LsRobotFilterEO t where 1=1 ");
        List<Object> values = new ArrayList<Object>();
        if(param.containsKey("keyWords") && param.get("keyWords") != null && !AppUtil.isEmpty(param.get("keyWords"))){
            sql.append(" and keyWords like ? ");
            values.add("%" + param.get("keyWords").toString() + "%");
        }
        return super.getPagination(pageinfo.getPageIndex(),pageinfo.getPageSize(), sql.toString(), values.toArray());
    }

    @Override
    public String[] getAllKeyWords() {
        String hql = "select keyWords as keyWords from LsRobotFilterEO t ";
        List<String> list = (List<String>)super.getObjects(hql, new Object[]{});
        return list.toArray(new String[]{});
    }
}
