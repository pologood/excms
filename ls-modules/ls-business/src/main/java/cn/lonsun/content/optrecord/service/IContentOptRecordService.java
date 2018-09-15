package cn.lonsun.content.optrecord.service;

import cn.lonsun.content.optrecord.entity.ContentOptRecordEO;
import cn.lonsun.content.optrecord.vo.OptRecordQueryVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

/**
 * @author gu.fei
 * @version 2018-01-18 9:42
 */
public interface IContentOptRecordService extends IMockService<ContentOptRecordEO> {

    /**
     * 分页查询
     * @param vo
     * @return
     */
    Pagination getPage(OptRecordQueryVO vo);
}
