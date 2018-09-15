package cn.lonsun.statistics;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.onlinePetition.internal.service.IOnlinePetitionService;
import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.CSVUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.msg.submit.service.IMsgSubmitService;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicApplyService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.DateUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

//import cn.lonsun.util.ExcelUtil;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-2-1<br/>
 */
@Controller
@RequestMapping("/statistics")
public class StatisticsController extends BaseController {
    @Autowired
    private IBaseContentService baseService;

    @Autowired
    private IGuestBookService guestService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IOnlinePetitionService petitionService;

    @Autowired
    private IMsgSubmitService submitService;

    @Autowired
    private IPublicContentService publicService;

    @Autowired
    private IPublicApplyService applyService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private ISiteRightsService siteRightsService;



    @RequestMapping("reportOut")
    public String reportOut(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("isOrgan",request.getParameter("isOrgan"));
        model.addAttribute("isUser",request.getParameter("isUser"));
        model.addAttribute("isColumn",request.getParameter("isColumn"));
        return "/statistics/out_index";
    }


    @RequestMapping("wordIndex")
    public String wordIndex(HttpServletRequest request, HttpServletResponse response, Model model) {

        model.addAttribute("isOrgan",request.getParameter("isOrgan"));
        model.addAttribute("isUser",request.getParameter("isUser"));
        model.addAttribute("isColumn",request.getParameter("isColumn"));
        return "/statistics/word_index";
    }

    @RequestMapping("publicIndex")
    public String publicIndex(HttpServletRequest request, HttpServletResponse response, Model model) {

        return "/statistics/public_index";
    }

    @RequestMapping("officeIndex")
    public String officeIndex(HttpServletRequest request, HttpServletResponse response, Model model) {

        return "/statistics/office_index";
    }

    @RequestMapping("guestIndex")
    public String guestIndex(HttpServletRequest request, HttpServletResponse response, Model model) {

        return "/statistics/guest_index";
    }

    @RequestMapping("videoIndex")
    public String videoIndex(HttpServletRequest request, HttpServletResponse response, Model model) {

        return "/statistics/video_index";
    }

    @RequestMapping("collectIndex")
    public String collectIndex(HttpServletRequest request, HttpServletResponse response, Model model) {

        return "/statistics/collect_index";
    }

    @RequestMapping("petitionIndex")
    public String petitionIndex(HttpServletRequest request, HttpServletResponse response, Model model) {

        return "/statistics/petition_index";
    }

    @RequestMapping("submitIndex")
    public String submitIndex(HttpServletRequest request, HttpServletResponse response, Model model) {

        return "/statistics/submit_index";
    }

    @RequestMapping("messageBoardIndex")
    public String messageBoardIndex(HttpServletRequest request, HttpServletResponse response, Model model) {

        return "/statistics/message_board_index";
    }

    @RequestMapping("contentIndex")
    public String contentIndex(HttpServletRequest request, HttpServletResponse response, Model model) {

        return "/statistics/content_index";
    }

    /**
     * 获取单位发文Top10
     *
     * @param chartsTime
     * @param typeCode
     * @return
     */
    @RequestMapping("getChartsList")
    @ResponseBody
    public Object getChartsList(String chartsTime, String typeCode, String type,Integer orderType,String isOrgan,String isUser,String isColumn) {
        List<ContentChartVO> list = new ArrayList<ContentChartVO>();
        ContentChartQueryVO queryVO = getQueryVO(chartsTime, typeCode, type);
        queryVO.setIsOrgan(isOrgan);//是否按照部门查询
        queryVO.setIsUser(isUser);//是否按照人员查询
        queryVO.setIsColumn(isColumn);//是否按照栏目查询
        if ("word".equals(type)) {
            if (BaseContentEO.TypeCode.guestBook.toString().equals(typeCode)) {//留言
                list = guestService.getStatisticsList(queryVO);
            } else if (BaseContentEO.TypeCode.messageBoard.toString().equals(typeCode)) {//多单位留言
                list = messageBoardService.getStatisticsList(queryVO);
            } else if (BaseContentEO.TypeCode.onlinePetition.toString().equals(typeCode)) {//网上信访
                list = petitionService.getStatisticsList(queryVO);
            } else if (BaseContentEO.TypeCode.public_content.toString().equals(typeCode)) {//信息公开
                list = baseService.getContentChart(queryVO);
                List<ContentChartVO> list1 = publicService.getChartList(queryVO, PublicContentEO.Type.PUBLIC_GUIDE.toString());
                List<ContentChartVO> list2 = publicService.getChartList(queryVO, PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString());
                List<ContentChartVO> list3 = publicService.getChartList(queryVO, PublicContentEO.Type.PUBLIC_INSTITUTION.toString());
                List<ContentChartVO> list4 = publicService.getChartList(queryVO, PublicContentEO.Type.DRIVING_PUBLIC.toString());
                List<ContentChartVO> list5 = publicService.getChartList(queryVO, null);
                List<List<ContentChartVO>> newList = new ArrayList<List<ContentChartVO>>();
                newList.add(list);
                newList.add(list1);
                newList.add(list2);
                newList.add(list3);
                newList.add(list4);
                newList.add(list5);
                return getObject(newList);
            } else {
                list = baseService.getContentChart(queryVO);

            }
        }
        if ("satisfactory".equals(type)) {
            list = messageBoardService.getSatisfactoryList(queryVO);
        }
        if ("submit".equals(type)) {//信息报送
            queryVO.setOrderType(orderType);
            list = submitService.getContentChart(queryVO);
        }
        if ("submitEmp".equals(type)) {//信息报送被采编
            queryVO.setOrderType(orderType);
            list = submitService.getEmpContentChart(queryVO);
        }
        return getObject(list);
    }

