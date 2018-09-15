package cn.lonsun.statistics;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.messageBoard.dao.IMessageBoardDao;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.words.util.ExportExcel;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.system.sitechart.dao.ISiteChartTrendDao;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author gu.fei
 * @version 2018-02-08 14:33
 */
@Controller
@RequestMapping(value = "siteInfo/statis")
public class SiteInfoStatisController extends BaseController {

    @Resource
    private ISiteChartTrendDao siteChartTrendDao;

    @Resource
    private IBaseContentDao baseContentDao;

    @Resource
    private IMessageBoardDao messageBoardDao;

    @Resource
    private ISiteRightsService siteRightsService;
    /**
     * 首页
     * @return
     */
    @RequestMapping(value = "index")
    public String index() {
        return "/statistics/site_info_statis";
    }

    /**
     * 获取统计信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteInfoStatis")
    public Object getSiteInfoStatis(String sortField,String sortOrder,String year) {
        sortField = "newsCount";
        sortOrder = "asc";
        return getObject(getSiteInfo(sortField,sortOrder,year));
    }

    /**
     * 获取统计信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteMgr")
    public Object getSiteMgr(Long siteId) {
        SiteMgrEO mgr = CacheHandler.getEntity(SiteMgrEO.class,siteId);
        return getObject(mgr);
    }

    /**
     * 获取访问统计信息
     * @return
     */
    public Map<Long,MainChartVO> getVisitStatis(String year) {
        Map<Long,MainChartVO> map = new HashMap<Long, MainChartVO>();
        List<Object> values = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder("select t.site_id as siteId,sum(t.pv) as pv,sum(t.uv) as uv from CMS_SITE_CHART_TREND t where 1=1");
        if(!StringUtils.isEmpty(year)) {
            sql.append(" and to_char(t.create_Date,'yyyy')=?");
            values.add(year);
        }
        sql.append(" GROUP BY t.SITE_ID");
        String[] fields = new String[]{"siteId","pv","uv"};
        List list = siteChartTrendDao.getBeansBySql(sql.toString(),values.toArray(),MainChartVO.class,fields);
        if(null != list && !list.isEmpty()) {
            for(Object obj : list) {
                MainChartVO vo = (MainChartVO) obj;
                map.put(vo.getSiteId(),vo);
            }
        }
        return map;
    }

    /**
     * 获取新闻数量
     * @return
     */
    public Map<Long,NewsStatisVO> getNewsCount(String year) {
        Map<Long,NewsStatisVO> map = new HashMap<Long, NewsStatisVO>();
        List<Object> values = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder("select SITE_ID as siteId,count(id) as count from CMS_BASE_CONTENT where type_Code in('articleNews','pictureNews','videoNews') and is_Publish=1");
        if(!StringUtils.isEmpty(year)) {
            sql.append(" and to_char(t.publishDate,'yyyy')=?");
            values.add(year);
        }
        sql.append(" GROUP BY SITE_ID");
        String[] fields = new String[]{"siteId","count"};
        List list = baseContentDao.getBeansBySql(sql.toString(),values.toArray(),NewsStatisVO.class,fields);
        if(null != list && !list.isEmpty()) {
            for(Object obj : list) {
                NewsStatisVO vo = (NewsStatisVO) obj;
                map.put(vo.getSiteId(),vo);
            }
        }
        return map;
    }

    /**
     * 获取留言统计信息
     * @param organId
     * @return
     */
    public MbStatisVO getMessageBoardCount(Long organId) {
        StringBuilder sql = new StringBuilder("");
        sql.append("SELECT");
        sql.append(" COUNT (*) as count,");
        sql.append(" sum(case when r.MESSAGE_BOARD_ID is not null  then 1 else 0 end) as fcount");
        sql.append(" FROM");
        sql.append(" CMS_BASE_CONTENT c");
        sql.append(" LEFT JOIN	CMS_MESSAGE_BOARD M on m.BASE_CONTENT_ID=c.id");
        sql.append(" LEFT JOIN( SELECT DISTINCT MESSAGE_BOARD_ID,RECORD_STATUS,operation_status,RECEIVE_ORGAN_ID FROM CMS_MESSAGE_BOARD_FORWARD) f");
        sql.append(" ON M . ID = f.message_board_id");
        sql.append(" LEFT JOIN( SELECT DISTINCT MESSAGE_BOARD_ID FROM CMS_MESSAGE_BOARD_reply) r");
        sql.append(" ON M . ID = r.message_board_id");
        sql.append(" where M .record_status = 'Normal'");
        sql.append(" AND f.record_status = 'Normal'");
        sql.append(" AND f.operation_status = 'Normal'");
        sql.append(" and c.record_status = 'Normal'");
        sql.append(" AND c.TYPE_CODE = 'messageBoard'");
        sql.append(" and f.RECEIVE_ORGAN_ID = ?");
        return (MbStatisVO)messageBoardDao.getBeanBySql(sql.toString(),new Object[]{organId},MbStatisVO.class,new String[]{"count","fcount"});
    }

