package cn.lonsun.staticcenter.generate.tag.impl.search.util;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.solr.vo.SolrPageQueryVO;
import cn.lonsun.staticcenter.generate.tag.impl.search.Common;
import cn.lonsun.supervise.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author gu.fei
 * @version 2016-5-24 17:06
 */
public class SearchUtil {

    public static SolrPageQueryVO setPageVal(Map<String, String> map) {
        SolrPageQueryVO vo = new SolrPageQueryVO();
        if(!AppUtil.isEmpty(map) && !map.isEmpty()) {

            if(!isNull(map.get("columnId"))) {
                Long columnId = Long.valueOf(paternStr(map.get("columnId")));
                List<IndicatorEO> indicatorEOs = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID,columnId);
                if(null != indicatorEOs && !indicatorEOs.isEmpty()) {
                    vo.setIndicatorEOs(indicatorEOs);
                } else {
                    vo.setColumnId(Long.valueOf(map.get("columnId")));
                }
            } else if(!isNull(map.get("columnIds"))) {
                vo.setColumnIds(paternStr(map.get("columnIds")));
            } else if(!isNull(map.get("excColumns"))) {
                vo.setExcColumns(paternStr(map.get("excColumns")));
            }

            vo.setSortOrder(paternStr(map.get("sort")));
            vo.setDatecode(paternStr(map.get("datecode")));
            String datacode = paternStr(map.get("datecode"));
            if(!isNull(datacode)) {
                vo.setBeginDate(getDateByCode(paternStr(datacode)));
            } else {
                vo.setBeginDate(DateUtil.trStr2Date(paternStr(map.get("beginDate"))));
                vo.setEndDate(DateUtil.trStr2Date(paternStr(map.get("endDate"))));
            }

            //关键字查询来源
            vo.setFromCode(paternStr(map.get("fromCode")));

            //查询类型
            if(!isNull(map.get("typeCode"))) {
                vo.setTypeCode(paternStr(map.get("typeCode")));
            }

            //政务论坛留言类型
            if(!isNull(map.get("type"))) {
                vo.setType(paternStr(map.get("type")));
            }

            //信息公开索引号
            if(!isNull(map.get("indexNum"))) {
                vo.setIndexNum(map.get("indexNum"));
            }

            //信息公开公文号
            if(!isNull(map.get("fileNum"))) {
                vo.setFileNum(map.get("fileNum"));
            }
        }
        return vo;
    }

    public static boolean isNull(String str) {
        return null == str || "".equals(str);
    }

    /**
     * 过滤回车换行
     * @param str
     * @return
     */
    public static String paternStr (String str) {
        if(!StringUtils.isEmpty(str)) {
            str = str.replaceAll("","");
            Pattern p = Pattern.compile("(\r\n|\r|\n|\n\r)");
            str = p.matcher(str).replaceAll("");
        }
        return str;
    }

    /**
     * 根据时间返回获取指定日期
     * @param datecode
     * @return
     */
    private static Date getDateByCode(String datecode) {
        Calendar calendar = Calendar.getInstance();
        if(datecode.equals(Common.DAY)) {
            calendar.add(Calendar.DATE, -1);    //得到前一天
        } else if(datecode.equals(Common.WEEK)) {
            calendar.add(Calendar.WEEK_OF_YEAR,-1);
        } else if(datecode.equals(Common.MONTH)) {
            calendar.add(Calendar.MONTH,-1);
        } else if(datecode.equals(Common.YEAR)) {
            calendar.add(Calendar.YEAR,-1);
        }
        return calendar.getTime();
    }
}
