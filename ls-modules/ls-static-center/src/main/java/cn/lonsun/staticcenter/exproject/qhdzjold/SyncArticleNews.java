package cn.lonsun.staticcenter.exproject.qhdzjold;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.util.HotWordsCheckUtil;

import java.util.*;

/**
 * Created by 1960274114 on 2016-10-13.
 * 综合信息
 */
public class SyncArticleNews extends AbSyncInfo {

    public SyncArticleNews(JdbcUtils jdbcUtils, Long siteId, Long createUserId, Long curColumdId) {
        super(jdbcUtils, siteId, createUserId, curColumdId);
    }
    public SyncArticleNews(JdbcUtils jdbcUtils, Long siteId) {
        super(jdbcUtils, siteId, null, null);
    }


    /**
     * 内容栏目
     * @return
     */
    public List<OldSiteColumnPairVO> getOldColumnList(){
        List<OldSiteColumnPairVO> ret = new ArrayList<OldSiteColumnPairVO>();
        String sql = "select id,title from jcms_normal_channel where type=? and Enabled=1 order by id asc";
        List<Object> list =  jdbcUtils.excuteQuery(sql, new String[]{"article"});
        if(null !=list && list.size()>0){
            for(Object obj:list){
                Map<String, Object> map = (HashMap<String, Object>) obj;
                OldSiteColumnPairVO vo = new OldSiteColumnPairVO(map.get("id").toString(),(String)map.get("title"));
                ret.add(vo);
            }
        }
        return ret;
    }

    public List<OldSiteColumnPairVO> getOldClassList(String channelId){
        List<OldSiteColumnPairVO> ret = new ArrayList<OldSiteColumnPairVO>();
        String sql = "select id,title from jcms_normal_class where ChannelId=?";
        List<Object> list =  jdbcUtils.excuteQuery(sql, new String[]{channelId});
        if(null !=list && list.size()>0){
            for(Object obj:list){
                Map<String, Object> map = (HashMap<String, Object>) obj;
                OldSiteColumnPairVO vo = new OldSiteColumnPairVO(map.get("id").toString(),(String)map.get("title"));
                ret.add(vo);
            }
        }
        return ret;
    }

    private List<Object> getSysNewsList(String classId,String channelId){
        String sql = "select Title,AddDate,Summary,Author,Content,ViewNum,IsImg,Img,OutUrl,SourceFrom from jcms_module_article where ChannelId=? and ClassId=? order by id asc";
        return  jdbcUtils.excuteQuery(sql, new String[]{classId,channelId});
    }

    public void imp(String classCode,String columnTypeCode,String channelId){
        List<Object> newsList = getSysNewsList(classCode,channelId);
        if(null !=newsList && newsList.size()>0){
            IBaseContentService baseContentService = SpringContextHolder.getBean(IBaseContentService.class);
            for(Object obj:newsList){
                if(step>limitSize){
                    break;
                }
                step ++;
                Map<String, Object> map = (HashMap<String, Object>) obj;
                //标题
                String title =(String) map.get("Title");
                //副标题
                String subTitle = null;
                //作者
                String author = (String) map.get("Author");
                //来源
                String resources = (String) map.get("SourceFrom");
                //摘要
                String remark = (String)map.get("Summary");
                //图片
                int IsImg = AppUtil.getint(map.get("IsImg"));
                String pic = (String)map.get("Img");
                //视频
               // String video = (String)map.get("sys_videourl");
                //发布日期
                Date publishDate = getDateValue( map.get("AddDate"));
                //点击数
                int hit = AppUtil.getint(map.get("ViewNum"));
                //内容
                String content = (String) map.get("Content");
                if(null !=content)
                   content = HotWordsCheckUtil.replaceAll(siteId, content);
                //图片


                BaseContentEO contentEO = new BaseContentEO();
                contentEO.setSiteId(siteId);
                contentEO.setColumnId(curColumdId);
                contentEO.setTypeCode(columnTypeCode);

                //导入标识
                contentEO.setEditor(imp_tag);
                contentEO.setIsPublish(isPublish);

                //导入数据
                contentEO.setTitle(title);
                contentEO.setSubTitle(subTitle);
                contentEO.setAuthor(author);
                contentEO.setResources(resources);
                contentEO.setPublishDate(publishDate);
                //标题新闻
                contentEO.setIsTitle(1);
                if(null !=pic && IsImg==1 && !"".equals(pic.trim())){
                    contentEO.setImageLink(pic);
                }
                //摘要
                contentEO.setRemarks(remark);
                //点击数
                contentEO.setHit(new Long(hit));
                //保存
                baseContentService.saveArticleNews(contentEO,content,null,null,null,null);
            }
        }

    }
}
