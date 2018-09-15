package cn.lonsun.staticcenter.generate.tag.impl.leaderwin;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.leaderwin.internal.dao.ILeaderInfoDao;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LeaderInfoBeanService extends AbstractBeanService {

    @Autowired
    private ILeaderInfoDao leaderInfoDao;


    @Override
    public Object getObject(JSONObject paramObj) {
        String leaderIdStr = paramObj.getString("leaderId");
        Long contentId = null;
        Long leaderId = null;
        if(StringUtils.isEmpty(leaderIdStr)){
            Context context = ContextHolder.getContext();
            contentId = context.getContentId();// 根据文章id查询文章
        }else{
            leaderId = Long.parseLong(leaderIdStr);
        }
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as name,b.columnId as columnId,b.siteId as siteId,"
                + "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
                + "s.leaderInfoId as leaderInfoId,s.leaderTypeId as leaderTypeId,s.positions as positions,s.work as work,s.jobResume as jobResume"
                + " from BaseContentEO b,LeaderInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.isPublish = 1");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(BaseContentEO.TypeCode.leaderInfo.toString());
        if(contentId != null){
            hql.append("  and  b.id = ?");
            values.add(contentId);
        }
        if(leaderId != null){
            hql.append("  and  s.leaderInfoId = ?");
            values.add(leaderId);
        }
        hql.append(" order by b.num desc");
        LeaderInfoVO liVo = (LeaderInfoVO) leaderInfoDao.getBean(hql.toString(), values.toArray(), LeaderInfoVO.class);
        if(liVo != null){
            liVo.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), liVo.getColumnId(), null)+ "?liId=" + liVo.getLeaderInfoId());
        }
        return liVo;
    }
}
