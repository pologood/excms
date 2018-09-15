package cn.lonsun.staticcenter.generate.tag.impl.journals;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.journals.dao.IJournalsDao;
import cn.lonsun.journals.vo.JournalsPageVO;
import cn.lonsun.security.internal.vo.MateriaNumVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lonsun on 2017-1-3.
 */
@Component
public class JournalsListBeanService  extends AbstractBeanService {

    @Autowired
    private IJournalsDao journalsDao;

    @Value("${mangerUrl}")
    private String mangerUrl;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        // 需要显示条数.
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        String ation =paramObj.getString("action");

        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);

//       Long pageIndex = paramObj.getLong("pageIndex");
//        if(AppUtil.isEmpty(pageIndex)){
//            pageIndex =Long.valueOf(ContextHolder.getContext().getParamMap().get("pageIndex"));
//            pageIndex=pageIndex-1;
//
//        }
        Long pageIndex = ContextHolder.getContext().getPageIndex() == null ? 0 : (ContextHolder.getContext().getPageIndex() - 1);

        Long columnId=paramObj.getLong(GenerateConstant.ID);
        if(AppUtil.isEmpty(columnId))
        {
            columnId=ContextHolder.getContext().getColumnId();
        }
        List<JournalsPageVO> list =new ArrayList<JournalsPageVO>();


        if(ation.equals("list")){
            list = getList(num,columnId);


        } else if(ation.equals("page")){

           return getPage( columnId, pageIndex, pageSize);

        }




        return list;
    }

       public List<JournalsPageVO> getList(Integer num,Long columnId){
           StringBuilder hql =new StringBuilder();
           Map<String, Object> param  =new HashMap<String, Object>();
           hql.append("select s.materiaName as materiaName,s.year as year,s.periodical as periodical,s.filePath as filePath,c.imageLink as imageLink,c.publishDate as publishDate from BaseContentEO c,JournalsEO s where c.id=s.baseContentId and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'  and s.siteId=:siteId  and c.isPublish=1 and c.columnId=:columnId ");
           param.put("siteId", ContextHolder.getContext().getSiteId());
           param.put("columnId",columnId);
           hql.append(" order by c.publishDate desc");
           List<JournalsPageVO> list =(List<JournalsPageVO>) journalsDao.getBeansByHql(hql.toString(),param, JournalsPageVO.class, num);
//           String fileServerPath= PathUtil.getPathConfig().getFileServerPath();
           for(JournalsPageVO journalsPageVO:  list){
               if(!AppUtil.isEmpty(journalsPageVO.getFilePath())){
                   journalsPageVO.setFilePath(mangerUrl+journalsPageVO.getFilePath());

               }
               if(!AppUtil.isEmpty(journalsPageVO.getImageLink())){
                   journalsPageVO.setImageLink(PathUtil.getUrl(journalsPageVO.getImageLink()));
               }

               journalsPageVO.setTitle("【"+journalsPageVO.getYear()+"年"+journalsPageVO.getPeriodical()+"期"+"】");

           }

        return list;
       }


      public Pagination getPage(Long columnId,Long pageIndex, Integer pageSize ){
          StringBuilder hql =new StringBuilder();
          List<Object> param  =new ArrayList<Object>();
          hql.append("select s.materiaName as materiaName,s.year as year,s.periodical as periodical,s.filePath as filePath,c.imageLink as imageLink,c.publishDate as publishDate from BaseContentEO c,JournalsEO s where c.id=s.baseContentId and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'  and s.siteId=? and c.isPublish=1 and c.columnId=?");
          param.add(ContextHolder.getContext().getSiteId());
          param.add(columnId);
          hql.append(" order by c.publishDate desc ");

          Pagination pagination = journalsDao.getPagination(pageIndex, pageSize, hql.toString(), param.toArray(), JournalsPageVO.class);
          List<JournalsPageVO> list =(List<JournalsPageVO>)pagination.getData();
          for(JournalsPageVO journalsPageVO:  list){
              if(!AppUtil.isEmpty(journalsPageVO.getFilePath())){
                  journalsPageVO.setFilePath(mangerUrl+journalsPageVO.getFilePath());

              }
              journalsPageVO.setTitle("【"+journalsPageVO.getYear()+"年"+journalsPageVO.getPeriodical()+"期"+"】");

          }


          if (ContextHolder.getContext().getColumnId() != null) {
              String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), ContextHolder.getContext().getColumnId(), null);
              pagination.setLinkPrefix(path);
          } else {
              pagination.setLinkPrefix(ContextHolder.getContext().getPath());
          }

          return pagination;

      }

}
