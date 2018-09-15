package cn.lonsun.weibo.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.weibo.entity.WeiboRadioContentEO;

/**
 * @author gu.fei
 * @version 2015-12-8 17:13
 */
public interface IWeiboRadioContentService extends IBaseService<WeiboRadioContentEO> {

    /**
     * 根据类型查询微博
     * @param type
     * @return
     */
    public WeiboRadioContentEO getByType(String type);

    /**
     * 分页查询
     * @param dto
     * @return
     */
    public Pagination getPageEOs(ParamDto dto);

    /**
     * 批量删除
     * @param ids
     */
    public void batchDel(Long[] ids);

    /**
     * 批量发布
     * @param ids
     */
    public void batchPublish(Long[] ids) throws Exception;
}
