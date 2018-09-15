package cn.lonsun.rbac.indicator.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.indicator.internal.entity.IndicatorEO;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface IIndicatorSpecialService extends IMockService<IndicatorEO> {
    /**
     *
     * 保存菜单、按钮、站点、栏目
     *
     * @author fangtinghua
     * @param indicator
     */
    public void save(IndicatorEO indicator);

    /**
     *
     * 删除indicator
     *
     * @author fangtinghua
     * @param indicatorId
     */
    public void delete(Long indicatorId);

    /**
     *
     * 删除indicator
     *
     * @author fangtinghua
     * @param ids
     */
    public void delete(Long[] ids);
}
