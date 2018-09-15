package cn.lonsun.staticcenter.generate.tag.impl.messageBoard;

import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoarForwardDao;
import cn.lonsun.content.messageBoard.dao.IMessageBoardDao;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MessageBoardYeOrRedPageListBeanService extends AbstractBeanService {

    @Autowired
    private IMessageBoardDao messageBoardDao;

    @Autowired
    private IMessageBoardForwardService forwardService;

    @SuppressWarnings("unchecked")
    @Override
    public Object getObject(JSONObject paramObj) {

        Context context = ContextHolder.getContext();
         String yellowOrRed =context.getParamMap().get("yellowOrRed");
        System.out.println("q特务感受大厦二月行情"+"**********"+yellowOrRed+"q特务感受大厦二月行情");
        if (org.apache.commons.lang3.StringUtils.isEmpty(yellowOrRed)) {
               yellowOrRed = paramObj.getString("yellowOrRed");
        }

        Long siteId = paramObj.getLong("siteId");
          if (siteId == null || siteId == 0) {
            siteId = context.getSiteId();
        }

        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        if(pageSize==null){
            pageSize = 15;
        }

        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate, c.isPublish as isPublish,c.columnId as columnId,c.siteId as siteId")
                .append(",c.id as baseContentId,m.id as id,m.messageBoardContent as messageBoardContent,m.addDate as addDate")
                .append(",m.resourceType as resourceType,m.openId as openId,m.personIp as personIp,m.personName as personName,m.dealStatus as dealStatus")
                .append(",m.docNum as docNum,m.className as className")
                .append(",m.classCode as classCode,m.randomCode as randomCode")
                .append(",m.isPublic as isPublic,m.isPublicInfo as isPublicInfo")
                .append(" from BaseContentEO c,MessageBoardEO m where m.baseContentId=c.id and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and m.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'" + " and c.typeCode = '" + BaseContentEO.TypeCode.messageBoard.toString() + "'")
                .append(" and c.isPublish=1 and m.isPublic =1 and  m.dealStatus is null");

        List<Object> values = new ArrayList<Object>();
        Map<String, Object> result = new HashMap<String, Object>();

        Calendar c = Calendar.getInstance();
        Date date=new Date();
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR,-7);//日期减去7天
        date=c.getTime();

        Calendar c2 = Calendar.getInstance();
        Date startDate=new Date();
        c2.setTime(startDate);
        c2.add(Calendar.DAY_OF_YEAR,-15);//日期减去15天
        startDate=c2.getTime();

        Calendar c3 = Calendar.getInstance();
        Date endDate=new Date();
        c3.setTime(endDate);
        c3.add(Calendar.DAY_OF_YEAR,-70);//日期减去70天
        endDate=c3.getTime();

        if(yellowOrRed.equals("1")){
            hql.append(" and m.createDate <= ? and m.createDate > ?");
            values.add(date);
            values.add(startDate);
        }

        if(yellowOrRed.equals("2")){
            hql.append(" and  m.createDate <= ? and m.createDate > ?");
            values.add(startDate);
            values.add(endDate);
        }
        hql.append(" and c.siteId=?");
        values.add(siteId);
        hql.append(" order by m.createDate desc");
        Pagination page =  messageBoardDao.getPagination(pageIndex,pageSize,hql.toString(), values.toArray(), MessageBoardEditVO.class);
        String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), context.getColumnId(), null);
        page.setLinkPrefix(path);
        List<MessageBoardEditVO> messageBoardEditVOs = (List<MessageBoardEditVO>) page.getData();
        if (null != messageBoardEditVOs && messageBoardEditVOs.size() > 0) {
            for (MessageBoardEditVO editVO :messageBoardEditVOs) {
                  editVO.setLink(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), editVO.getColumnId(), editVO.getBaseContentId()));
                  List<MessageBoardForwardVO> forwardVOList = forwardService.getAllUnit(editVO.getId());
                  if (forwardVOList != null && forwardVOList.size() > 0 && forwardVOList.get(0).getReceiveUnitName() != null) {
                      editVO.setReceiveUnitName(forwardVOList.get(0).getReceiveUnitName());
                }  else {
                      editVO.setReceiveUnitName("已转发其他机构");
                }
            }
        }
        result.put("page",page);
        result.put("yellowOrRed",yellowOrRed);
        return result;
    }

    /*  long intervalMilli = 0;
                Date createDate = editVO.getCreateDate();
                Date toDay = new Date();
                if (createDate != null) {
                    intervalMilli = toDay.getTime() - createDate.getTime();
                    if (intervalMilli >= 0) {
                        int day = (int) (intervalMilli / (24 * 60 * 60 * 1000)) + 1;
                        //1表示黄牌
                        if(yellowOrRed.equals("1")&&day<7&&day>15){
                            delList.add(editVO);
                        }
                        //2表示红牌
                        if(yellowOrRed.equals("2")&&day<=15){
                            delList.add(editVO);
                        }
                    }
                }*/
}


