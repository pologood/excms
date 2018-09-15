package cn.lonsun.content.internal.service;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.service.IMockService;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface IBaseContentSpecialService extends IMockService<BaseContentEO> {

    /**
     * @param columnId
     * @return
     * @Description 根据栏目iD查询记录条数
     * @author Hewbing
     * @date 2015年9月22日 下午2:01:01
     */
    public Long getCountByColumnId(Long columnId);
}
