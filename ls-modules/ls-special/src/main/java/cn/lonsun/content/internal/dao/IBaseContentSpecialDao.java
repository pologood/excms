package cn.lonsun.content.internal.dao;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.dao.IMockDao;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface IBaseContentSpecialDao  extends IMockDao<BaseContentEO> {
    /**
     * @param columnId
     * @return
     * @Description 按栏目获取记录条数 get count by column
     * @author Hewbing
     * @date 2015年10月9日 下午3:49:32
     */
    public Long getCountByColumnId(Long columnId);
}
