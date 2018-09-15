package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.govbbs.internal.dao.IBbsPostDao;
import cn.lonsun.govbbs.internal.vo.BbsPostVO;
import cn.lonsun.govbbs.util.BbsStaticCenterUtil;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 主题信息
 * Created by zhangchao on 2016/12/26.
 */
@Component
public class GovBbsPostListBeanService extends AbstractBeanService {

    @Autowired
    private IBbsPostDao bbsPostDao;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        //站点id
        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        // 需要显示条数.
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        num =(num == null?8:num);
        //版块ids
        List<Long> plateIds = null;
        String plateIdStr = paramObj.getString("plateIds");
        if (!StringUtils.isEmpty(plateIdStr)) {
            plateIds = new ArrayList<Long>();
            plateIds.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(plateIdStr.split(","), Long.class))));
        }
        List<Long> delPlateIds = null;
        //排序不显示的版块
        String delPlateIdStr = paramObj.getString("delPlateIds");
        if (!StringUtils.isEmpty(delPlateIdStr)) {
            delPlateIds = new ArrayList<Long>();
            delPlateIds.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(delPlateIdStr.split(","), Long.class))));
        }
        //单位ids
        List<Long> unitIds = null;
        String unitIdStr = paramObj.getString("unitIds");
        if (!StringUtils.isEmpty(unitIdStr)) {
            unitIds = new ArrayList<Long>();
            unitIds.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(unitIdStr.split(","), Long.class))));
        }
        //信息类型
        List<String> keys = null;
        String keyStr = paramObj.getString("keys");
        if (!StringUtils.isEmpty(keyStr)) {
            keys = new ArrayList<String>();
            keys.addAll(Arrays.asList((String[]) (ConvertUtils.convert(keyStr.split(","), String.class))));
        }
        //默认排序
        String sortField = paramObj.getString("sortField");
        if(StringUtils.isEmpty(sortField)){
            sortField = BbsStaticCenterUtil.defaultPostOrder;
        }
        //是否查询需要办理的
        Boolean isAccept = paramObj.getBoolean("isAccept");
        if(isAccept == null){isAccept = false;}

        //是否影藏一个月没有回复的
        Boolean isHideMon = paramObj.getBoolean("isHideMon");
        if(isHideMon == null){isHideMon = false;}
        //是否查询红牌
        String ry = paramObj.getString("isRedYellow");
        Integer isRedYellow = null;
        if(!StringUtils.isEmpty(ry)){
            isRedYellow = BbsStaticCenterUtil.getLevel(ry);
        }
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("select" +
                " b.postId as postId,b.title as title,b.isAccept as isAccept,b.acceptUnitId as acceptUnitId," +
                "b.acceptUnitName as acceptUnitName,b.createDate as createDate" +
                " from BbsPostEO b where b.siteId = ? and b.recordStatus = 'Normal' and b.isPublish = 1 and b.createDate is not null");
        values.add(siteId);
        if(plateIds != null && !plateIds.isEmpty()){
            hql.append(" and (1=0");
            for(Long plateId:plateIds){
                hql.append(" or b.plateId = ?");
                values.add(plateId);
            }
            hql.append(")");
        }
        if(delPlateIds != null && !delPlateIds.isEmpty()){
            for(Long plateId:delPlateIds){
                hql.append(" and b.plateId != ?");
                values.add(plateId);
            }
        }
        if(unitIds != null && !unitIds.isEmpty()){
            hql.append(" and (1=0");
            for(Long unitId:unitIds){
                hql.append(" or b.acceptUnitId = ?");
                values.add(unitId);
            }
            hql.append(")");
        }
        if(isHideMon){
            Date end = new Date();
            Calendar date = Calendar.getInstance();
            date.setTime(end);
            date.set(Calendar.DATE, date.get(Calendar.DATE) - 30);
            end = date.getTime();
            hql.append(" and (b.isAccept is null or b.isAccept = 1 or (b.isAccept = 0 and b.createDate >= ?))");
            values.add(end);
        }
        if(keys != null && !keys.isEmpty()){
            hql.append(" and (1=0");
            for(String key:keys){
                hql.append(" or b.infoKey = ?");
                values.add(key);
            }
            hql.append(")");
        }
        if(isAccept){
            hql.append(" and (b.isAccept = 1 or b.isAccept = 0) ");
        }
        if(null != isRedYellow){
            Long times = new Date().getTime()/1000;
            if(isRedYellow == 1){//正常的
                hql.append(" and (b.yellowTimes > ? or b.yellowTimes is null)");
                values.add(times);
            }else if(isRedYellow == 2){//黄牌
                hql.append(" and b.isAccept = 0 ");
                hql.append(" and b.yellowTimes <= ?");
                hql.append(" and b.redTimes > ?");
                values.add(times);
                values.add(times);
            }else if(isRedYellow == 3){//红牌
                hql.append(" and b.isAccept = 0 ");
                hql.append(" and b.redTimes <= ?");
                values.add(times);
            }
        }
        hql.append(" order by ").append(sortField);
        List<BbsPostVO> posts = (List<BbsPostVO>)bbsPostDao.getBeansByHql(hql.toString(),values.toArray(),BbsPostVO.class,num);
        return posts;
    }

}
