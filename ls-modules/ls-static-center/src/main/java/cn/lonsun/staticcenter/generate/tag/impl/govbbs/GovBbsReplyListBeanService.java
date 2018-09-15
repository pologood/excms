package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.govbbs.internal.dao.IBbsReplyDao;
import cn.lonsun.govbbs.internal.vo.BbsReplyListVO;
import cn.lonsun.govbbs.util.BbsStaticCenterUtil;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangchao on 2016/12/27.
 */
@Component
public class GovBbsReplyListBeanService extends AbstractBeanService {

    @Autowired
    private IBbsReplyDao bbsReplyDao;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        //站点id
        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        // 需要显示条数.
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        num =(num == null?8:num);
        //版块ids
        List<Long> plateIds = null;
        String plateIdStrs = paramObj.getString("plateIds");
        if (!StringUtils.isEmpty(plateIdStrs)) {
            plateIds = new ArrayList<Long>();
            plateIds.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(plateIdStrs.split(","), Long.class))));
        }
        //默认排序
        String sortField = paramObj.getString("sortField");
        if(StringUtils.isEmpty(sortField)){
            sortField = BbsStaticCenterUtil.defaultDateOrder;
        }
        //是否查询需要办理的
        Boolean isHandle = paramObj.getBoolean("isHandle");
        if(isHandle == null){isHandle = false;}

        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("select" +
                " b.replyId as replyId,b.postTile as postTile,b.postId as postId,b.plateId as plateId," +
                "b.memberId as memberId,b.memberName as memberName,b.handleUnitId as handleUnitId,b.handleUnitName as handleUnitName," +
                "b.handleTime as handleTime,b.createDate as createDate" +
                " from BbsReplyEO b where b.siteId = ? and b.recordStatus = 'Normal' and b.isPublish = 1");
        values.add(siteId);
        if(plateIds != null && plateIds.size()> 0){
            hql.append(" and (1=0");
            for(Long plateId:plateIds){
                hql.append(" or b.plateId = ?");
                values.add(plateId);
            }
            hql.append(")");
        }
        if(isHandle){
            hql.append(" and b.isHandle = 1");
        }
        hql.append(" order by ").append(sortField);
        List<BbsReplyListVO> replys = (List<BbsReplyListVO>)bbsReplyDao.getBeansByHql(hql.toString(),values.toArray(),BbsReplyListVO.class,num);
        return replys;
    }
}
