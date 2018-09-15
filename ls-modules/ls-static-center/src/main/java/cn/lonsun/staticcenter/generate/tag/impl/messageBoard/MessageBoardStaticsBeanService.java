package cn.lonsun.staticcenter.generate.tag.impl.messageBoard;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.messageBoard.dao.IMessageBoardDao;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.statistics.MessageBoardListVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2016-11-25.
 */
@Component
public class MessageBoardStaticsBeanService extends AbstractBeanService {
    @Autowired
    private IMessageBoardService messageBoardService;
    @SuppressWarnings("unchecked")
    @Override
    public Object getObject(JSONObject paramObj) {
        Integer size = paramObj.getInteger("pageSize");
        StatisticsQueryVO queryVO =new StatisticsQueryVO();
        queryVO.setSiteId(ContextHolder.getContext().getSiteId());
        List<MessageBoardListVO> list = messageBoardService.getMessageBoardUnitList(queryVO);
        if(size!=null&&size!=0){
           return list.subList(0,size);
        }
       return list;
    }
}
