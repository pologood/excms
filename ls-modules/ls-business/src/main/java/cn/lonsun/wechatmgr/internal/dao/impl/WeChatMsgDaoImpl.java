package cn.lonsun.wechatmgr.internal.dao.impl;


import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.dao.IWeChatMsgDao;
import cn.lonsun.wechatmgr.internal.entity.WechatMsgEO;
import cn.lonsun.wechatmgr.vo.MessageVO;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;
import cn.lonsun.wechatmgr.vo.WeResponseVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gu.fei
 * @version 2016-09-29 14:34
 */
@Repository("weChatMsgDao")
public class WeChatMsgDaoImpl extends MockDao<WechatMsgEO> implements IWeChatMsgDao {
    @Override
    public Pagination getUserResponse(WeChatUserVO pageQueryVO) {
        List<Object> param =new ArrayList<Object>();
        StringBuffer hql =new StringBuffer();
        hql.append("select n.ID as id,n.CREATE_TIME as createTime,n.CREATE_DATE as createDate,n.JUDGE as judge,m.NICKNAME as nickname,m.SEX as sex,m.HEADIMGURL as headimgurl,n.CONTENT as content,n.PIC_URL as picUrl,n.MEDIA_ID as mediaId,n.REP_MSG_ID as repMsgId, n.REP_MSG_CONTENT as repMsgContent,n.REP_MSG_DATE as repMsgDate,n.ORIGIN_USER_NAME as originUserName,n.UPDATE_DATE as updateDate  from   cms_wechat_user m, cms_wechat_msg n");
        hql.append("  where m.SITE_ID =?  and   m.OPENID = n.ORIGIN_USER_NAME and m.RECORD_STATUS =? and n.RECORD_STATUS=?");
        param.add(pageQueryVO.getSiteId());
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(AMockEntity.RecordStatus.Normal.toString());
        if(!LoginPersonUtil.isSuperAdmin()&&!LoginPersonUtil.isSiteAdmin()){

            hql.append(" and n.TURN_UNIT_ID=?");
            param.add(LoginPersonUtil.getUnitId());
        }
        if(!AppUtil.isEmpty(pageQueryVO.getNickname())){
            hql.append(" and  n.CONTENT like ?");
            param.add("%" + SqlUtil.prepareParam4Query(pageQueryVO.getNickname()) + "%");

        }

        if(!AppUtil.isEmpty(pageQueryVO.getCreateDate())){
            hql.append(" and  n.CREATE_DATE >= ?  and n.CREATE_DATE < ? ");
            param.add(pageQueryVO.getCreateDate());
            param.add(new Date(pageQueryVO.getCreateDate().getTime()+24*3600*1000));

        }
        hql.append(" order by n.CREATE_TIME desc");
        return getPaginationBySql(pageQueryVO.getPageIndex(),pageQueryVO.getPageSize(),hql.toString(),param.toArray(), WeResponseVO.class);
    }

    @Override
    public Pagination getUserTurn(WeChatUserVO pageQueryVO) {
        List<Object> param =new ArrayList<Object>();
        StringBuffer hql =new StringBuffer();
        hql.append("select n.ID as id,n.CREATE_TIME as createTime,m.NICKNAME as nickname,n.JUDGE as judge,n.CREATE_DATE as createDate,m.SEX as sex,m.HEADIMGURL as headimgurl,n.CONTENT as content,n.PIC_URL as picUrl,n.MEDIA_ID as mediaId,n.REP_MSG_ID as repMsgId, n.REP_MSG_CONTENT as repMsgContent,n.REP_MSG_DATE as repMsgDate,n.ORIGIN_USER_NAME as originUserName,n.UPDATE_DATE as updateDate  from   cms_wechat_user m, cms_wechat_msg n");
        hql.append("  where m.SITE_ID =?  and   m.OPENID = n.ORIGIN_USER_NAME and m.RECORD_STATUS =? and n.RECORD_STATUS=?");
        param.add(LoginPersonUtil.getSiteId());
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(AMockEntity.RecordStatus.Normal.toString());
        if(!LoginPersonUtil.isSuperAdmin()&&!LoginPersonUtil.isSiteAdmin()){

            hql.append(" and n.CHANGE_UNIT_ID=?");
            param.add(LoginPersonUtil.getUnitId());
        }
        if(!AppUtil.isEmpty(pageQueryVO.getNickname())){
            hql.append(" and  n.CONTENT like ?");
            param.add("%" + SqlUtil.prepareParam4Query(pageQueryVO.getNickname()) + "%");

        }

        if(!AppUtil.isEmpty(pageQueryVO.getCreateDate())){
            hql.append(" and  n.CREATE_DATE >= ?  and n.CREATE_DATE < ? ");
            param.add(pageQueryVO.getCreateDate());
            param.add(new Date(pageQueryVO.getCreateDate().getTime()+24*3600*1000));

        }
        hql.append(" order by n.CREATE_TIME desc");

        return getPaginationBySql(pageQueryVO.getPageIndex(),pageQueryVO.getPageSize(),hql.toString(),param.toArray(), WeResponseVO.class);

    }

    @Override
    public List<Object> getWeekCount(List<String> days,Integer isRep,Long siteId) {
        if(!(days != null && days.size() > 0)){
            return null;
        }
        String sql="select";
        for(int i = 0;i< days.size();i++){
            sql +=" sum(case when to_char(t.create_date,'yyyy-MM-dd') = '"+days.get(i)+"'";
            if(isRep != null && (isRep == 0 || isRep == 1)){
                sql +=" and t.is_rep = "+isRep+"";
            }
            sql +=" then 1 else 0 end)";
            if(i != days.size() -1){
                sql +=",";
            }
        }
        sql+= " from cms_wechat_msg t where t.record_status = 'Normal'";
        if(siteId != null){
            sql +=" and t.site_id = "+siteId+"";
        }
        return (List<Object>)getObjectsBySql(sql,new Object[]{});
    }

    @Override
    public List<Object> getUnitsCount(Long siteId) {
        String sql = "select t.id,t.create_time,t.is_rep,t.rep_msg_date,t.turn_unit_id,r.name_ " +
                "from cms_wechat_msg t left join rbac_organ r on t.turn_unit_id = r.organ_id where t.record_status = 'Normal' and t.site_id = ? order by t.id desc";
        return (List<Object>)getObjectsBySql(sql,new Object[]{siteId});
    }

    @Override
    public List<WechatMsgEO> getTodoJudge(MessageVO msg, Long siteId) {
        StringBuffer hql =new StringBuffer();
        hql.append("from  WechatMsgEO t where  t.siteId =?  and  t.originUserName =? and  t.msgType =? and t.isRep=1 and t.isJudge=0 and t.recordStatus =?  order by  t.updateDate desc ");
        List<Object> param =new ArrayList<Object>();
        param.add(siteId);
        param.add(msg.getFromUserName());
        param.add("text");
        param.add(AMockEntity.RecordStatus.Normal.toString());
        return getEntitiesByHql(hql.toString(),param.toArray());
    }
}
