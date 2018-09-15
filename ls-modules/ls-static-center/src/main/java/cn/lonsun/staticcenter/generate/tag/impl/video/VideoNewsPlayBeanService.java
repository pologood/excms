package cn.lonsun.staticcenter.generate.tag.impl.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.content.internal.dao.IVideoNewsDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.VideoNewsVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.util.MongoUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * <br/>
 * 播放视频文章页
 * 
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-15<br/>
 */
@Component
public class VideoNewsPlayBeanService extends AbstractBeanService {

    @Autowired
    private IVideoNewsDao videoDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Long contentId = paramObj.getLong(GenerateConstant.ID);
        StringBuffer hql =
                new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId ")
                        .append(" ,c.imageLink as imageLink ,c.author as author")
                        .append(",v.videoId as videoId,v.videoPath as videoPath,v.videoName as videoName,v.status as status")
                        .append(" from BaseContentEO c, VideoNewsEO v where c.id=v.contentId  and c.typeCode='" + BaseContentEO.TypeCode.videoNews.toString()
                                + "' and c.id=" + contentId)
                        .append(" and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'  and v.recordStatus='"
                                + AMockEntity.RecordStatus.Normal.toString() + "' ");
        VideoNewsVO vo=(VideoNewsVO)videoDao.getBean(hql.toString(), new Object[] {}, VideoNewsVO.class);
        try {
            vo.setArticle(MongoUtil.queryById(vo.getId()));
        } catch (GenerateException e) {
            e.printStackTrace();
        }
        return vo;
    }


}