    /**
     * 获取查询参数
     * @param chartsTime
     * @param typeCode
     * @param type
     * @return
     */
    private ContentChartQueryVO getQueryVO(String chartsTime, String typeCode, String type) {
        if (StringUtils.isEmpty(type)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误");
        }
        Long siteId = LoginPersonUtil.getSiteId();
        if (StringUtils.isEmpty(chartsTime)) {
            throw new BaseRunTimeException();
        }
        ContentChartQueryVO queryVO = new ContentChartQueryVO();
        queryVO.setSiteId(siteId);
        queryVO.setTypeCode(typeCode);
        if ("today".equals(chartsTime)) {//今日
            queryVO.setStartStr(DateUtil.getStrToday());
            queryVO.setEndStr(DateUtil.getStrNow());
            queryVO.setStartDate(DateUtil.getToday());
            queryVO.setEndDate(new Date());

        } else if ("yesterday".equals(chartsTime)) {//昨天
            queryVO.setStartStr(DateUtil.getStrYesterday());
            queryVO.setEndStr(DateUtil.getStrToday());
            queryVO.setStartDate(DateUtil.getYesterday());
            queryVO.setEndDate(DateUtil.getToday());
        } else if ("week".equals(chartsTime)) {//本周
            queryVO.setStartStr(DateUtil.getStrWeek());
            queryVO.setEndStr(DateUtil.getStrNow());
            queryVO.setStartDate(DateUtil.getWeek());
            queryVO.setEndDate(new Date());
        } else if ("month".equals(chartsTime)) {//本月
            queryVO.setStartStr(DateUtil.getStrMonth());
            queryVO.setEndStr(DateUtil.getStrNow());
            queryVO.setStartDate(DateUtil.getMonth());
            queryVO.setEndDate(new Date());
        }
        return queryVO;
    }

    /**
     * 获取留言和信访类别比例
     *
     * @param chartsTime
     * @param typeCode
     * @return
     */
    @RequestMapping("getTypeList")
    @ResponseBody
    public Object getTypeList(String chartsTime, String typeCode) {
        if (StringUtils.isEmpty(typeCode)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误");
        }
        List<ContentChartVO> list = new ArrayList<ContentChartVO>();
        Long siteId = LoginPersonUtil.getSiteId();
        if (StringUtils.isEmpty(chartsTime)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误");
        }

        ContentChartQueryVO queryVO = new ContentChartQueryVO();
        queryVO.setSiteId(siteId);
        if ("today".equals(chartsTime)) {//今日
            queryVO.setSiteId(siteId);
            queryVO.setTypeCode(typeCode);
            queryVO.setStartStr(DateUtil.getStrToday());
            queryVO.setEndStr(DateUtil.getStrNow());

        } else if ("yesterday".equals(chartsTime)) {//昨天
            queryVO.setSiteId(siteId);
            queryVO.setTypeCode(typeCode);
            queryVO.setStartStr(DateUtil.getStrYesterday());
            queryVO.setEndStr(DateUtil.getStrToday());
        } else if ("week".equals(chartsTime)) {//本周
            queryVO.setSiteId(siteId);
            queryVO.setTypeCode(typeCode);
            queryVO.setStartStr(DateUtil.getStrWeek());
            queryVO.setEndStr(DateUtil.getStrNow());
        } else if ("month".equals(chartsTime)) {//本月
            queryVO.setSiteId(siteId);
            queryVO.setTypeCode(typeCode);
            queryVO.setStartStr(DateUtil.getStrMonth());
            queryVO.setEndStr(DateUtil.getStrNow());
        } else if ("history".equals(chartsTime)) {//历史
            queryVO.setSiteId(siteId);
            queryVO.setTypeCode(typeCode);
        }

        if (BaseContentEO.TypeCode.guestBook.toString().equals(typeCode)) {//留言
            list = guestService.getTypeList(queryVO);
        } else if (BaseContentEO.TypeCode.onlinePetition.toString().equals(typeCode)) {//网上信访
            list = petitionService.getTypeList(queryVO);
        } else  if (BaseContentEO.TypeCode.messageBoard.toString().equals(typeCode)){
            list = messageBoardService.getTypeList(queryVO);
        }
        return getObject(list);
    }

