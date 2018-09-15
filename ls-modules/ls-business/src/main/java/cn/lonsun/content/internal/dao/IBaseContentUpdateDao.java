package cn.lonsun.content.internal.dao;

import cn.lonsun.content.internal.entity.BaseContentUpdateEO;
import cn.lonsun.content.vo.BaseContentUpdateQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

import java.util.List;

/**
 * Created by liuk on 2017/6/30.
 */
public interface IBaseContentUpdateDao extends IBaseDao<BaseContentUpdateEO> {

    Pagination getPagination(BaseContentUpdateQueryVO queryVO);

    int getCountByColumnId(BaseContentUpdateQueryVO queryVO);

    List<BaseContentUpdateEO> getList(BaseContentUpdateQueryVO queryVO);
}