    /**
     * 导出统计信息
     * @param response
     */
    @RequestMapping("/export")
    public void export(HttpServletResponse response, String sortField, String sortOrder, String year) {
        String[] titles = new String[]{"网站名称","独立用户访问量","网站总访问量","政务动态信息更新量",
                "信息公开目录更新量","微信信息发布量","网名留言数量","办结留言数量","政务服务注册用户数","全称在线办理办件量"};
        List list = getSiteInfo(sortField,sortOrder,year);
        try {
            String name = "站点统计信息(" + DateFormatUtils.format(new Date(),"yyyy-MM-dd") + ")";
            String suffic = "xls";
            List<String> excFields = new ArrayList<String>();
            excFields.add("siteId");
            ExportExcel.exportExcel(name,suffic,titles,list,response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取站群统计信息
     * @return
     */
    public List<SiteInfoStatisVO> getSiteInfo(String sortField, final String sortOrder, String year) {
        List<SiteMgrEO> sites = CacheHandler.getList(SiteMgrEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Site.toString());
        List<SiteInfoStatisVO> infos = new ArrayList<SiteInfoStatisVO>();
        Map<Long,MainChartVO> chartMap = getVisitStatis(year);
        Map<Long,NewsStatisVO> newsMap = getNewsCount(year);
        if(null != sites && !sites.isEmpty()) {
            for(SiteMgrEO eo : sites) {
                SiteInfoStatisVO vo = new SiteInfoStatisVO();
                vo.setSiteName(eo.getName());
//                vo.setIcpRecord(eo.getIcpRecord());
//                vo.setPoliceRecord(eo.getPoliceRecord());
//                vo.setPublicCount(eo.getPublicUpdateCount());
//                vo.setWxCount(eo.getWechatPublishCount());
//                vo.setRegisterUserCount(eo.getRegisterUserCount());
//                vo.setOnlineDealCount(eo.getOnlineDealCount());
                MainChartVO chart = chartMap.get(eo.getIndicatorId());
                if(null != chart) {
                    vo.setPv(chart.getPv());
                    vo.setUv(chart.getUv());
                }
                NewsStatisVO news = newsMap.get(eo.getIndicatorId());
                if(null != news) {
                    vo.setNewsCount(news.getCount());
                }
                if(null != eo.getUnitIds()) {
                    MbStatisVO mb = getMessageBoardCount(Long.valueOf(eo.getUnitIds()));
                    if(null != mb) {
                        vo.setMessageCount(mb.getCount());
                        vo.setReplyedCount(mb.getFcount());
                    }
                }
                infos.add(vo);
            }
        }
        if(!StringUtils.isEmpty(sortField)) {
            try {
                Field field = SiteInfoStatisVO.class.getDeclaredField(sortField);
                field.setAccessible(true);
                final Field finalField = field;
                Collections.sort(infos, new Comparator<Object>() { // 菜单排序
                    public int compare(Object s1, Object s2) {
                        SiteInfoStatisVO eo1 = (SiteInfoStatisVO) s1;
                        SiteInfoStatisVO eo2 = (SiteInfoStatisVO) s2;
                        Long a = null,b = null;
                        try {
                            a = Long.valueOf(null != finalField.get(eo1)?finalField.get(eo1).toString():"0");
                            b = Long.valueOf(null != finalField.get(eo2)?finalField.get(eo2).toString():"0");
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        if(null != sortOrder && sortOrder.equalsIgnoreCase("asc")) {
                            return a.compareTo(b);
                        } else {
                            return b.compareTo(a);
                        }
                    }
                });
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return infos;
    }
}
