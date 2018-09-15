package cn.lonsun.content.internal.dao;

import cn.lonsun.content.internal.entity.VideoNewsEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.VideoNewsVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;

import java.util.List;

/**
 * 视频新闻Dao层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-21<br/>
 */

public interface IVideoNewsDao extends IMockDao<VideoNewsEO> {
    /**
     * 获取视频新闻分页
     *
     * @return
     */
    public Pagination getPage(ContentPageVO pageVO);

    /**
     * 根据内容协同主表id获取视频新闻信息
     *
     * @param id
     * @return
     */
    public VideoNewsVO getVideoEO(Long id, String status);

    public VideoNewsVO getRemovedVideo(Long id);

    public void changeStatus(Long videoId, String mongoId);

    public Long countData(Long columnId);

    List<VideoNewsVO> getListForPublish(Long columnId);

    public VideoNewsVO getEntityForPublish(Long contentId);

    public void removeVideos(Long[] ids);

    public List<VideoNewsVO> getVideoList(Long columnId, Long siteId, Integer num);
}
