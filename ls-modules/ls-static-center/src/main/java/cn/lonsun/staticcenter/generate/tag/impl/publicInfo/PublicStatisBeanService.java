package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.publicInfo.internal.dao.IPublicApplyDao;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.util.DateUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

/**
 * @author gu.fei
 * @version 2017-04-26 14:16
 */
@Service
public class PublicStatisBeanService extends AbstractBeanService {

    @Autowired
    private IBaseContentDao baseContentDao;
    @Autowired
    private IPublicApplyDao publicApplyDao;

    private static final String SITE_ID = "siteId";

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();
        if(!AppUtil.isEmpty(paramObj.get(SITE_ID))) {
            siteId = paramObj.getLong(SITE_ID);
        }
        //信息公开
        StringBuilder pc = new StringBuilder("from BaseContentEO where recordStatus = ?");
        pc.append(" and isPublish = 1");
        pc.append(" and typeCode = ?");
        pc.append(" and publishDate >= ?");
        pc.append(" and publishDate <= ?");
        pc.append(" and siteId = ");
        pc.append(siteId);

        //本日发布
        paramObj.put("dayPublic",baseContentDao.getCount(pc.toString(),new Object[]{AMockEntity.RecordStatus.Normal.toString(),BaseContentEO.TypeCode.public_content.toString(),DateUtil.getToday(),new Date()}));
        //本月发布
        paramObj.put("monthPublic",baseContentDao.getCount(pc.toString(),new Object[]{AMockEntity.RecordStatus.Normal.toString(),BaseContentEO.TypeCode.public_content.toString(),DateUtil.getMonth(),new Date()}));

        //依申请公开-受理
        StringBuilder accept = new StringBuilder("from PublicApplyEO where recordStatus = ?");
        accept.append(" and applyDate >= ?");
        accept.append(" and applyDate <= ?");
        accept.append(" and siteId = ");
        accept.append(siteId);

        //本月受理
        paramObj.put("monthApply",publicApplyDao.getCount(accept.toString(),new Object[]{AMockEntity.RecordStatus.Normal.toString(),DateUtil.getMonth(),new Date()}));
        //本年受理
        paramObj.put("yearApply",publicApplyDao.getCount(accept.toString(),new Object[]{AMockEntity.RecordStatus.Normal.toString(),DateUtil.getYear(),new Date()}));

        //依申请公开-办结
        StringBuilder reply = new StringBuilder("from PublicApplyEO where recordStatus = ?");
        reply.append(" and replyDate >= ?");
        reply.append(" and replyDate <= ?");
        reply.append(" and replyStatus is not null");
        reply.append(" and siteId = ");
        reply.append(siteId);
        //本月办结
        paramObj.put("monthReply",publicApplyDao.getCount(reply.toString(),new Object[]{AMockEntity.RecordStatus.Normal.toString(),DateUtil.getMonth(),new Date()}));
        //本年办结
        paramObj.put("yearReply",publicApplyDao.getCount(reply.toString(),new Object[]{AMockEntity.RecordStatus.Normal.toString(),DateUtil.getYear(),new Date()}));

        return Collections.emptyList();
    }
}
