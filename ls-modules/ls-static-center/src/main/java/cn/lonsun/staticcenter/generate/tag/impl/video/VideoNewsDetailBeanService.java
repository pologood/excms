package cn.lonsun.staticcenter.generate.tag.impl.video;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IVideoNewsDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.VideoNewsVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.generate.util.RegexUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频新闻属性预处理
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-14<br/>
 */
@Component
public class VideoNewsDetailBeanService extends AbstractBeanService {
    @Autowired
    private IVideoNewsDao videoDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long contentId = context.getContentId();// 根据文章id查询文章
        StringBuffer hql =
                new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId,c.titleColor as titleColor ")
                        .append(" ,c.imageLink as imageLink ,c.author as author,c.resources as resources,c.num as num ,c.editor as editor")
                        .append(" ,c.isBold as isBold,c.isTilt as isTilt,c.isUnderline as isUnderline,c.remarks as remarks,c.responsibilityEditor as responsibilityEditor")
                        .append(" ,c.publishDate as publishDate,c.isPublish as isPublish,c.isTop as isTop,c.isNew as isNew ,c.isHot as isHot,c.hit as hit ")
                        .append(" ,c.createDate as createDate,c.updateDate as updateDate,c.topValidDate as topValidDate,v.fileType as fileType")
                        .append(",v.videoId as videoId,v.videoPath as videoPath,v.videoName as videoName,v.imageName as imageName ,v.status as status")
                        .append(" from BaseContentEO c, VideoNewsEO v where c.id=v.contentId  and c.typeCode=? and c.id=?")
                        .append(" and c.recordStatus=?  and v.recordStatus=?");
        List<Object> values = new ArrayList<Object>();
        values.add(BaseContentEO.TypeCode.videoNews.toString());
        values.add(contentId);
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        return videoDao.getBean(hql.toString(), values.toArray(), VideoNewsVO.class);
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        VideoNewsVO eo = (VideoNewsVO) resultObj;
        // 根据id去mongodb读取文件内容
        eo.setArticle(MongoUtil.queryById(eo.getId()));
//        String fileServerPath= PathUtil.getPathConfig().getFileServerPath();
        String videoName=eo.getVideoName();
        String videoPath=eo.getVideoPath();
        if (!StringUtils.isEmpty(videoName)&&!videoName.equals(videoPath)) {
            if( !AppUtil.isEmpty(videoPath)){
//                videoPath = fileServerPath + videoPath;
                eo.setVideoPath(PathUtil.getUrl(eo.getVideoPath()));

            }
        }
// else{
//            eo.setVideoPath(videoName);
//        }
        return RegexUtil.parseProperty(content, eo);
    }
}