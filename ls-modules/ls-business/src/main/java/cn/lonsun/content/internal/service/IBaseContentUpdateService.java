package cn.lonsun.content.internal.service;

import cn.lonsun.content.internal.entity.BaseContentUpdateEO;
import cn.lonsun.content.vo.BaseContentUpdateQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by liuk on 2017/6/30.
 */
public interface IBaseContentUpdateService extends IBaseService<BaseContentUpdateEO> {

    Pagination getPagination(BaseContentUpdateQueryVO queryVO);

    int getCountByColumnId(BaseContentUpdateQueryVO queryVO);

    void export(BaseContentUpdateQueryVO queryVO, HttpServletResponse response);

    void sendMessageByCurrentUser(Long userId);
}
