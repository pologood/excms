package cn.lonsun.staticcenter.exproject.ahszjj;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.staticcenter.util.JdbcUtils;
import cn.lonsun.util.HotWordsCheckUtil;

import java.util.*;

/**
 * Created by 1960274114 on 2016-10-13.
 * 综合信息-->质量工程
 */
public class SyncArticleNews extends AbSyncInfo{

    public SyncArticleNews(JdbcUtils jdbcUtils, Long siteId, Long createUserId, Long curColumdId) {
        super(jdbcUtils, siteId, createUserId, curColumdId);
    }
    public SyncArticleNews(JdbcUtils jdbcUtils, Long siteId) {
        super(jdbcUtils, siteId, null, null);
    }


    /**
     * 质量品牌栏目列表
     * @return
     */
    public List<OldSiteColumnPairVO> getOldColumnList(){
        List<OldSiteColumnPairVO> ret = new ArrayList<OldSiteColumnPairVO>();
        String sql = "select id,classname,followid from SYS_Class";
        List<Object> list =  jdbcUtils.excuteQuery(sql, null);
        if(null !=list && list.size()>0){
            for(Object obj:list){
                Map<String, Object> map = (HashMap<String, Object>) obj;
                String code = map.get("id") + "~" + map.get("followid");
                OldSiteColumnPairVO vo = new OldSiteColumnPairVO(code,(String)map.get("classname"));
                ret.add(vo);
            }
        }
        return ret;
    }

    private List<Object> getSysNewsList(String classId,String followid){
        String sql = "select id,sys_title,sys_content,sys_author,sys_idate,sys_ly,sys_pic from sys_news where sys_sid=?";
        return  jdbcUtils.excuteQuery(sql, new String[]{classId});
    }

    public void imp(String classCode,String columnTypeCode){

        String[] classCodeArgs = classCode.split("~");
        String classId = classCodeArgs[0];
        String followid = classCodeArgs[1];
        List<Object> newsList = getSysNewsList(classId,followid);
        if(null !=newsList && newsList.size()>0){
            IBaseContentService baseContentService = SpringContextHolder.getBean(IBaseContentService.class);
            for(Object obj:newsList){
                if(step>limitSize){
                    break;
                }
                step ++;
                Map<String, Object> map = (HashMap<String, Object>) obj;
                //标题
                String title =(String) map.get("sys_title");
                //副标题
                String subTitle = null;
                //作者
                String author = (String) map.get("sys_author");
                //来源
                String resources = (String) map.get("sys_ly");
                //图片
                String pic = (String)map.get("sys_pic");
                //视频
               // String video = (String)map.get("sys_videourl");
                //发布日期
                Date publishDate = getDateValue( map.get("sys_idate"));
                //内容
                String content = (String) map.get("sys_content");
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


                //保存
                baseContentService.saveArticleNews(contentEO,content,null,null,null,null);
            }
        }

    }
}
