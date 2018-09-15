package cn.lonsun.staticcenter.generate.tag.impl.leaderwin;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.leaderwin.internal.dao.ILeaderInfoDao;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class LeaderWinsBeanService extends AbstractBeanService {

    @Autowired
    private ILeaderInfoDao leaderInfoDao;


    @SuppressWarnings("unchecked")
    @Override
    public Object getObject(JSONObject paramObj) {
        List<Long> ids = new ArrayList<Long>();
        List<Long> typeIds = new ArrayList<Long>();
        List<Long> removeIds = new ArrayList<Long>();
        String id = "";
        String typeId = "";
        String removeId = "";
        try {
            id = paramObj.getString(GenerateConstant.ID);
            typeId = paramObj.getString("typeId");
            removeId = paramObj.getString("removeIds");
        } catch (Exception e) {
        }
        if (!StringUtils.isEmpty(id)) {
            ids.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(id.split(","), Long.class))));
            id = StringUtils.join(ids.toArray(), ",");
        }
        if (!StringUtils.isEmpty(typeId)) {
            typeIds.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(typeId.split(","), Long.class))));
            typeId = StringUtils.join(typeIds.toArray(), ",");
        }
        if (!StringUtils.isEmpty(removeId)) {
            removeIds.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(removeId.split(","), Long.class))));
            removeId = StringUtils.join(removeIds.toArray(), ",");
        }
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as name,b.columnId as columnId,b.siteId as siteId,"
            + "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
            + "s.leaderInfoId as leaderInfoId,s.leaderTypeId as leaderTypeId,s.positions as positions,s.work as work,s.jobResume as jobResume"
            + " from BaseContentEO b,LeaderInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.isPublish = 1");
        if (!StringUtils.isEmpty(id)) {
            hql.append(" and s.leaderInfoId in (" + id + ")");
        }
        if (!StringUtils.isEmpty(typeId)) {
            hql.append(" and s.leaderTypeId in (" + typeId + ")");
        }
        if (!StringUtils.isEmpty(removeId)) {
            hql.append(" and s.leaderInfoId not in (" + removeId + ")");
        }
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(BaseContentEO.TypeCode.leaderInfo.toString());
        hql.append(" order by b.num desc");
        List<LeaderInfoVO> list = (List<LeaderInfoVO>) leaderInfoDao.getBeansByHql(hql.toString(), values.toArray(), LeaderInfoVO.class);
        if (list != null && list.size() > 0) {
            // 处理文章链接
            for (LeaderInfoVO liVo : list) {
                liVo.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), liVo.getColumnId(), null) + "?liId=" + liVo.getLeaderInfoId());
            }
        }
        return list;
    }

}
