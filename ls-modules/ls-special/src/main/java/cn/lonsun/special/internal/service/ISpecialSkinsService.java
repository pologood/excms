package cn.lonsun.special.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.special.internal.entity.SpecialSkinsEO;

import java.util.List;

/**
 * Created by doocal on 2016-10-15.
 */
public interface ISpecialSkinsService extends IMockService<SpecialSkinsEO> {


    /**
     * 保存专题主题
     *
     * @param eo
     */
    void saveSpecialSkins(SpecialSkinsEO eo);


    /**
     * 获取列表list
     *
     * @param eo
     * @return
     */
    List<SpecialSkinsEO> getThemeSkinList(SpecialSkinsEO eo);

    /**
     * 获取列表map
     *
     * @param siteId
     * @param themeId
     * @return
     */
    List<SpecialSkinsEO> getSkinsItem(Long siteId, Long themeId);

    /**
     * 获取专题皮肤信息
     *
     * @param siteId
     * @param themeId
     * @return
     */
    SpecialSkinsEO getDefaultSkins(Long siteId, Long themeId);

    /**
     * 根据主题 ID 删除皮肤样式
     */
    void deleteByThemeId(Long id);
}
