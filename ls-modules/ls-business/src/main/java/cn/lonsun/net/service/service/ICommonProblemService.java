package cn.lonsun.net.service.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsCommonProblemEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface ICommonProblemService extends IMockService<CmsCommonProblemEO> {

    /**
     * 分页查询办事常见问题
     * @param dto
     * @return
     */
    public Pagination getPageEntities(ParamDto dto);

    /**
     * 保存信息
     * @param eo
     */
    public void saveEO(CmsCommonProblemEO eo);

    /**
     * 更新信息
     * @param eo
     */
    public void updateEO(CmsCommonProblemEO eo);


    /**
     * 发布信息
     * @param ids
     * @param publish
     * @return
     */
    public Object publish(Long[] ids, Long publish);

    /**
     * 删除
     * @param ids
     */
    public void deleteEO(Long[] ids);
}
