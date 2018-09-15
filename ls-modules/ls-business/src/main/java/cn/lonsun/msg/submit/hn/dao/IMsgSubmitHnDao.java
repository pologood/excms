package cn.lonsun.msg.submit.hn.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.msg.submit.hn.entity.CmsMsgSubmitHnEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface IMsgSubmitHnDao extends IMockDao<CmsMsgSubmitHnEO> {
    /**
     * 加载自己发布的信息
     * @return
     */
    Pagination getPageList(ParamDto dto);

    /**
     * 加载传阅给自己的信息
     * @return
     */
    Pagination getToMePageList(ParamDto dto);

    /**
     * 加载待发布的信息
     * @return
     */
    Pagination getTobePageList(ParamDto dto);

    /**
     * 获取传阅给自己信息数量
     * @return
     */
    Long getToMeCount();

    /**
     * 加载待发布信息数量
     * @return
     */
    Long getToBeCount(ParamDto dto);

    /**
     * 加载发布信息数量
     * @return
     */
    Long getBeCount(ParamDto dto);

    /**
     * 加载发布的信息
     * @return
     */
    Pagination getBePageList(ParamDto dto);
}
