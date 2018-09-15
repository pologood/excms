package cn.lonsun.special.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.special.internal.entity.SpecialThemeEO;
import cn.lonsun.special.internal.vo.SpecialThemeQueryVO;

import java.util.List;
import java.util.Map;

/**
 * Created by doocal on 2017-12-15.
 */
public interface ISpecialThemeService extends IMockService<SpecialThemeEO> {

    /**
     * 保存专题主题
     *
     * @param specialTheme
     */
    void saveSpecialTheme(SpecialThemeEO specialTheme);

    public Pagination getCloudList(Map<String,Object> param);

    /**
     * 从云平台下载主题
     * @param id
     * @param version 下载的版本
     */
    public void downloadSpecialTheme(Long id, Integer version);

    /**
     * 从云平台更新主题
     * @param id
     */
    public void updateSpecialTheme(Long id);


    /**
     * 获取分页数据
     *
     * @param queryVO
     * @return
     */
    Pagination getPagination(SpecialThemeQueryVO queryVO);

    /**
     * 获取专题缩略图
     */
    SpecialThemeEO getSpecialThumb(SpecialThemeQueryVO queryVO);

    /**
     * 获取压缩包内缩略图
     */
    List<Map<String, Object>> saveSpecialThumb(String path);
}