    /**
     * 获取全站 今日、昨日、本周、本月、历史发文数量
     *
     * @return
     */
    @RequestMapping("getCount")
    @ResponseBody
    public Object getCount(String typeCode) {
        if (StringUtils.isEmpty(typeCode)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误");
        }
        List<ChartVO> list = new ArrayList<ChartVO>();
        Long siteId = LoginPersonUtil.getSiteId();
        // 获取站点今日发文数量
        ContentChartQueryVO queryVO1 = new ContentChartQueryVO();
        queryVO1.setSiteId(siteId);
        queryVO1.setTypeCode(typeCode);
        queryVO1.setStartDate(DateUtil.getToday());
        queryVO1.setEndDate(new Date());
        ChartVO chartVO1 = new ChartVO();
        chartVO1.setCount(baseService.getCountChart(queryVO1));
        chartVO1.setChartsTime("today");
        list.add(chartVO1);

        // 获取昨日发文数量
        ContentChartQueryVO queryVO2 = new ContentChartQueryVO();
        queryVO2.setSiteId(siteId);
        queryVO2.setTypeCode(typeCode);
        queryVO2.setStartDate(DateUtil.getYesterday());
        queryVO2.setEndDate(DateUtil.getToday());
        ChartVO chartVO2 = new ChartVO();
        chartVO2.setCount(baseService.getCountChart(queryVO2));
        chartVO2.setChartsTime("yesterday");
        list.add(chartVO2);

        // 获取本周发文数量
        ContentChartQueryVO queryVO3 = new ContentChartQueryVO();
        queryVO3.setSiteId(siteId);
        queryVO3.setTypeCode(typeCode);
        queryVO3.setStartDate(DateUtil.getWeek());
        queryVO3.setEndDate(new Date());
        ChartVO chartVO3 = new ChartVO();
        chartVO3.setCount(baseService.getCountChart(queryVO3));
        chartVO3.setChartsTime("week");
        list.add(chartVO3);

        // 获取本月发文数量
        ContentChartQueryVO queryVO4 = new ContentChartQueryVO();
        queryVO4.setSiteId(siteId);
        queryVO4.setTypeCode(typeCode);
        queryVO4.setStartDate(DateUtil.getMonth());
        queryVO4.setEndDate(new Date());
        ChartVO chartVO4 = new ChartVO();
        chartVO4.setCount(baseService.getCountChart(queryVO4));
        chartVO4.setChartsTime("month");
        list.add(chartVO4);

        // 获取历史发文数量
        ContentChartQueryVO queryVO5 = new ContentChartQueryVO();
        queryVO5.setSiteId(siteId);
        queryVO5.setTypeCode(typeCode);
        ChartVO chartVO5 = new ChartVO();
        chartVO5.setCount(baseService.getCountChart(queryVO5));
        chartVO5.setChartsTime("history");
        list.add(chartVO5);
        return getObject(list);
    }

    /**
     * 获取全站 今日、昨日、本周、本月、历史信息报送数量
     *
     * @return
     */
    @RequestMapping("getSubmitCount")
    @ResponseBody
    public Object getSubmitCount() {

        List<ChartVO> list = new ArrayList<ChartVO>();
        Long siteId = LoginPersonUtil.getSiteId();
        // 获取站点今日发文数量
        ContentChartQueryVO queryVO1 = new ContentChartQueryVO();
        queryVO1.setSiteId(siteId);
        queryVO1.setStartStr(DateUtil.getStrToday());
        queryVO1.setEndStr(DateUtil.getStrNow());
        ChartVO chartVO1 = new ChartVO();
        chartVO1.setCount(submitService.getCountChart(queryVO1));
        chartVO1.setChartsTime("today");
        list.add(chartVO1);

        // 获取昨日发文数量
        ContentChartQueryVO queryVO2 = new ContentChartQueryVO();
        queryVO2.setSiteId(siteId);
        queryVO2.setStartStr(DateUtil.getStrYesterday());
        queryVO2.setEndStr(DateUtil.getStrToday());
        ChartVO chartVO2 = new ChartVO();
        chartVO2.setCount(submitService.getCountChart(queryVO2));
        chartVO2.setChartsTime("yesterday");
        list.add(chartVO2);

        // 获取本周发文数量
        ContentChartQueryVO queryVO3 = new ContentChartQueryVO();
        queryVO3.setSiteId(siteId);
        queryVO3.setStartStr(DateUtil.getStrWeek());
        queryVO3.setEndStr(DateUtil.getStrNow());
        ChartVO chartVO3 = new ChartVO();
        chartVO3.setCount(submitService.getCountChart(queryVO3));
        chartVO3.setChartsTime("week");
        list.add(chartVO3);

        // 获取本月发文数量
        ContentChartQueryVO queryVO4 = new ContentChartQueryVO();
        queryVO4.setSiteId(siteId);
        queryVO4.setStartStr(DateUtil.getStrMonth());
        queryVO4.setEndStr(DateUtil.getStrNow());
        ChartVO chartVO4 = new ChartVO();
        chartVO4.setCount(submitService.getCountChart(queryVO4));
        chartVO4.setChartsTime("month");
        list.add(chartVO4);

        // 获取历史发文数量
        ContentChartQueryVO queryVO5 = new ContentChartQueryVO();
        queryVO5.setSiteId(siteId);
        ChartVO chartVO5 = new ChartVO();
        chartVO5.setCount(submitService.getCountChart(queryVO5));
        chartVO5.setChartsTime("history");
        list.add(chartVO5);
        return getObject(list);
    }

    /**
     * 获取文字新闻的详细分页列表
     *
     * @param vo
     * @return
     */
    @RequestMapping("getWordPage")
    @ResponseBody
    public Object getWordPage(StatisticsQueryVO vo) {
        Pagination page = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        page = baseService.getWordPage(vo);
        return page;
    }

    /**
     * 获取文字新闻的详细分页列表(栏目分组)
     *
     * @param vo
     * @return
     */
    @RequestMapping("getWordPageByColumn")
    @ResponseBody
    public Object getWordPageByColumn(StatisticsQueryVO vo) {
        Pagination page = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        page = baseService.getWordPageByColumn(vo);
        return page;
    }

    /**
     * 信息公开按月、年、季度统计
     *
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping("getPublicForOrgan")
    public Object getPublicForOrgan(PublicContentQueryVO queryVO) {
        return publicService.getPublicTjListByDate(queryVO);
    }

    /**
     * 获取信息公开的详细分页列表
     *
     * @param vo
     * @return
     */
    @RequestMapping("getPublicPage")
    @ResponseBody
    public Object getPublicPage(StatisticsQueryVO vo) {
        Pagination page = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        page = publicService.getPublicPage(vo);
        return page;
    }

