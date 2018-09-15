package cn.lonsun.wechatmgr.internal.service.impl;


import cn.lonsun.base.anno.DbInject;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.vo.DeleteDnVO;
import cn.lonsun.wechatmgr.internal.dao.IWeChatMsgDao;
import cn.lonsun.wechatmgr.internal.entity.WechatMsgEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatMsgService;
import cn.lonsun.wechatmgr.vo.MessageVO;
import cn.lonsun.wechatmgr.vo.UnitTopStatisVO;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gu.fei
 * @version 2016-09-29 14:33
 */
@Service
public class WeChatMsgServiceImpl extends MockService<WechatMsgEO> implements IWeChatMsgService {
    @DbInject("weChatMsg")
    private IWeChatMsgDao weChatMsgDao;

    @Override
    public Pagination getUserResponse(WeChatUserVO pageQueryVO) {
        return weChatMsgDao.getUserResponse(pageQueryVO);
    }

    @Override
    public Pagination getUserTurn(WeChatUserVO pageQueryVO) {
        return weChatMsgDao.getUserTurn(pageQueryVO);
    }

    @Override
    public List<String> getWeekCount(List<String> days,Integer isRep,Long siteId) {
        List<Object> objects = weChatMsgDao.getWeekCount(days,isRep,siteId);
        List<String> counts=new ArrayList<String>();
        if(objects != null){
            Object[] obj = (Object[])objects.get(0);
            for(int i=0;i<days.size();i++){
                String count = "0";
                try{
                    count = (obj[i] == null?"0" : obj[i].toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
                counts.add(count);
            }
        }
        return counts;
    }

    @Override
    public List<UnitTopStatisVO> getUnitsCount(Long siteId) {
        List<UnitTopStatisVO> uts = null;
        List<Object> objects = weChatMsgDao.getUnitsCount(siteId);
        if(objects != null && objects.size() > 0){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //零时保存单位名称
            Map<String,String> nameMap = new HashMap<String, String>();
            Map<String,List<WechatMsgEO>> mapCT = new HashMap<String, List<WechatMsgEO>>();
            List<WechatMsgEO>  wms = null;
            for(Object obj:objects){
                Object[] objs = (Object[])obj;
                String id = objs[0] == null?"":objs[0].toString();
                String create_time = objs[1] == null?"":objs[1].toString();
                String is_rep = objs[2] == null?"":objs[2].toString();
                String rep_msg_date = objs[3] == null?"":objs[3].toString();
                //如果未转班，设置管理员办理 1
                String turn_unit_id = objs[4] == null? "1":objs[4].toString();
                String name_ = turn_unit_id.equals("1") ?"--[系统]--":(objs[5] == null?"":objs[5].toString());
                Date repDate = null;
                if (!StringUtils.isEmpty(rep_msg_date)) {
                    try { repDate = sdf.parse(rep_msg_date); } catch (ParseException e) { e.printStackTrace();}
                }
                if(!nameMap.containsKey(turn_unit_id)){
                    nameMap.put(turn_unit_id,name_);
                }
                if(!mapCT.containsKey(turn_unit_id)){
                    wms = new ArrayList<WechatMsgEO>();
                }else{
                    wms = mapCT.get(turn_unit_id);
                }
                WechatMsgEO wm = new WechatMsgEO();
                wm.setId(Long.parseLong(id));
                wm.setCreateTime(Long.parseLong(create_time));
                wm.setIsRep(Integer.parseInt(is_rep));
                wm.setRepMsgDate(repDate);
                wm.setTurnUnitId(Long.parseLong(turn_unit_id));
                wms.add(wm);
                mapCT.put(turn_unit_id,wms);
            }
            //处理数据
            uts = GetUnitTopStatisVO(nameMap,mapCT);
        }
        return sort(uts);
    }

    @Override
    public List<WechatMsgEO> getTodoJudge(MessageVO msg, Long siteId) {
        return weChatMsgDao.getTodoJudge(msg,siteId);
    }

    /**
     * 处理数据
     * @param nameMap
     * @param mapCT
     * @return
     */
    private List<UnitTopStatisVO> GetUnitTopStatisVO(Map<String, String> nameMap, Map<String, List<WechatMsgEO>> mapCT) {
        List<UnitTopStatisVO> uts = new ArrayList<UnitTopStatisVO>();
        List<WechatMsgEO>  wms = null;
        DecimalFormat df= new DecimalFormat("#.##");
        for (Map.Entry<String, String> entry : nameMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(!StringUtils.isEmpty(key)){
                UnitTopStatisVO vo = new UnitTopStatisVO();
                vo.setName(value);
                wms = mapCT.get(key);
                if(wms != null && wms.size() > 0){
                    Long sendCounts = 0L;
                    Long replyCounts = 0L;
                    Double replyTimeRate = 0.0;
                    for(WechatMsgEO wm:wms){
                        if(wm.getIsRep() != null && wm.getIsRep() == 1){
                            replyCounts ++;
                            if(wm.getRepMsgDate() != null){
                                Long ct = wm.getCreateTime()*1000;
                                Long rt = wm.getRepMsgDate().getTime();
                                replyTimeRate += (rt.doubleValue()-ct.doubleValue())/(1000*60*60);
                            }
                        }
                        sendCounts ++;
                    }
                    vo.setReplyCount(replyCounts);
                    vo.setSendCount(sendCounts);
                    if(replyCounts != 0){
                        try{
                            Double rr = replyCounts.doubleValue() / sendCounts.doubleValue()*100;
                            vo.setReplyRate(rr.intValue());
                            vo.setReplyTimeRate(df.format(replyTimeRate/replyCounts));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                uts.add(vo);
            }
        }
        return uts;
    }

    /**
     * 排序
     * @param uts
     * @return
     */
    private List<UnitTopStatisVO> sort(List<UnitTopStatisVO> uts) {
        Collections.sort(uts,new Comparator<UnitTopStatisVO>() {
            public int compare(UnitTopStatisVO v1, UnitTopStatisVO v2) {
                Integer r1=v1.getReplyRate();
                Integer r2=v2.getReplyRate();
                return r2.compareTo(r1);
            }
        });
        return uts;
    }
}
