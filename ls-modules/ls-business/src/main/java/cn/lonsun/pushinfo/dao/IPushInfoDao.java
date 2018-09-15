package cn.lonsun.pushinfo.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.pushinfo.entity.PushInfoEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

/**
 * @author gu.fei
 * @version 2016-1-21 14:27
 */
public interface IPushInfoDao extends IMockDao<PushInfoEO> {

    /**
     * 分页查询数据
     * @param dto
     * @return
     */
    public Pagination getPageEOs(ParamDto dto);

    /**
     * 根据路径查询
     * @param path
     * @return
     */
    public PushInfoEO getByPath(String path);

    /**
     * 删除数据
     * @param ids
     */
    public void deleteEOs(Long[] ids);
}
