package cn.lonsun.content.supervise.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardPageVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.util.PoiExcelUtil;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.system.sitechart.vo.KVL;
import cn.lonsun.system.sitechart.vo.KVP;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhangmf
 * @version 2018-07-09 18:51
 */
@Controller
@RequestMapping(value="supervise", produces = { "application/json;charset=UTF-8" })
public class SuperviseController extends BaseController {

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @RequestMapping("index")
    public String index(ModelMap map){
        //设置开始结束时间
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        cal.add(Calendar.MONTH,-1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = cal.getTime();
        map.put("startDate",format.format(startDate));
        map.put("endDate",format.format(endDate));
        return "/content/supervise/supervise_page";
    }

    @RequestMapping("details")
    public String details(ModelMap map){
        //设置开始结束时间
        return "/content/supervise/details";
    }

    /**
     * 获取所有 留言类型的栏目
     * @return
     */
    @RequestMapping(value = "getColumns")
    @ResponseBody
    public Object getColumns() {
        return columnConfigService.getColumnByTypeCode(LoginPersonUtil.getSiteId(), BaseContentEO.TypeCode.messageBoard.toString());
    }

    /**
     * 获取所有 留言类别和受理单位
     * @return
     */
    @RequestMapping(value = "getRec")
    @ResponseBody
    public Map<String,List<ContentModelParaVO>> getRec(Long columnId) {
        Map<String,List<ContentModelParaVO>> map = new HashMap<String, List<ContentModelParaVO>>();
        List<ContentModelParaVO> recList = new ArrayList<ContentModelParaVO>();
        List<ContentModelParaVO> codeList = new ArrayList<ContentModelParaVO>();
        if (!AppUtil.isEmpty(columnId)) {
            recList.addAll(ModelConfigUtil.getParam(columnId, LoginPersonUtil.getSiteId(), null));
        }else {
            List<ColumnMgrEO> columns = columnConfigService.getColumnByTypeCode(LoginPersonUtil.getSiteId(), BaseContentEO.TypeCode.messageBoard.toString());
            if (columns.size() > 0) {
                columnId = columns.get(0).getIndicatorId();
            }
        }
        recList.addAll(ModelConfigUtil.getParam(columnId, LoginPersonUtil.getSiteId(), null));
        codeList.addAll(ModelConfigUtil.getMessageBoardType(columnId, LoginPersonUtil.getSiteId()));

        map.put("recList",recList);
        map.put("codeList",codeList);

        return map;
    }

    @RequestMapping(value = "getAllData")
    @ResponseBody
    public Object getAllDate(MessageBoardPageVO pageVO, Integer tabIndex) {
        if (!AppUtil.isEmpty(tabIndex)) {
            return getMap(pageVO,tabIndex);
        }

        return null;
    }

    // 获取留言分页
    @RequestMapping(value = "getDetailPage")
    @ResponseBody
    public Pagination getDetailPage(MessageBoardPageVO pageVO) {
        Date ed = pageVO.getEd();
        if (!AppUtil.isEmpty(ed)) {
            ed.setTime(ed.getTime() + 24*3600000);//结束时间往后推一天，保证本日的数据
            pageVO.setEd(ed);
        }
        return messageBoardService.getPage(pageVO);
    }

    // 获取留言分页
    @RequestMapping(value = "getPage")
    @ResponseBody
    public Object getPage(MessageBoardPageVO pageVO, Integer tabIndex) {
        Pagination page = new Pagination();
        Integer pageSize = pageVO.getPageSize();
        Long pageIndex = pageVO.getPageIndex();
        page.setPageSize(pageSize);
        page.setPageIndex(pageIndex);

        if (!AppUtil.isEmpty(tabIndex)) {
            Map<String,Map<String,Long>> map = getMap(pageVO,tabIndex);
            List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
            List<Map<String,Object>> list1 = new ArrayList<Map<String, Object>>();
            Long total = 0L;

            //获取list，并设置total
            total = getPageList(pageVO,tabIndex,map,list);

            page.setTotal(total);

            //分页处理
            Long pageCount = total / pageSize + 1;
            page.setPageCount(pageCount);

            for (int i = 0 ; i < list.size() ; i++) {
                if (i >= pageSize*pageIndex) {
                    list1.add(list.get(i));
                }
            }
            page.setData(list1);
        }
        return page;
    }

    private Long getPageList(MessageBoardPageVO pageVO,Integer tabIndex,Map<String,Map<String,Long>> map,List<Map<String,Object>> list) {
        DecimalFormat df = new DecimalFormat("#0.0");
        Long total = 0L;
        List<ContentModelParaVO> recList = getRec(pageVO.getColumnId()).get("recList");
        List<ContentModelParaVO> codeList = getRec(pageVO.getColumnId()).get("codeList");

        List<ContentModelParaVO> tempList;
        if (tabIndex.intValue() == 1) {
            tempList = codeList;
        }else {
            tempList = recList;
        }

        //遍历类别或者受理单位的list来组合数据，保证前台顺序一致
        for (ContentModelParaVO temp:tempList) {
            Map<String,Object> tempMap = new HashMap<String, Object>();
            String key;
            if (tabIndex.intValue() == 1) {
                key = temp.getClassCode();
            }else {
                key = temp.getRecUnitId().toString();
            }
            //如果结果中不包含
            if (map.get(key) == null) continue;

            tempMap.put("name",key);
            Long ok = map.get(key).get("ok");
            Long no = map.get(key).get("no");
            Long sum = ok + no;
            tempMap.put("ok",ok);
            tempMap.put("no",no);
            if (sum > 0) {
                tempMap.put("satisfyPr",df.format((double)ok/sum*100));
            }else {
                tempMap.put("satisfyPr",0);
            }

            list.add(tempMap);
            total += 1L;
        }

        return total;
    }

    /**
     * 导出Excel
     * @throws Exception
     */
    @RequestMapping("exportExcel")
    public void downExcelPage(HttpServletRequest request, HttpServletResponse response,MessageBoardPageVO pageVO, Integer tabIndex) throws Exception{
        DecimalFormat df = new DecimalFormat("#0.0");
        List<ContentModelParaVO> recList = getRec(pageVO.getColumnId()).get("recList");
        List<ContentModelParaVO> codeList = getRec(pageVO.getColumnId()).get("codeList");
        Map<String,Object> titleMap = new HashMap<String, Object>(codeList.size());
        for (ContentModelParaVO code:codeList) {
            titleMap.put(code.getClassCode(),code.getClassName());
        }
        for (ContentModelParaVO rec:recList) {
            titleMap.put(rec.getRecUnitId().toString(),rec.getRecUnitName());
        }
        if (!AppUtil.isEmpty(tabIndex)) {
            Map<String,Map<String,Long>> map = getMap(pageVO,tabIndex);
            List<Object[]> list = new ArrayList<Object[]>();
            Long total = 0L;
            //遍历处理数据
            List<ContentModelParaVO> tempList1;
            if (tabIndex.intValue() == 1) {
                tempList1 = codeList;
            }else {
                tempList1 = recList;
            }

            //遍历类别或者受理单位的list来组合数据，保证前台顺序一致
            for (ContentModelParaVO temp:tempList1) {
                List<Object> tempList = new ArrayList<Object>();
                String key;
                if (tabIndex.intValue() == 1) {
                    key = temp.getClassCode();
                }else {
                    key = temp.getRecUnitId().toString();
                }
                //如果结果中不包含
                if (map.get(key) == null) continue;

                tempList.add(titleMap.get(key));
                Long ok = map.get(key).get("ok");
                Long no = map.get(key).get("no");
                Long sum = ok + no;
                tempList.add(ok);
                tempList.add(no);
                if (sum > 0) {
                    tempList.add(df.format((double)ok/sum*100) + "%");
                }else {
                    tempList.add(0 + "%");
                }

                list.add(tempList.toArray());
                total += 1L;
            }

            PoiExcelUtil.exportExcel("评价监督", "评价监督", "xls",new String[]{"单位","满意","不满意","满意率"} , list, response);
        }
    }

    private Map<String,Map<String,Long>> getMap(MessageBoardPageVO pageVO, Integer tabIndex) {
        Map<String,Map<String,Long>> map = new LinkedHashMap<String,Map<String,Long>>();
        Date ed = pageVO.getEd();
        if (!AppUtil.isEmpty(ed)) {
            ed.setTime(ed.getTime() + 24*3600000);//结束时间往后推一天，保证本日的数据
            pageVO.setEd(ed);
        }
        pageVO.setEd(ed);
        List<KVP> kvps1;//满意
        List<KVP> kvps2;//不满意
        List<KVL> kvl1;//满意
        List<KVL> kvl2;//不满意

        if (tabIndex.intValue() == 1) {//tabIndex 1 : 按留言类别统计
            pageVO.setSatisfactoryType(1);
            kvps1 = messageBoardService.getSatisfactoryTypeCount(pageVO);//满意
            pageVO.setSatisfactoryType(2);
            kvps2 = messageBoardService.getSatisfactoryTypeCount(pageVO);//不满意
            Map<String,Long> tempMap;
            for (KVP kvp:kvps1) {
                tempMap = new HashMap<String, Long>();
                tempMap.put("ok",kvp.getValue());
                tempMap.put("no",0L);
                map.put(kvp.getName(),tempMap);
            }

            for (KVP kvp:kvps2) {
                if (map.get(kvp.getName()) != null) {
                    tempMap = map.get(kvp.getName());
                }else {
                    tempMap = new HashMap<String, Long>();
                    tempMap.put("ok",0L);
                }

                tempMap.put("no",kvp.getValue());
                map.put(kvp.getName(),tempMap);
            }
        }else {//2 ： 按单位统计
            pageVO.setSatisfactoryType(1);
            kvl1 = messageBoardService.getSatisfactoryUnitCount(pageVO);//满意
            pageVO.setSatisfactoryType(2);
            kvl2 = messageBoardService.getSatisfactoryUnitCount(pageVO);//不满意

            Map<String,Long> tempMap;
            for (KVL kvl:kvl1) {
                tempMap = new HashMap<String, Long>();
                tempMap.put("ok",kvl.getValue());
                tempMap.put("no",0L);
                map.put(kvl.getName().toString(),tempMap);
            }

            for (KVL kvl:kvl2) {
                if (map.get(kvl.getName().toString()) != null) {
                    tempMap = map.get(kvl.getName().toString());
                }else {
                    tempMap = new HashMap<String, Long>();
                    tempMap.put("ok",0L);
                }

                tempMap.put("no",kvl.getValue());
                map.put(kvl.getName().toString(),tempMap);
            }
        }
        return map;
    }
}
