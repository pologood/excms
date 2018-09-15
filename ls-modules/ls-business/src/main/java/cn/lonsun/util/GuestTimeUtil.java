package cn.lonsun.util;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.*;

/**
 * 留言红黄牌处理<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-1<br/>
 */
public class GuestTimeUtil {

    private static final String pattern = "yyyy-MM-dd";
    private static Calendar cale = Calendar.getInstance();

    /**
     * 计算红黄牌
     *
     * @author fangtinghua
     * @param list
     * @return
     * @throws ParseException
     */
    public static List<GuestBookEditVO> setTimes(List<GuestBookEditVO> list) throws ParseException {
        if (null == list || list.isEmpty()) {
            return null;
        }
        Date endTime = new Date();
        if(list.get(0).getColumnId()==null||list.get(0).getSiteId()==null){
            return null;
        }
        Long siteId = list.get(0).getSiteId();
        Long columnId = list.get(0).getColumnId();
        ColumnTypeConfigVO setting =  ModelConfigUtil.getCongfigVO(columnId, siteId);
        if(setting==null){
            return null;
        }
        for (GuestBookEditVO editVO : list) {
            editVO.setRecType(setting.getRecType());
            if (setting.getIsRedYellow() != null && setting.getIsRedYellow() == 1 && editVO.getReceiveId() != null) {// 接收单位
                if (null != editVO.getIsResponse() && editVO.getIsResponse() == 1) {
                    endTime = editVO.getReplyDate();
                }
                Date startTime = editVO.getAddDate();
                long intervalMilli = endTime.getTime() - startTime.getTime();
                if (intervalMilli >= 0) {
                    int day = (int) (intervalMilli / (24 * 60 * 60 * 1000)) + 1;
                    // 处理日期池
                    if (!StringUtils.isEmpty(setting.getDatePool())) {
                        String[] timesArray = setting.getDatePool().split("#");
                        for (String t : timesArray) {
                            Long timeI = DateUtils.parseDate(t, pattern).getTime();
                            if (timeI >= startTime.getTime() && timeI <= endTime.getTime()) {
                                day--;
                            }
                        }
                    }
                    while (startTime.compareTo(endTime) <= 0) {
                        cale.setTime(startTime);
                        if (cale.get(Calendar.DAY_OF_WEEK) == 6 || cale.get(Calendar.DAY_OF_WEEK) == 0) {
                            day--;
                        }
                        startTime = DateUtils.addDays(startTime, 1);
                    }
                    if (setting.getLimitDay() != null && day > setting.getLimitDay()) {
                        editVO.setIsTimeOut(1);
                        if (setting.getYellowCardDay() != null && day > setting.getYellowCardDay()) {
                            editVO.setIsTimeOut(2);
                            if (setting.getRedCardDay() != null && day > setting.getRedCardDay()) {
                                editVO.setIsTimeOut(3);
                            }
                        }
                    }
                }
            }
            if (!StringUtils.isEmpty(editVO.getCommentCode())) {
                DataDictVO dictVO = DataDictionaryUtil.getItem("guest_comment", editVO.getCommentCode());
                if (dictVO != null) {
                    editVO.setCommentName(dictVO.getKey());
                }
            }
            if (!StringUtils.isEmpty(editVO.getDealStatus())) {
                DataDictVO dictVO = DataDictionaryUtil.getItem("deal_status", editVO.getDealStatus());
                if (dictVO != null) {
                    editVO.setStatusName(dictVO.getKey());
                }
            }
            if (StringUtils.isEmpty(setting.getClassCodes()) || StringUtils.isEmpty(editVO.getClassCode())) {
                continue;
            } else if (setting.getClassCodes().contains(editVO.getClassCode())) {
                editVO.setLimitDay(setting.getLimitDay());
                editVO.setYellowCardDay(setting.getYellowCardDay());
                editVO.setRedCardDay(setting.getRedCardDay());
                DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", editVO.getClassCode());
                if (dictVO != null) {
                    editVO.setClassName(dictVO.getKey());
                }
            }
            if(editVO.getRecType()!=null&&editVO.getRecType()==1){
                if(!StringUtils.isEmpty(editVO.getReplyUserId())){
                    DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", editVO.getReplyUserId());
                    if (dictVO != null) {
                        editVO.setReplyUserName(dictVO.getKey());
                    }
                }
                if(editVO.getReplyUnitId()!=null){
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, editVO.getReplyUnitId());
                    if (organEO != null) {
                        editVO.setReplyUnitName(organEO.getName());
                    }
                }
                if(editVO.getReceiveId()!=null){
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, editVO.getReceiveId());
                    if (organEO != null) {
                        editVO.setReceiveName(organEO.getName());
                    }
                }

            }

            if(editVO.getRecType()!=null&&editVO.getRecType()==0){
                if(editVO.getReceiveId()!=null){
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, editVO.getReceiveId());
                    if (organEO != null) {
                        editVO.setReceiveName(organEO.getName());
                    }
                }
            }
        }
        return list;
    }
}