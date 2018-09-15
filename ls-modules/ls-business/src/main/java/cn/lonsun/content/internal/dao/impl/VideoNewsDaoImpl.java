package cn.lonsun.content.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IVideoNewsDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.VideoNewsEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.VideoNewsVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频新闻Dao实现类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-21<br/>
 */

@Repository("videoNewsDao")
public class VideoNewsDaoImpl extends MockDao<VideoNewsEO> implements IVideoNewsDao {
    /**
     * 获取视频新闻分页
     *
     */
    @Override
    public Pagination getPage(ContentPageVO pageVO) {
        StringBuffer hql=new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId,c.titleColor as titleColor ")
                .append(" ,c.imageLink as imageLink ,c.remarks as remarks,c.author as author,c.resources as resources,c.num as num,c.editor as editor ")
                .append(" ,c.isBold as isBold,c.isTilt as isTilt,c.isUnderline as isUnderline,c.remarks as remarks,c.workFlowStatus as workFlowStatus")
                .append(" ,c.publishDate as publishDate,c.isPublish as isPublish,c.isTop as isTop,c.isNew as isNew ,c.isHot as isHot,c.hit as hit ")
                .append(" ,c.createDate as createDate,c.updateDate as updateDate,c.topValidDate as topValidDate,v.videoPath as videoPath,v.videoName as videoName" )
                .append(",v.status as status,v.imageName as imageName,v.fileType as fileType,v.id as videoId")
                .append(" from BaseContentEO c, VideoNewsEO v where c.id=v.contentId and c.typeCode='" + BaseContentEO.TypeCode.videoNews.toString() + "'")
                .append(" and c.recordStatus='"+AMockEntity.RecordStatus.Normal.toString()+"' ");

        if(!AppUtil.isEmpty(pageVO.getTitle())){
            hql .append(" and c.title like '%"+ SqlUtil.prepareParam4Query(pageVO.getTitle().trim())+"%'");
        }
        if(!AppUtil.isEmpty(pageVO.getColumnId())){
            hql.append(" and c.columnId="+pageVO.getColumnId());
        }else{
            hql.append(" and c.columnId is null");
        }
        if(!AppUtil.isEmpty(pageVO.getCondition())&&pageVO.getStatus()!=null){
            hql.append(" and c."+pageVO.getCondition()+"="+pageVO.getStatus());
        }
        hql.append(ModelConfigUtil.getOrderByHql(pageVO.getColumnId(),pageVO.getSiteId(),BaseContentEO.TypeCode.videoNews.toString()));
        return getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), new Object[]{},VideoNewsVO.class);
    }



    /**
     * 根据内容协同主表id获取视频新闻信息
     * @param id
     * @return
     */
    @Override
    public VideoNewsVO getVideoEO(Long id,String status) {
        StringBuffer hql=new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId,c.titleColor as titleColor,c.responsibilityEditor as responsibilityEditor ")
                .append(" ,c.imageLink as imageLink ,c.author as author,c.resources as resources,c.num as num,c.workFlowStatus as workFlowStatus ")
                .append(" ,c.isBold as isBold,c.isTilt as isTilt,c.isUnderline as isUnderline,c.remarks as remarks,c.editor as editor")
                .append(" ,c.publishDate as publishDate,c.isPublish as isPublish,c.isTop as isTop,c.isNew as isNew ,c.isHot as isHot,c.hit as hit ")
                .append(" ,c.createDate as createDate,c.updateDate as updateDate,c.topValidDate as topValidDate,c.isJob as isJob,c.jobIssueDate as jobIssueDate")
                .append(",v.videoId as videoId,v.videoPath as videoPath,v.videoName as videoName,v.imageName as imageName ,v.status as status,v.fileType as fileType")
                .append(" from BaseContentEO c, VideoNewsEO v where c.id=v.contentId  and c.typeCode='" + BaseContentEO.TypeCode.videoNews.toString() + "' and c.id="+id);

        if(!AMockEntity.RecordStatus.Removed.toString().equals(status)){
            hql.append(" and c.recordStatus='"+ AMockEntity.RecordStatus.Normal.toString()+"' ");
        }
        List<VideoNewsVO> list=(List<VideoNewsVO>)  getBeansByHql(hql.toString(), new Object[]{}, VideoNewsVO.class);
        VideoNewsVO vo=null;
        if(list!=null&&list.size()>0){
            vo=list.get(0);
        }
        return vo;
    }

    @Override
    public void changeStatus(Long videoId, String mongoId) {
        VideoNewsEO eo=getEntity(VideoNewsEO.class, videoId);
        if(eo.getVideoId()!=null){
            eo.setStatus(100);
            eo.setVideoPath(mongoId);
            update(eo);
        }
    }

    @Override
    public Long countData(Long columnId) {
        StringBuffer hql = new StringBuffer("select * from BaseContentEO c, VideoNewsEO v "
                + "where v.contentId=c.id and c.recordStatus=?  and c.columnId=?");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(columnId);
        return getCount(hql.toString(),values.toArray());
    }

    @Override
    public List<VideoNewsVO> getListForPublish(Long columnId) {
        StringBuffer hql = new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId,c.titleColor as titleColor ")
                .append(" ,c.imageLink as imageLink ,c.author as author,c.resources as resources,c.num as num ")
                .append(" ,c.isBold as isBold,c.isTilt as isTilt,c.isUnderline as isUnderline,c.remarks as remarks")
                .append(" ,c.publishDate as publishDate,c.isPublish as isPublish,c.isTop as isTop,c.isNew as isNew ,c.isHot as isHot,c.hit as hit ")
                .append(" ,c.createDate as createDate,c.updateDate as updateDate")
                .append(",v.videoId as videoId,v.videoPath as videoPath,v.videoName as videoName,v.imageName as imageName ,v.status as status")
                .append(" from BaseContentEO c, VideoNewsEO v where c.id=v.contentId  and c.typeCode=? and c.columnId=? ")
                .append(" and c.recordStatus=?  and c.isPublish=1");

        List<Object> values = new ArrayList<Object>();
        values.add(BaseContentEO.TypeCode.videoNews.toString());
        values.add(columnId);
        values.add(AMockEntity.RecordStatus.Normal.toString());
        List<VideoNewsVO> list=(List<VideoNewsVO>)  getBeansByHql(hql.toString(), values.toArray(), VideoNewsVO.class);
        return list;
    }
    @Override
    public VideoNewsVO getEntityForPublish(Long contentId) {
        StringBuffer hql=new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId,c.titleColor as titleColor ")
                .append(" ,c.imageLink as imageLink ,c.author as author,c.resources as resources,c.num as num ,c.editor as editor")
                .append(" ,c.isBold as isBold,c.isTilt as isTilt,c.isUnderline as isUnderline,c.remarks as remarks")
                .append(" ,c.publishDate as publishDate,c.isPublish as isPublish,c.isTop as isTop,c.isNew as isNew ,c.isHot as isHot,c.hit as hit ")
                .append(" ,c.createDate as createDate,c.updateDate as updateDate,c.topValidDate as topValidDate,v.fileType as fileType" )
                .append(",v.videoId as videoId,v.videoPath as videoPath,v.videoName as videoName,v.imageName as imageName ,v.status as status")
                .append(" from BaseContentEO c, VideoNewsEO v where c.id=v.contentId  and c.typeCode=? and c.id=?")
                .append(" and c.recordStatus=? ");
        List<Object> values = new ArrayList<Object>();
        values.add(BaseContentEO.TypeCode.videoNews.toString());
        values.add(contentId);
        values.add(AMockEntity.RecordStatus.Normal.toString());
        VideoNewsVO vo=(VideoNewsVO) getBean(hql.toString(), values.toArray(), VideoNewsVO.class);
        return vo;
    }


    /**
     * 根据内容协同主表id获取已删除视频新闻信息
     * @param id
     * @return
     */
    @Override
    public VideoNewsVO getRemovedVideo(Long id) {
        StringBuffer hql=new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId,c.titleColor as titleColor ")
                .append(" ,c.imageLink as imageLink ,c.author as author,c.resources as resources,c.num as num ")
                .append(" ,c.isBold as isBold,c.isTilt as isTilt,c.isUnderline as isUnderline,c.remarks as remarks")
                .append(" ,c.publishDate as publishDate,c.isPublish as isPublish,c.isTop as isTop,c.isNew as isNew ,c.isHot as isHot,c.hit as hit ")
                .append(" ,c.createDate as createDate,c.updateDate as updateDate,c.topValidDate as topValidDate" )
                .append(",v.videoId as videoId,v.videoPath as videoPath,v.videoName as videoName,v.imageName as imageName ,v.status as status")
                .append(" from BaseContentEO c, VideoNewsEO v where c.id=v.contentId  and c.typeCode='" + BaseContentEO.TypeCode.videoNews.toString() + "' and c.id="+id)
                .append(" and c.recordStatus='"+ AMockEntity.RecordStatus.Removed.toString()+"' ");
        VideoNewsVO vo=(VideoNewsVO)  getBean(hql.toString(), new Object[]{}, VideoNewsVO.class);
        return vo;
    }

    @Override
    public void removeVideos(Long[] ids) {
        if(null==ids||ids.length<=0){
            return ;
        }
        Map<String, Object> map=new HashMap<String, Object>();
        map.put("contentId", ids);
        String hql=" delete VideoNewsEO where contentId in(:contentId)";
        executeUpdateByJpql(hql, map);

    }

    @Override
    public List<VideoNewsVO> getVideoList(Long columnId, Long siteId, Integer num) {
        StringBuffer hql =
                new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId ")
                        .append(" ,c.imageLink as imageLink ,c.author as author")
                        .append(",v.videoId as videoId,v.videoPath as videoPath,v.videoName as videoName,v.status as status")
                        .append(" from BaseContentEO c, VideoNewsEO v where c.id=v.contentId  and c.typeCode=:typeCode")
                        .append(" and c.columnId=:columnId")
                        .append(" and c.siteId=:siteId and c.recordStatus=:recordStatus and c.isPublish=1");

        hql.append(" order by ").append(ModelConfigUtil.getOrderTypeValue(columnId, siteId));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("typeCode",BaseContentEO.TypeCode.videoNews.toString());
        map.put("columnId",columnId);
        map.put("siteId",siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<VideoNewsVO> list=(List<VideoNewsVO>)getBeansByHql(hql.toString(), map, VideoNewsVO.class, num);
        return list;
    }

}
