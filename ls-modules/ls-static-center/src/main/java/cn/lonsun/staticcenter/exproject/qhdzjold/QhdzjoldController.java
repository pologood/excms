package cn.lonsun.staticcenter.exproject.qhdzjold;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.site.site.internal.dao.IColumnConfigDao;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1960274114 on 2016-10-18.
 * 青海地震局
 */
@Controller
@RequestMapping("/qhdzjold/db")
public class QhdzjoldController extends BaseController {
    private Long createUserId =1L;
    //政民互动导入页面
    private final String PAGE_News = "/qhdzjold/db/impListPage";
    //政民互动导入
    private final String ACTION_News = "/qhdzjold/db/newsImp";

    private JdbcUtils loadRemoteJdbc() {
        JdbcUtils jdbcUtils = JdbcUtils.getInstance();
        String db_ip = "61.191.61.136";
        int db_port = 31433;
        String db_name = "qhdzjold";
        String db_user = "qhdzjold";
        String db_pwd = "qhdzjold";

        jdbcUtils.setURLSTR("jdbc:jtds:sqlserver://".concat(db_ip).concat(":" + db_port)
                .concat(";DatabaseName=".concat(db_name).concat(";useLOBs=false;")));
        jdbcUtils.setUSERNAME(db_user);
        jdbcUtils.setUSERPASSWORD(db_pwd);
        return jdbcUtils;
    }

    @RequestMapping("impListPage")
    public ModelAndView impListPage(Long siteId){
        if(null == siteId) return null;

        ModelAndView mv =  new ModelAndView("/dbconvert/qhdzjold_imp");
        String type_title = "综合信息";
        String imp_action = ACTION_News;
        JdbcUtils jdbcUtils = loadRemoteJdbc();
        SyncArticleNews articleNews = new SyncArticleNews(jdbcUtils, siteId);
        List<OldSiteColumnPairVO> fromList = articleNews.getOldColumnList();
        //ex8站点栏目
        IColumnConfigService columnConfigService = SpringContextHolder.getBean(IColumnConfigService.class);
        List<ColumnMgrEO> toList = new ArrayList<ColumnMgrEO>();

        List<ColumnMgrEO> rootList = columnConfigService.getColumnTreeBySite(siteId);
        if(null !=rootList){
            IColumnConfigDao configDao = SpringContextHolder.getBean(IColumnConfigDao.class);
            for(ColumnMgrEO eo1:rootList){//1级
                toList.add(eo1);
                List<ColumnMgrEO> list1 = configDao.getColumnByParentId(eo1.getIndicatorId(), true, siteId);
                if(null !=list1){
                    for(ColumnMgrEO eo2:list1){//2级
                        eo2.setName(" - " + eo2.getName());
                        toList.add(eo2);
                        List<ColumnMgrEO> list2 = configDao.getColumnByParentId(eo2.getIndicatorId(), true, siteId);
                        if(null !=list2){
                            for(ColumnMgrEO eo3:list2) {//2级
                                eo3.setName(" -- " + eo3.getName());
                                toList.add(eo3);
                                List<ColumnMgrEO> list3 = configDao.getColumnByParentId(eo3.getIndicatorId(), true, siteId);
                                if(null !=list3){
                                    for(ColumnMgrEO eo4:list3) {//3级
                                        eo4.setName(" ---  " + eo4.getName());
                                        toList.add(eo4);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        mv.addObject("siteId",siteId);
        mv.addObject("fromList", fromList);
        mv.addObject("toList", toList);
        mv.addObject("type_title",type_title);
        mv.addObject("imp_action",imp_action);
        return mv;
    }

    @RequestMapping("newsImp")
    @ResponseBody
    public String newsImp(Long siteId,String channelId,String fromCode,String indicatorIdAndCode) {
        if(StringUtils.isEmpty(fromCode) || StringUtils.isEmpty(indicatorIdAndCode)){
            return "未知操作!<a href=\"".concat(PAGE_News  + "?siteId=" + siteId).concat("\">返回</a>");
        }
        JdbcUtils jdbcUtils = loadRemoteJdbc();
        Long start = System.currentTimeMillis();
        String[] indicatorIdAndCodeArgs = indicatorIdAndCode.split("~");
        String columnTypeCode = indicatorIdAndCodeArgs[0];
        Long indicatorId = AppUtil.getLong(indicatorIdAndCodeArgs[1]);
        SyncArticleNews articleNews = new SyncArticleNews(jdbcUtils, siteId,createUserId,indicatorId);
        articleNews.imp(fromCode,columnTypeCode,channelId);

        return "导入成功（耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒）！<a href=\"".concat(PAGE_News  + "?siteId=" + siteId).concat("\">返回</a>");
    }

    /**
     * 获取二级栏目
     * @param fromCode
     * @return
     */
    @RequestMapping("getClassList")
    @ResponseBody
    public Object getClassList(String fromCode){
        List<OldSiteColumnPairVO> ret = new ArrayList<OldSiteColumnPairVO>();
        if(StringUtils.isEmpty(fromCode)){
            return ajaxErr("必要参数为空");
        }
        JdbcUtils jdbcUtils = loadRemoteJdbc();
        SyncArticleNews articleNews = new SyncArticleNews(jdbcUtils, null);
        return ajaxOk(articleNews.getOldClassList(fromCode));
    }
}