    /**
     * 获取留言和信访的详细分页列表
     *
     * @param vo
     * @return
     */
    @RequestMapping("getGuestPage")
    @ResponseBody
    public Object getGuestPage(StatisticsQueryVO vo) {
        Pagination page = null;

        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            if(AppUtil.isEmpty(vo.getSiteId())) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
            }else {
                siteId = vo.getSiteId();
            }

        } else {
            vo.setSiteId(siteId);
        }
        if (!StringUtils.isEmpty(vo.getTypeCode())) {
            if (BaseContentEO.TypeCode.guestBook.toString().equals(vo.getTypeCode())) {
                page = guestService.getGuestPage(vo);
            } else if (BaseContentEO.TypeCode.messageBoard.toString().equals(vo.getTypeCode())) {
                page = messageBoardService.getUnitMessageBoardPage(vo);
            } else if (BaseContentEO.TypeCode.onlinePetition.toString().equals(vo.getTypeCode())) {
                page = petitionService.getPetitionPage(vo);
            }
        }
        return page;
    }

    /**
     * 获取信息报送详细分页列表
     * @param vo
     * @return
     */
    @RequestMapping("getSubmitPage")
    @ResponseBody
    public Object getSubmitPage(StatisticsQueryVO vo) {
        Pagination page = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        page = submitService.getSubmitPage(vo);
        return page;
    }

    /**
     * 导出文字新闻列表
     *
     * @param vo
     * @param response
     */
    @RequestMapping("exportWordList")
    public void exportWordList(StatisticsQueryVO vo, HttpServletResponse response) {
        List<WordListVO> list = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        list = baseService.getWordList(vo);
        // 文件头
        String[] titles = new String[] { "序号", "部门Id", "部门名称", "发文总数（篇）", "通过发布（篇）", "未通过发布（篇）", "发布率(%)" };
        if(!AppUtil.isEmpty(vo.getIsUser())&&vo.getIsUser().equals("1")){
            titles = new String[] { "序号", "用户Id", "用户名称", "新建文章", "已审发布", "未发布", "发布率(%)" };
        }else if(!AppUtil.isEmpty(vo.getIsColumn())&&vo.getIsColumn().equals("1")){
            titles = new String[] { "序号", "栏目Id", "栏目名称", "新建文章", "已审发布", "未发布", "发布率(%)" };
        }else if(!AppUtil.isEmpty(vo.getIsOrgan())&&vo.getIsOrgan().equals("1")){
            titles = new String[] { "序号", "单位Id", "单位名称", "新建文章", "已审发布", "未发布", "发布率(%)" };
        }

        // 内容
        List<String[]> datas = new ArrayList<String[]>();
        if (list != null && list.size() > 0) {
            int i = 1;
            for (WordListVO wordListVO : list) {
                String[] row1 = new String[7];
                row1[0] = i++ + "";
                row1[1] = wordListVO.getOrganId() + "";
                row1[2] = wordListVO.getOrganName();
                row1[3] = wordListVO.getCount() + "";
                row1[4] = wordListVO.getPublishCount() + "";
                row1[5] = wordListVO.getUnPublishCount() + "";
                row1[6] = wordListVO.getRate() + "";
                // row1[4] = formatTimeToString(log.getCreateDate(),
                // "yyyy-MM-dd HH:mm:ss");
                datas.add(row1);
            }
        }
        // 导出
        String name = System.currentTimeMillis() + "";
        try {
            CSVUtils.download(name, titles, datas, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 导出文字新闻列表
     *
     * @param vo
     * @param response
     */
    @RequestMapping("exportWordListByColumn")
    public void exportWordListByColumn(StatisticsQueryVO vo, HttpServletResponse response) {
        List<WordListVO> list = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        list = baseService.getWordListByColumn(vo);
        // 文件头
        String[] titles = new String[] { "序号", "栏目Id", "栏目名称", "发文总数（篇）", "通过发布（篇）", "未通过发布（篇）", "发布率(%)" };
        // 内容
        List<String[]> datas = new ArrayList<String[]>();
        if (list != null && list.size() > 0) {
            int i = 1;
            for (WordListVO wordListVO : list) {
                String[] row1 = new String[7];
                row1[0] = i++ + "";
                row1[1] = wordListVO.getColumnId() + "";
                row1[2] = wordListVO.getColumnName();
                row1[3] = wordListVO.getCount() + "";
                row1[4] = wordListVO.getPublishCount() + "";
                row1[5] = wordListVO.getUnPublishCount() + "";
                row1[6] = wordListVO.getRate() + "";
                // row1[4] = formatTimeToString(log.getCreateDate(),
                // "yyyy-MM-dd HH:mm:ss");
                datas.add(row1);
            }
        }
        // 导出
        String name = System.currentTimeMillis() + "";
        try {
            CSVUtils.download(name, titles, datas, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 导出信息公开列表
     *
     * @param vo
     * @param response
     */
    @RequestMapping("exportPublicList")
    public void exportPublicList(StatisticsQueryVO vo, HttpServletResponse response) {
        List<PublicListVO> list = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        list = publicService.getPublicList(vo);
        HSSFWorkbook workbook = getExcel(list);
        if (workbook == null) {
            return;
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + workbook.getSheetName(0) + ".csv");
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 导出信息报送列表
     */
    @RequestMapping("exportSubmitList")
    public void exportSubmitList(StatisticsQueryVO vo, HttpServletResponse response) {
        List<SubmitListVO> list = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        List<String[]> datas = new ArrayList<String[]>();
        String[] titles;
        if(vo.getSelectOne()==1||vo.getSelectOne()==2) {
            if(vo.getSelectOne()==1){
                // 文件头
                titles = new String[]{"序号", "单位Id", "单位名称", "报送（条）", "采用（条）", "未采用（条）", "采用率(%)"};
            }else{
                titles = new String[]{"序号", "部门Id", "部门名称", "报送（条）", "采用（条）", "未采用（条）", "采用率(%)"};
            }
            // 内容
            list = submitService.getSubmitList(vo);
            if (list != null && list.size() > 0) {
                int i = 1;
                for (SubmitListVO guestListVO : list) {
                    String[] row1 = new String[7];
                    row1[0] = i++ + "";
                    row1[1] = guestListVO.getOrganId() + "";
                    row1[2] = guestListVO.getOrganName();
                    row1[3] = guestListVO.getCount() + "";
                    row1[4] = guestListVO.getEmployCount() + "";
                    row1[5] = guestListVO.getUnEmployCount() + "";
                    row1[6] = guestListVO.getRate() + "";
                    // row1[4] = formatTimeToString(log.getCreateDate(),
                    // "yyyy-MM-dd HH:mm:ss");
                    datas.add(row1);
                }
            }

        }else{
            // 文件头
            titles = new String[] { "序号", "用户名称", "单位名称", "报送（条）", "采用（条）", "未采用（条）", "采用率(%)" };
            // 内容

            list = submitService.getSubmitList2(vo);
            if (list != null && list.size() > 0) {
                int i = 1;
                for (SubmitListVO guestListVO : list) {
                    String[] row1 = new String[7];
                    row1[0] = i++ + "";
                    row1[1] = guestListVO.getPersonName();
                    row1[2] = guestListVO.getOrganName();
                    row1[3] = guestListVO.getCount() + "";
                    row1[4] = guestListVO.getEmployCount() + "";
                    row1[5] = guestListVO.getUnEmployCount() + "";
                    row1[6] = guestListVO.getRate() + "";
                    // row1[4] = formatTimeToString(log.getCreateDate(),
                    // "yyyy-MM-dd HH:mm:ss");
                    datas.add(row1);
                }
            }
        }

        // 导出
        String name = System.currentTimeMillis() + "";
        try {
            CSVUtils.download(name, titles, datas, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出留言和信访列表
     *
     * @param vo
     * @param response
     */
    @RequestMapping("exportGuestList")
    public void exportGuestList(StatisticsQueryVO vo, HttpServletResponse response) {
        List<GuestListVO> list = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        if (!StringUtils.isEmpty(vo.getTypeCode())) {//留言
            if (BaseContentEO.TypeCode.guestBook.toString().equals(vo.getTypeCode())) {
                list = guestService.getGuestList(vo);
            } else if (BaseContentEO.TypeCode.onlinePetition.toString().equals(vo.getTypeCode())) {//网上信访
                list = petitionService.getPetitionList(vo);
            }
        }
        // 文件头
        String[] titles = new String[] { "序号", "单位Id", "单位名称", "接收（条）", "回复（条）", "未回复（条）", "回复率(%)" };
        // 内容
        List<String[]> datas = new ArrayList<String[]>();
        if (list != null && list.size() > 0) {
            int i = 1;
            for (GuestListVO guestListVO : list) {
                String[] row1 = new String[7];
                row1[0] = i++ + "";
                row1[1] = guestListVO.getOrganId() + "";
                row1[2] = guestListVO.getOrganName();
                row1[3] = guestListVO.getRecCount() + "";
                row1[4] = guestListVO.getDealCount()+ "";
                row1[5] = guestListVO.getUndoCount() + "";
                row1[6] = guestListVO.getRate() + "";
                // row1[4] = formatTimeToString(log.getCreateDate(),
                // "yyyy-MM-dd HH:mm:ss");
                datas.add(row1);
            }
        }
        // 导出
        String name = System.currentTimeMillis() + "";
        try {
            CSVUtils.download(name, titles, datas, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出多单位留言列表
     *
     * @param vo
     * @param response
     */
    @RequestMapping("exportMessageBoardList")
    public void exportMessageBoardList(StatisticsQueryVO vo, HttpServletResponse response) {
        List<MessageBoardListVO> list = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        if (!StringUtils.isEmpty(vo.getTypeCode())) {
            if (BaseContentEO.TypeCode.messageBoard.toString().equals(vo.getTypeCode())) {
                list = messageBoardService.getMessageBoardList(vo);
            }
        }
        // 文件头s
        String[] titles = new String[] { "序号", "单位名称", "接收（条）", "回复（条）", "未回复（条）", "回复率(%)" };
        // 内容
        List<String[]> datas = new ArrayList<String[]>();
        if (list != null && list.size() > 0) {
            int i = 1;
            for (MessageBoardListVO messageBoardListVO : list) {
                String[] row1 = new String[6];
                row1[0] = i++ + "";
                row1[1] = messageBoardListVO.getOrganName() + "";
                row1[2] = messageBoardListVO.getReceiveCount()+"";
                row1[3] = messageBoardListVO.getReplyCount() + "";
                row1[4] = messageBoardListVO.getNoReplyCount() + "";
                row1[5] = messageBoardListVO.getReplyRate() + "";
                datas.add(row1);
            }
        }
        // 导出
        String name = System.currentTimeMillis() + "";
        try {
            CSVUtils.download(name, titles, datas, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @RequestMapping("toDetailPage")
    public String toDetailPage(Long uId,String uName,Model model){
        model.addAttribute("uId",uId);
        model.addAttribute("uName",uName);
        return "/statistics/submit_detail";
    }

    @RequestMapping("getDetailPage")
    @ResponseBody
    public Object getDetailPage(Long pageIndex,Integer pageSize,Long uId ,String uName){
        Pagination page=submitService.getDetailPage(pageIndex,pageSize,uId,uName);
        return page;
    }

    /**
     * 设计导出的Excel表格
     * @param list
     * @return
     */
    private HSSFWorkbook getExcel(List<PublicListVO> list) {
        // 创建新的Excel 工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        String name = System.currentTimeMillis() + "";
        // 创建新的Sheet
        HSSFSheet sheet = workbook.createSheet(name);
        HSSFCellStyle style = workbook.createCellStyle(); // 样式对象
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
        // 设置标题行样式
        HSSFRow row = sheet.createRow((short) 0);
        HSSFRow row2 = sheet.createRow((short) 1);
        // 四个参数分别是：起始行，起始列，结束行，结束列
//        sheet.addMergedRegion(new Region(0, (short) 0, 1, (short) 0));
        sheet.addMergedRegion(new CellRangeAddress((short) 0,(short) 0, (short) 1, (short) 0));
        HSSFCell ce = row.createCell((short) 0);
        ce.setCellValue("序号"); // 表格的第一行第一列显示的数据
        ce.setCellStyle(style); // 样式，居中
//        sheet.addMergedRegion(new Region(0, (short) 1, 1, (short) 1));
        sheet.addMergedRegion(new CellRangeAddress((short) 0,(short) 1, (short) 1, (short) 1));
        HSSFCell ce1 = row.createCell((short) 1);
        ce1.setCellValue("部门Id");
        ce1.setCellStyle(style);
//        sheet.addMergedRegion(new Region(0, (short) 2, 1, (short) 2));
        sheet.addMergedRegion(new CellRangeAddress((short) 0,(short) 2, (short) 1, (short) 2));
        HSSFCell ce2 = row.createCell((short) 2);
        ce2.setCellValue("部门名称");
        ce2.setCellStyle(style);
//        sheet.addMergedRegion(new Region(0, (short) 3, 1, (short) 3));
        sheet.addMergedRegion(new CellRangeAddress((short) 0,(short) 3, (short) 1, (short) 3));
        HSSFCell ce3 = row.createCell((short) 3);
        ce3.setCellValue("新建文章(篇)");
        ce3.setCellStyle(style);
//        sheet.addMergedRegion(new Region(0, (short) 4, 0, (short) 9));
        sheet.addMergedRegion(new CellRangeAddress((short) 0,(short) 4, (short) 0, (short) 9));
        HSSFCell ce4 = row.createCell((short) 4);
        ce4.setCellValue("通过发布数（篇）");
        ce4.setCellStyle(style);
        HSSFCell ce5 = row2.createCell((short) 4);
        ce5.setCellValue("总发布数");
        ce5.setCellStyle(style);
        HSSFCell ce55 = row2.createCell((short) 5);
        ce55.setCellValue("公开指南");
        ce55.setCellStyle(style);
        HSSFCell ce6 = row2.createCell((short) 6);
        ce6.setCellValue("公开年报");
        ce6.setCellStyle(style);
        HSSFCell ce7 = row2.createCell((short) 7);
        ce7.setCellValue("公开制度");
        ce7.setCellStyle(style);
        HSSFCell ce8 = row2.createCell((short) 8);
        ce8.setCellValue("主动公开");
        ce8.setCellStyle(style);
        HSSFCell ce9 = row2.createCell((short) 9);
        ce9.setCellValue("依申请公开");
        ce9.setCellStyle(style);
//        sheet.addMergedRegion(new Region(0, (short) 10, 1, (short) 10));
        sheet.addMergedRegion(new CellRangeAddress((short) 0,(short) 10, (short) 1, (short) 10));
        HSSFCell ce10 = row.createCell((short) 10);
        ce10.setCellValue("未发布(篇)");
        ce10.setCellStyle(style);
//        sheet.addMergedRegion(new Region(0, (short) 11, 1, (short) 11));
        sheet.addMergedRegion(new CellRangeAddress((short) 0,(short) 11, (short) 1, (short) 11));
        HSSFCell ce11 = row.createCell((short) 11);
        ce11.setCellValue("发布率（%）");
        ce11.setCellStyle(style);
        for (int i = 0; i < list.size(); i++) {// 行
            int j = 0;// 列
            HSSFRow irow = sheet.createRow((short) i + 2);
            HSSFCell jce = irow.createCell((short) j);
            jce.setCellValue(i);
            jce.setCellStyle(style);
            HSSFCell jce1 = irow.createCell((short) ++j);
            jce1.setCellValue(list.get(i).getOrganId());
            jce1.setCellStyle(style);
            HSSFCell jce2 = irow.createCell((short) ++j);
            jce2.setCellValue(list.get(i).getOrganName());
            jce2.setCellStyle(style);
            HSSFCell jce3 = irow.createCell((short) ++j);
            jce3.setCellValue(list.get(i).getCount());
            jce3.setCellStyle(style);
            HSSFCell jce4 = irow.createCell((short) ++j);
            jce4.setCellValue(list.get(i).getPublishCount());
            jce4.setCellStyle(style);
            HSSFCell jce5 = irow.createCell((short) ++j);
            jce5.setCellValue(list.get(i).getPublishCount1());
            jce5.setCellStyle(style);
            HSSFCell jce6 = irow.createCell((short) ++j);
            jce6.setCellValue(list.get(i).getPublishCount2());
            jce6.setCellStyle(style);
            HSSFCell jce7 = irow.createCell((short) ++j);
            jce7.setCellValue(list.get(i).getPublishCount3());
            jce7.setCellStyle(style);
            HSSFCell jce8 = irow.createCell((short) ++j);
            jce8.setCellValue(list.get(i).getPublishCount4());
            jce8.setCellStyle(style);
            HSSFCell jce9 = irow.createCell((short) ++j);
            jce9.setCellValue(list.get(i).getPublishCount5());
            jce9.setCellStyle(style);
            HSSFCell jce10 = irow.createCell((short) ++j);
            jce10.setCellValue(list.get(i).getUnPublishCount());
            jce10.setCellStyle(style);
            HSSFCell jce11 = irow.createCell((short) ++j);
            jce11.setCellValue(list.get(i).getRate());
            jce11.setCellStyle(style);
        }
        return workbook;

    }



    /**
     * 获取空栏目列表
     *
     * @param vo
     * @return
     */
    @RequestMapping("getEmptyColumnList")
    @ResponseBody
    public Object getEmptyColumnList(StatisticsQueryVO vo) {
        vo.setIsColumn("1");
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        return getObject(baseService.getEmptyColumnList(vo));
    }

    /**
     * 获取空栏目数量
     *
     * @param vo
     * @return
     */
    @RequestMapping("getEmptyColumnCount")
    @ResponseBody
    public Object getEmptyColumnCount(StatisticsQueryVO vo) {
        vo.setIsColumn("1");
        if(AppUtil.isEmpty(vo.getTypeCode())){
            vo.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());
        }
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        return getObject(baseService.getEmptyColumnCount(vo));
    }

    /**
     * 导出空栏目列表
     *
     * @param vo
     * @param response
     */
    @RequestMapping("exportEmptyColumnList")
    public void exportEmptyColumnList(StatisticsQueryVO vo, HttpServletResponse response) {
        List<WordListVO> list = null;
        vo.setIsColumn("1");
        Long siteId = LoginPersonUtil.getSiteId();

        SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);


        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        list = baseService.getEmptyColumnList(vo);
        // 文件头
        String[] titles = new String[] { "序号", "栏目Id", "栏目名称", "栏目URL地址" };

        // 内容
        List<String[]> datas = new ArrayList<String[]>();
        if (list != null && list.size() > 0) {
            int i = 1;
            for (WordListVO wordListVO : list) {

                String columnUrl = siteMgrEO.getUri()+"/"+wordListVO.getOrganId()+".html";//栏目页访问地址

                String[] row1 = new String[4];
                row1[0] = i++ + "";
                row1[1] = wordListVO.getOrganId() + "";
                row1[2] = ColumnUtil.getColumnName(wordListVO.getOrganId(),siteId);
                row1[3] = columnUrl;
                datas.add(row1);
            }
        }
        // 导出
        String name = System.currentTimeMillis() + "";
        try {
            CSVUtils.download(name, titles, datas, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取统计数量
     * @param queryVO
     * @return
     */
    @RequestMapping("getSummaryCount")
    @ResponseBody
    public SummaryCountVO getSummaryCount(StatisticsQueryVO queryVO) {
        SummaryCountVO resultVO = new SummaryCountVO();
        resultVO.setQueryType(queryVO.getTypeCode());
        //用于查询的vo
        StatisticsQueryVO vo = new StatisticsQueryVO();
        vo.setTypeCode(queryVO.getTypeCode());
        vo.setStartDate(DateUtil.getYearMonthDayStr());//当天
        //1.留言统计(今日新增留言，系统待办留言，系统已办留言)
        if(BaseContentEO.TypeCode.messageBoard.toString().equals(queryVO.getTypeCode())) {
            //今日新增留言
            resultVO.setTodayCreateNums(messageBoardService.getSummaryCount(vo));
            //系统待办留言
            vo = new StatisticsQueryVO();
            vo.setDealStatus(MessageBoardEO.DealStatus.unhandle.toString());
            resultVO.setTodoNums(messageBoardService.getSummaryCount(vo));
            //系统已办留言
            vo = new StatisticsQueryVO();
            vo.setDealStatus(MessageBoardEO.DealStatus.handled.toString());
            resultVO.setDoneNums(messageBoardService.getSummaryCount(vo));
        }
        //2.综合信息（文字、图片、视频，当天新增、更新、发布数量）
        else  {
            //当天新增、更新、发布数量
            vo.setOpType("create");
            resultVO.setTodayCreateNums(baseService.getSummaryCount(vo));
            vo.setOpType("update");
            resultVO.setTodayUpdateNums(baseService.getSummaryCount(vo));
            vo.setOpType("publish");
            resultVO.setTodayPublishNums(baseService.getSummaryCount(vo));
        }
        return resultVO;
    }

    @ResponseBody
    @RequestMapping("getCreateOrganByColumnIds")
    public Object getCreateOrganByColumnIds(Long[] columnIds) {
        Long siteId = LoginPersonUtil.getSiteId();
        List<Long> columnIdList = getColumnIds(columnIds);
        List<OrganEO> list = baseService.getCreateOrganByColumnIds(columnIdList,siteId);
        return list;
    }

    @ResponseBody
    @RequestMapping("getContentStatis")
    public Object getContentStatis(Long[] columnIds,Long parentId,StatisticsQueryVO queryVO) {
        Long siteId = LoginPersonUtil.getSiteId();
        List<Long> organIds = new ArrayList<Long>();

        List<OrganEO> organList = baseService.getCreateOrganByColumnIds(getColumnIds(columnIds),siteId);
        if(null != organList && organList.size() > 0){
            for(OrganEO organEO : organList){
                if(!organIds.contains(organEO.getOrganId())){
                    organIds.add(organEO.getOrganId());
                }
            }
        }

        List<Long> columnIdList = new ArrayList<Long>();
        if(!AppUtil.isEmpty(parentId)){
            List<ColumnMgrEO> columnMgrEOList = getColumnById(parentId);
            if(null != columnMgrEOList && columnMgrEOList.size() > 0){
                for(ColumnMgrEO mgrEO : columnMgrEOList){
                    columnIdList.add(mgrEO.getIndicatorId());
                }
            }
        }else {
            columnIdList = new ArrayList(Arrays.asList(columnIds));
        }

        queryVO.setColumnIdList(getColumnIds(columnIds));
        queryVO.setOrganIds(organIds);
        queryVO.setSiteId(siteId);

        List<Map<String,Object>> mapList = baseService.statisticsByColumnIdsAndOrganId(queryVO);

        List<Long> ids = new ArrayList<Long>();
        if(null != mapList && mapList.size()>0){
            for(Map<String,Object> map :mapList){
                if(map.get("ISPARENT").toString().equals(String.valueOf(1)) ){
                    sumSubCount(map,mapList,organIds,ids);
                }
            }
        }

        List<Map<String,Object>> resultList = new ArrayList<Map<String, Object>>();
        for(Map<String,Object> map : mapList) {
            String columnId = map.get("COLUMNID").toString();
            if (columnIdList.indexOf(Long.valueOf(columnId)) > -1) {
                if (null == map.get("TOTALCOUNT")) {
                    map.put("TOTALCOUNT", 0);
                }
                for (Long organId : organIds) {
                    if (null == map.get("C_" + organId)) {
                        map.put("C_" + organId, 0);
                    }
                }
                if (map.get("ISPARENT").toString().equals(String.valueOf(1))) {
                    map.put("isLeaf", false);
                } else {
                    map.put("isLeaf", true);
                }
                map.put("expanded", false);
                resultList.add(map);
            }
        }
        return resultList;
    }

    public void sumSubCount(Map<String,Object> map,List<Map<String,Object>> mapList,List<Long> organIds,List<Long> ids){
            String columnId = map.get("COLUMNID").toString();
            if(ids.indexOf(Long.valueOf(columnId)) > -1){
                return;
            }
            for(int i = 0; i < mapList.size(); i++){
                Map<String,Object> subMap  = mapList.get(i);
                String parentId = subMap.get("PARENTID").toString();
                if(Long.valueOf(parentId).equals(Long.valueOf(columnId))){

                    if(subMap.get("ISPARENT").toString().equals(String.valueOf(1)) ){
                        sumSubCount(subMap,mapList,organIds,ids);
                    }
                    Integer totalcount = 0;
                    Integer subTotalCount = 0;
                    for(Long organId : organIds){
                        Integer count = 0;
                        if(null != map.get("C_"+organId)){
                            count = Integer.valueOf(map.get("C_"+organId).toString());
                        }
                        Integer subCount = 0;
                        if(null != subMap.get("C_"+organId)){
                            subCount = Integer.valueOf(subMap.get("C_"+organId).toString());
                        }
                        map.put("C_"+organId,count+subCount);
                    }
                    if(null != map.get("TOTALCOUNT")){
                        totalcount = Integer.valueOf(map.get("TOTALCOUNT").toString());
                    }
                    if(null != subMap.get("TOTALCOUNT")){
                        subTotalCount = Integer.valueOf(subMap.get("TOTALCOUNT").toString());
                    }
                    map.put("TOTALCOUNT",totalcount+subTotalCount);

                    if(ids.indexOf(Long.valueOf(columnId)) < 0){
                        ids.add(Long.valueOf(columnId));

                    }
                }
            }
    }

    public List<Long> getColumnIds(Long[] columnIds){
        List<Long> columnIdList = new ArrayList<Long>();
        if(null != columnIds && columnIds.length > 0){
            for(Long columnId : columnIds){
                List<ColumnMgrEO> columnMgrList = columnConfigService.getAllColumnBySite(columnId);
                if(null != columnMgrList && columnMgrList.size() > 0){
                    for(ColumnMgrEO columnMgrEO : columnMgrList){
                        if(!columnIdList.contains(columnMgrEO.getIndicatorId())){
                            columnIdList.add(columnMgrEO.getIndicatorId());
                        }
                    }
                }
                columnIdList.add(columnId);
            }
        }
        return columnIdList;
    }

    public List<ColumnMgrEO> getColumnById(Long indicatorId) {
        if (indicatorId == null) {
            return new ArrayList<ColumnMgrEO>();
        }
        //异步获取栏目
        List<ColumnMgrEO> list = columnConfigService.getColumnTreeBySite(indicatorId);
        if (list == null || list.size() == 0) {
            return new ArrayList<ColumnMgrEO>();
        }
        //处理统计信息
        baseService.getStatisticsCount(list);
        boolean flag1 = LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot() || LoginPersonUtil.isSiteAdmin();
        if (flag1) {//厂商、超管、站点管理员不走权限判断
            return list;
        } else {
            List<ColumnMgrEO> list1 = getColumnOpt(list);
            if (list1 == null || list1.size() == 0) {
                return new ArrayList<ColumnMgrEO>();
            }
            return list1;
        }
    }

    /**
     * 获取当前用户的栏目权限
     *
     * @param list
     * @return
     */
    public List<ColumnMgrEO> getColumnOpt(List<ColumnMgrEO> list) {
        if (list == null || list.size() == 0) {
            return list;
        }
        return siteRightsService.getCurUserColumnOpt(list);
    }

}
