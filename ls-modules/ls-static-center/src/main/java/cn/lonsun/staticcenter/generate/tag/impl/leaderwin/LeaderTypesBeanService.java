package cn.lonsun.staticcenter.generate.tag.impl.leaderwin;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.leaderwin.internal.dao.ILeaderInfoDao;
import cn.lonsun.content.leaderwin.internal.dao.ILeaderTypeDao;
import cn.lonsun.content.leaderwin.internal.entity.LeaderTypeEO;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.content.leaderwin.vo.LeaderWebVO;
import cn.lonsun.content.survey.util.MapUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.solr.SolrQueryHolder;
import cn.lonsun.solr.entity.IndexKeyWordsEO;
import cn.lonsun.solr.service.IIndexKeyWordsService;
import cn.lonsun.solr.vo.QueryResultVO;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.solr.vo.SolrPageQueryVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.tag.impl.search.util.SearchUtil;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class LeaderTypesBeanService extends AbstractBeanService {

    @Autowired
    private ILeaderInfoDao leaderInfoDao;

    @Autowired
    private ILeaderTypeDao leaderTypeDao;

    @Autowired
    private IIndexKeyWordsService indexKeyWordsService;

    @Autowired
    private IIndicatorService indicatorService;

    @Autowired
    private IWorkGuideService workGuideService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        Long siteId = context.getSiteId();
        // 此写法是为了使得在页面这样调用也能解析
        if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        String liIdStr = context.getParamMap().get("liId");
        Long liId = StringUtils.isEmpty(liIdStr) ? null : Long.parseLong(liIdStr);
        List<LeaderTypeEO> types = leaderTypeDao.getList(siteId, columnId);
        Map<String, Object> mapParams = new HashMap<String, Object>();
        List<LeaderWebVO> leaders = new ArrayList<LeaderWebVO>();
        LeaderInfoVO leaderInfo = null;
        if (types != null && types.size() > 0) {
            int i = 0;
            Map<Long, List<LeaderInfoVO>> map = MapUtil.getLeadersMap(leaderInfoDao.getList(siteId, columnId));
            Long typeId = null;
            for (LeaderTypeEO type : types) {
                LeaderWebVO vo = new LeaderWebVO();
                vo.setLeaderTypeId(type.getLeaderTypeId());
                vo.setColumnId(columnId);
                vo.setSiteId(type.getSiteId());
                vo.setTitle(type.getTitle());
                List<LeaderInfoVO> infos = (map == null?null:map.get(type.getLeaderTypeId()));
                if (infos != null && infos.size() > 0) {
                    String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), columnId, null);
                    for (LeaderInfoVO info : infos) {
                        if (i == 0 && liId == null) {
                            leaderInfo = info;
                            typeId = info.getLeaderTypeId();
                            info.setIsClick(1);
                        }
                        if (liId != null && info.getLeaderInfoId().equals(liId)) {
                            leaderInfo = info;
                            typeId = info.getLeaderTypeId();
                            info.setIsClick(1);
                        }
                        i++;
                        info.setLinkUrl(path + "?liId=" + info.getLeaderInfoId());
                    }
                }
                //设置选中实践
                if (typeId != null && typeId.equals(type.getLeaderTypeId())) {
                    vo.setIsOpen(1);
                }
                vo.setLeaderInfos(infos);
                leaders.add(vo);
            }
            if (typeId == null) {
                leaders.get(0).setIsOpen(1);
            }
        }
        mapParams.put("leaders", leaders);
        mapParams.put("lcount", leaders.size());
        mapParams.put("leaderInfo", leaderInfo);
        return mapParams;
    }


    /**
     * 格式化时间
     *
     * @param date
     * @return
     */
    private String dataFormat(Date date) {
        if (null == date) {
            return "";
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }
}
