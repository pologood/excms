package cn.lonsun.special.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.special.internal.entity.SpecialSkinsEO;

import java.util.List;

/**
 * Created by doocal on 2016-10-15.
 */
public interface ISpecialSkinsDao extends IMockDao<SpecialSkinsEO> {


    /**
     * 获取样式列表
     *
     * @param eo
     * @return
     */
    List<SpecialSkinsEO> getThemeSkinList(SpecialSkinsEO eo);

    /**
     * 根据主题 ID 删除样式
     *
     * @param id
     */
    void deleteByThemeId(Long id);

}
