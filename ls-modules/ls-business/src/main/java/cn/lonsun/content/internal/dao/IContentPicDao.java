package cn.lonsun.content.internal.dao;

import java.util.List;

import cn.lonsun.content.internal.entity.ContentPicEO;
import cn.lonsun.core.base.dao.IMockDao;

public interface IContentPicDao extends IMockDao<ContentPicEO> {
    /**
     * @param contentId
     * @return
     * @Description 根据主表ID获取记录集合   get the picture list by the content ID
     * @author Hewbing
     * @date 2015年10月9日 下午3:51:44
     */
    public List<ContentPicEO> getPicsList(Long contentId);

    /**
     * @param path
     * @param picId
     * @Description 修改图片路径和缩略图路径  update the picture and it's thumb path
     * @author Hewbing
     * @date 2015年10月9日 下午3:52:28
     */
    public void updatePicPath(String path, String thumbPath, Long picId);

    /**
     * @param picEO
     * @Description 修改图片信息  update the picture info
     * @author Hewbing
     * @date 2015年10月9日 下午3:52:50
     */
    public void updatePicInfo(ContentPicEO picEO);

    /**
     * @param picEO
     * @Description 修改图片基础信息
     * @author Hewbing
     * @date 2015年10月9日 下午3:53:20
     */
    public void updatePic(ContentPicEO picEO, Long contentId);

    public void updatePic(ContentPicEO picEO);

    /**
     * @param paths
     * @return List<ContentPicEO>    返回类型
     * @throws
     * @Title: getListByPath
     * @Description: mongodb path调用
     */
    public List<ContentPicEO> getListByPath(String[] paths);


    void removePic(Long[] contentIds);

}
