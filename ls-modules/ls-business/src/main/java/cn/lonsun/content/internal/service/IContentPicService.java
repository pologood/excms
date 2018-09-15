package cn.lonsun.content.internal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentPicEO;
import cn.lonsun.content.vo.SynColumnVO;
import cn.lonsun.core.base.service.IMockService;

public interface IContentPicService extends IMockService<ContentPicEO> {
    /**
     * @param contentId
     * @return
     * @Description 根据主表ID获取记录集合
     * @author Hewbing
     * @date 2015年10月9日 下午3:54:13
     *
     */
    public List<ContentPicEO> getPicsList(Long contentId);

    /**
     * @param path
     * @param picId
     * @Description 修改图片路径和缩略图路径
     * @author Hewbing
     * @date 2015年10月9日 下午3:54:41
     */
    public void changePicPath(String path, String thumbPath, Long picId);

    /**
     * @param picEO
     * @Description 修改图片信息
     * @author Hewbing
     * @date 2015年10月9日 下午3:54:48
     */
    public void updatePicInfo(ContentPicEO picEO);

    /**
     * @param picList
     * @Description 修改图片基本信息
     * @author Hewbing
     * @date 2015年10月9日 下午3:54:52
     */
    public void allSavePic(List<ContentPicEO> picList);

    /**
     * @param picEO
     * @Description 跳过log添加图片
     * @author Hewbing
     * @date 2015年10月9日 下午5:13:43
     */
    public void addPic(ContentPicEO picEO);

    /**
     * @param contentEO
     * @param picList
     * @return Long    返回类型
     * @throws
     * @Title: savePicNews
     * @Description: 保存图片新闻
     */
    public SynColumnVO savePicNews(BaseContentEO contentEO, String content, String picList, Long[] synColumnIds);


    /**
     * @param paths
     * @return List<ContentPicEO>    返回类型
     * @throws
     * @Title: getListByPath
     * @Description: mongodb path调用
     */
    public List<ContentPicEO> getListByPath(String[] paths);

    /**
     * @param Filedata
     * @param siteId
     * @param columnId
     * @param contentId
     * @param picId
     * @param request
     * @return String   返回类型
     * @throws
     * @Title: picBeautify
     * @Description: TODO
     */
    public String picBeautify(MultipartFile Filedata, Long siteId, Long columnId, Long contentId, Long picId, HttpServletRequest request);

    /**
     * @param picId 设定文件
     * @return void   返回类型
     * @throws
     * @Title: delPic
     * @Description: TODO
     */
    public void delPic(Long picId);

    public void synToColumn(List<Long> picIds, BaseContentEO contentEO, String content, Long[] synColumnIds, Long oldId);

    void removePic(Long[] contentIds);

    void updatePic(ContentPicEO l, Long id);

    void updateNums(Long picIds[],Long sortNums[]);

}
