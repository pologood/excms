package cn.lonsun.content.internal.service;

import cn.lonsun.content.internal.entity.VideoNewsEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.VideoNewsVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;

import java.io.IOException;
import java.util.List;

/**
 * 视频新闻Service层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-21<br/>
 */

public interface IVideoNewsService extends IMockService<VideoNewsEO> {
    /**
     * 删除视频新闻
     *
     * @param id
     */
    public String delVideoEO(Long id);

    /**
     * 获取视频新闻分页
     */
    public Pagination getPage(ContentPageVO pageVO);


    /**
     * 根据内容协同主表id获取视频新闻信息
     *
     * @param id
     * @return
     */
    public VideoNewsVO getVideoEO(Long id, String status);

    //根据主表ID查找已删除视频信息
    public VideoNewsVO getRemovedVideo(Long id);

    /**
     * 保存视频新闻
     *
     * @param vo
     */
    public VideoNewsEO saveVideo(VideoNewsVO vo);

    public String delVideoEOs(Long[] ids);

    public void changeStatus(Long videoId, String mongoId);

    public void changeStatusNew(Long videoId, String path);

    public void failChange(Long videoId);

    public Long changeHit(Long id);

    public Long countData(Long columnId);

    public List<VideoNewsVO> getListForPublish(Long columnId);


    public VideoNewsVO getEntityForPublish(Long contentId);

    /**
     * 物理删除主表和辅助表
     *
     * @param ids
     */
    public void removeVideos(Long[] ids);

    public List<VideoNewsVO> getVideoList(Long columnId, Long siteId, Integer num);

    public void importVideo(VideoNewsVO vo);

    public void transVideo(Long videoId) throws IOException;

    void importHongAn(List<Object> list, Long siteId, ColumnMgrEO columnMgrEO);
}

